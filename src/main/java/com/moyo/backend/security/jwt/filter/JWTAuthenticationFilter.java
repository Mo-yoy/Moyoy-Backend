package com.moyo.backend.security.jwt.filter;

import com.moyo.backend.domain.user.Role;
import com.moyo.backend.security.jwt.util.JwtPayloadReader;
import com.moyo.backend.security.jwt.util.JwtValidator;
import com.moyo.backend.security.oauth.dto.GitHubOAuth2User;
import com.moyo.backend.security.oauth.dto.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.moyo.backend.common.constant.MoyoConstants.*;


@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;
    private final JwtPayloadReader jwtPayloadReader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if(authorizationHeader == null){

            log.info("{} 헤더가 존재하지 않음", AUTHORIZATION);
            filterChain.doFilter(request,response);
            return;
        }

        if(!authorizationHeader.startsWith(BEARER)) throw new RuntimeException("Bearer 토큰이 아닌 오류 발생");

        String accessToken = authorizationHeader.substring(7);

        // 추출한 jwt AccessToken JWT Validator로 검증
        jwtValidator.validateJwtAccessToken(accessToken);

        String providerId = jwtPayloadReader.getProviderId(accessToken);
        Role role = Role.valueOf(jwtPayloadReader.getRole(accessToken).substring(5));
        UserDto userDto = new UserDto(role,providerId);

        GitHubOAuth2User gitHubOAuth2User = new GitHubOAuth2User(userDto);

        OAuth2AuthenticationToken authenticationToken =
                new OAuth2AuthenticationToken(gitHubOAuth2User,gitHubOAuth2User.getAuthorities(),GITHUB_REGISTRATION_ID);

        
        // 액세스 토큰으로 인증 처리
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request,response);
    }
}