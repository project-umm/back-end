package com.umm.app.repository;

import com.umm.app.entity.User;
import com.umm.app.entity.UserAskFriend;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAskFriendRepository extends JpaRepository<UserAskFriend, UUID> {

    Optional<UserAskFriend> findById(@NotNull UUID id);
    List<UserAskFriend> findAllByFriendAndIsPending(User friend, Boolean isPending);
    Optional<UserAskFriend> findByUserAndFriend(User user, User Friend);
}
