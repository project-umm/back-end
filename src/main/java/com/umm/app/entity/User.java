package com.umm.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false, updatable = false)
    private UUID id;

    @Column(unique = true, nullable = false, updatable = false)
    private String username;

    private String nickname;
    private String password;
    private String email;

    @Column(name = "is_active")
    private Boolean isActive;
    private String role;
    @Column(name = "profile_url")
    private String profileUrl;
    @Column(name = "phone_number")
    private String phoneNumber;
}
