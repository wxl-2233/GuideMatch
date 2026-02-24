package com.guide.service.impl;

import com.guide.entity.User;
import com.guide.mapper.UserMapper;
import com.guide.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getByNickname(String nickname) {
        return userMapper.findByNickname(nickname);
    }

    @Override
    public User getById(Integer id) {
        return userMapper.findById(id);
    }

    @Override
    public void createUser(User user) {
        userMapper.insert(user);
    }
}
