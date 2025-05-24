package com.umm.app.repository;

import com.umm.app.entity.User;
import com.umm.app.entity.UserFriend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserFriendRepository extends JpaRepository<UserFriend, UUID> {

    List<UserFriend> findAllByUser(User user);
}
