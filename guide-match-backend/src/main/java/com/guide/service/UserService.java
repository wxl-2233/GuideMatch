package com.guide.service;

import com.guide.entity.User;

public interface UserService {

    User getByNickname(String nickname);

    User getById(Integer id);

    void createUser(User user);
}
