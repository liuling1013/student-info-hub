package com.grade.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.grade.entity.Student;
import com.grade.entity.Teacher;
import com.grade.entity.User;
import com.grade.mapper.StudentMapper;
import com.grade.mapper.TeacherMapper;
import com.grade.service.UserService;
import com.grade.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private TeacherMapper teacherMapper;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        User user = userService.login(username, password);

        if (user == null) {
            return Result.error("账号或密码错误");
        }
        if (user.getStatus() != 1) {
            return Result.error("账号已被禁用");
        }

        String token = userService.generateToken(user);
        userService.updateLastLogin(user.getUserId());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getUserId());
        data.put("role", user.getRole());

        if ("student".equals(user.getRole())) {
            Student student = studentMapper.selectOne(
                    new LambdaQueryWrapper<Student>().eq(Student::getUserId, user.getUserId()));
            if (student != null) {
                data.put("studentId", student.getStudentId());
            }
        }
        if ("teacher".equals(user.getRole())) {
            Teacher teacher = teacherMapper.selectOne(
                    new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, user.getUserId()));
            if (teacher != null) {
                data.put("teacherId", teacher.getTeacherId());
            }
        }
        return Result.success(data);
    }
}