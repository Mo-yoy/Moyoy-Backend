package com.moyo.backend.security.jwt.filter;

import com.moyo.backend.security.jwt.exception.JwtTokenExpiredException;
import com.moyo.backend.security.jwt.exception.JwtTokenInvalidException;
import com.moyo.backend.security.jwt.exception.JwtTokenTypeMismatchException;
import com.moyo.backend.security.oauth.GithubOAuth2User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.moyo.backend.common.constant.MoyoConstants.AUTHORIZATION;
import static com.moyo.backend.common.constant.MoyoConstants.JWT_ACCESS_TYPE;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final MACVerifier macVerifier;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (requestURI.equals("/auth/login/github") || requestURI.equals("/login/oauth2/code/github")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(AUTHORIZATION);
        if(header == null){
            filterChain.doFilter(request,response);
            return;
        }

        if(!header.startsWith("Bearer ")) throw new JwtTokenInvalidException();
        String accessToken = header.replace("Bearer ", "");

        try {
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            boolean verifyResult = signedJWT.verify(macVerifier);
            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();

            if(verifyResult){
                String type = jwtClaimsSet.getClaim("type").toString();

                if(type != null && !type.equals(JWT_ACCESS_TYPE)) throw new JwtTokenTypeMismatchException();
                if (jwtClaimsSet.getExpirationTime() != null && jwtClaimsSet.getExpirationTime().before(new Date())) throw new JwtTokenExpiredException();

                Long id = (Long)jwtClaimsSet.getClaim("id");
                Object rawAuthority = jwtClaimsSet.getClaim("authority");

                Set<GrantedAuthority> authorities =
                        ((List<?>) rawAuthority).stream()
                                .filter(Objects::nonNull)
                                .filter(Map.class::isInstance)
                                .map(Map.class::cast)
                                .map(map -> map.get("role"))
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toSet());

                Map<String, Object> attributes = new HashMap<>();
                attributes.put("id", id);

                GithubOAuth2User userPrincipal = new GithubOAuth2User(authorities, attributes);
                Authentication authentication = new OAuth2AuthenticationToken(userPrincipal, userPrincipal.getAuthorities(), "github");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            else throw new JwtTokenInvalidException();

        } catch (ParseException | JOSEException e) {
            log.error("JWT 인증 필터에서 에러 발생 : {}", e.getMessage());
            throw new JwtTokenInvalidException();
        }

        filterChain.doFilter(request,response);
    }
}
