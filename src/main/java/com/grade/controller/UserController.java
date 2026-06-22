package com.grade.controller;

import com.grade.entity.User;
import com.grade.service.UserService;
import com.grade.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestParam String username, @RequestParam String password) {
        User user = userService.login(username, password);
        if (user == null) {
            return Result.error("账号或密码错误");
        }
        return Result.success(user);
    }
}