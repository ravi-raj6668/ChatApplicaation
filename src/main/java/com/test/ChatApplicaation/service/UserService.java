package com.test.ChatApplicaation.service;

import com.test.ChatApplicaation.entity.User;

import java.util.List;

public interface UserService {
    public User createUser(User user);

    public User getUserById(Integer id);

    List<User> getAllUsers();

    public void deleteUser(Integer usedId);

}
