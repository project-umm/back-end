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
@Table(name = "dm_chats")
public class DmChat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false, updatable = false)
    private UUID id;

    @Column(name = "dm_id", nullable = false)
    private UUID dmId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "chat_message", nullable = false)
    private String chatMessage;

}
