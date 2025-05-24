package com.umm.app.repository;

import com.umm.app.entity.Dm;
import com.umm.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DmRepository extends JpaRepository<Dm, UUID> {

    List<Dm> findAllByUser(User user);
    Optional<Dm> findByUserAndFriend(User user, User friend);
}
