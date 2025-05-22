package com.umm.app.repository;

import com.umm.app.entity.User;
import com.umm.app.entity.UserAskFriend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAskFriendRepository extends JpaRepository<UserAskFriend, UUID> {

    Optional<UserAskFriend> findByUserAndFriend(User user, User Friend);
}
