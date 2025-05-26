package com.umm.app.repository;

import com.umm.app.entity.DmChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DmChatRepository extends JpaRepository<DmChat, UUID> {
}
