package com.grade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.grade.entity.User;
import com.grade.mapper.UserMapper;
import com.grade.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username).eq(User::getStatus, 1);
        User user = userMapper.selectOne(wrapper);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public String generateToken(User user) {
        return jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
    }

    public void updateLastLogin(Long userId) {
        User user = new User();
        user.setUserId(userId);
        user.setLastLogin(new Date());
        userMapper.updateById(user);
    }
}