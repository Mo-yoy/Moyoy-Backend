package com.moyo.backend.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String profileImgUrl;

    @Builder
    public User(String name, String profileImgUrl){
        this.name = name;
        this.profileImgUrl = profileImgUrl;
    }
}

