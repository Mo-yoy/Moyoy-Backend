package com.moyo.backend.security.jwt.service;

import com.moyo.backend.security.jwt.MacSecuritySigner;
import com.moyo.backend.security.jwt.domain.JwtWhiteList;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.moyo.backend.common.constant.MoyoConstants.JWT_ACCESS_TYPE;
import static com.moyo.backend.common.constant.MoyoConstants.JWT_REFRESH_TYPE;

@Service
@Transactional
@RequiredArgsConstructor
public class JwtReissueService {

    private final OctetSequenceKey jwk;
    private final MacSecuritySigner macSecuritySigner;
    private final JwtWhiteListRepository jwtWhiteListRepository;

    public Map<String, String> reIssueJwt(String jwtRefreshToken) {

        String reissueAccess;
        String reissueRefresh;

        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtRefreshToken);
            MACVerifier macVerifier = new MACVerifier(jwk.toSecretKey());
            boolean verifyResult = signedJWT.verify(macVerifier);

            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();

            if (verifyResult) {

                String type = jwtClaimsSet.getClaim("type").toString();
                if(!type.equals(JWT_REFRESH_TYPE)) throw new JwtTokenTypeMismatchException();

                if (jwtClaimsSet.getExpirationTime() != null && jwtClaimsSet.getExpirationTime().before(new Date())) throw new JwtTokenExpiredException();
                if(!jwtWhiteListRepository.existByTokenValue(jwtRefreshToken)) throw new JwtTokenBlockedException();

                String id = jwtClaimsSet.getClaim("id").toString();
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

                GithubOAuth2User user = new GithubOAuth2User(authorities, attributes);

                reissueRefresh = macSecuritySigner.getJwtToken(user, jwk, JWT_REFRESH_TYPE);
                reissueAccess = macSecuritySigner.getJwtToken(user, jwk, JWT_ACCESS_TYPE);

                jwtWhiteListRepository.deleteByTokenValue(jwtRefreshToken);

                JwtWhiteList reissueRefreshWhiteList = JwtWhiteList.from(SignedJWT.parse(reissueRefresh).getJWTClaimsSet(), reissueRefresh);
                JwtWhiteList reissueAccessWhiteList = JwtWhiteList.from(SignedJWT.parse(reissueAccess).getJWTClaimsSet(), reissueAccess);

                jwtWhiteListRepository.save(reissueRefreshWhiteList);
                jwtWhiteListRepository.save(reissueAccessWhiteList);
            }
            else throw new JwtTokenInvalidException();

        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }

        return Map.of(JWT_ACCESS_TYPE, reissueAccess, JWT_REFRESH_TYPE, reissueRefresh);
    }
}
