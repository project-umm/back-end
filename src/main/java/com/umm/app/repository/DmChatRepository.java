package com.umm.app.repository;

import com.umm.app.entity.DmChat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

public interface DmChatRepository extends JpaRepository<DmChat, UUID> {

    @Query("SELECT c FROM DmChat c WHERE c.dmId = :dmId AND c.key <= :key ORDER BY c.key ASC")
    List<DmChat> findDmChatNextPage(UUID dmId, BigInteger key, Pageable pageable);
}
