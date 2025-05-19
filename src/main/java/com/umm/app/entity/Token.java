package com.umm.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "tokens")
public class Token extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, updatable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String refresh;
    @Column(name = "last_login_location")
    private String lastLoginLocation;
    @Column(name = "expire_at")
    private Date expireAt;
    @Column(name = "is_expired")
    private Boolean isExpired;

}
