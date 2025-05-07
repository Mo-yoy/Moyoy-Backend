package com.moyo.backend.security.jwt.domain;

import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Table(name = "jwt_white_list")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtWhiteList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tokenType;

    @Column(length = 500, nullable = false, unique = true)
    private String value;

    private LocalDateTime expiresAt;

    @Builder(access = AccessLevel.PRIVATE)
    private JwtWhiteList(String  tokenType, String value, LocalDateTime expiresAt) {
        this.tokenType = tokenType;
        this.value = value;
        this.expiresAt = expiresAt;
    }

    public static JwtWhiteList from(JWTClaimsSet claimsSet, String jwtRefreshToken){

        return JwtWhiteList.builder()
                .tokenType(claimsSet.getClaim("type").toString())
                .value(jwtRefreshToken)
                .expiresAt(LocalDateTime.ofInstant(((Date) claimsSet.getClaim("exp")).toInstant(),ZoneId.systemDefault()))
                .build();
    }

}