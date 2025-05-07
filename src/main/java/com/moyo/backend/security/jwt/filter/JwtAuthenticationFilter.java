package com.moyo.backend.security.jwt.filter;

import com.moyo.backend.security.jwt.exception.JwtTokenBlockedException;
import com.moyo.backend.security.jwt.exception.JwtTokenExpiredException;
import com.moyo.backend.security.jwt.exception.JwtTokenInvalidException;
import com.moyo.backend.security.jwt.exception.JwtTokenTypeMismatchException;
import com.moyo.backend.security.jwt.repository.JwtWhiteListRepository;
import com.moyo.backend.security.oauth.GithubOAuth2User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
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

import static com.moyo.backend.common.constant.MoyoConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final OctetSequenceKey jwk;
    private final JwtWhiteListRepository jwtWhiteListRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(AUTHORIZATION);
        if(header == null){
            filterChain.doFilter(request,response);
            return;
        }

        if(!header.startsWith("Bearer ")) throw new JwtTokenInvalidException();

        String accessToken = header.replace("Bearer ", "");

        try {
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            MACVerifier macVerifier = new MACVerifier(jwk.toSecretKey());
            boolean verifyResult = signedJWT.verify(macVerifier);

            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();

            if(verifyResult){

                String type = jwtClaimsSet.getClaim("type").toString();
                if(!type.equals(JWT_ACCESS_TYPE)) throw new JwtTokenTypeMismatchException();
                if (jwtClaimsSet.getExpirationTime() != null && jwtClaimsSet.getExpirationTime().before(new Date())) throw new JwtTokenExpiredException();
                if(!jwtWhiteListRepository.existByTokenValue(accessToken)) throw new JwtTokenBlockedException();

                Long id = (Long)jwtClaimsSet.getClaim("id");
                String username = jwtClaimsSet.getClaim("username").toString();
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
                attributes.put("username", username);

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
