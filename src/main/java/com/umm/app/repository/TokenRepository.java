package com.umm.app.repository;

import com.umm.app.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByRefresh(String refresh);
}
