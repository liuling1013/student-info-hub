package com.grade.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.grade.dto.StudentImportDto;
import com.grade.entity.*;
import com.grade.mapper.*;
import com.grade.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseOfferMapper courseOfferMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ==================== 学生管理 ====================
    @GetMapping("/students")
    public Result listStudents() {
        return Result.success(studentMapper.selectList(null));
    }

    @PostMapping("/student")
    public Result addStudent(@RequestBody Student student) {
        User existUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, student.getStudentNo()));
        if (existUser != null) {
            return Result.error("该学号已存在账号，无法重复添加");
        }
        User user = new User();
        user.setUsername(student.getStudentNo());
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole("student");
        user.setStatus(1);
        user.setCreatedAt(new Date());
        userMapper.insert(user);
        student.setUserId(user.getUserId());
        studentMapper.insert(student);
        return Result.success("添加成功");
    }

    @PutMapping("/student")
    public Result updateStudent(@RequestBody Student student) {
        studentMapper.updateById(student);
        return Result.success("更新成功");
    }

    @DeleteMapping("/student/delete")
    public Result deleteStudent(@RequestParam Long studentId) {
        Student s = studentMapper.selectById(studentId);
        if (s != null) userMapper.deleteById(s.getUserId());
        studentMapper.deleteById(studentId);
        return Result.success("删除成功");
    }

    @PostMapping("/student/batchImport")
    public Result batchImportStudents(@RequestParam("file") MultipartFile file) {
        try {
            List<StudentImportDto> importList = EasyExcel.read(file.getInputStream())
                    .head(StudentImportDto.class).sheet().doReadSync();
            int insertCount = 0, updateCount = 0;

            for (StudentImportDto dto : importList) {
                if (dto.getStudentNo() == null || dto.getStudentNo().trim().isEmpty()) continue;
                Student existStudent = studentMapper.selectOne(
                        new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, dto.getStudentNo()));
                if (existStudent != null) {
                    existStudent.setName(dto.getName());
                    existStudent.setClassName(dto.getClassName());
                    existStudent.setPhone(dto.getPhone());
                    existStudent.setEmail(dto.getEmail());
                    studentMapper.updateById(existStudent);
                    updateCount++;
                } else {
                    User existUser = userMapper.selectOne(
                            new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getStudentNo()));
                    User user;
                    if (existUser != null) {
                        user = existUser;
                    } else {
                        user = new User();
                        user.setUsername(dto.getStudentNo());
                        user.setPassword(passwordEncoder.encode("123456"));
                        user.setRole("student");
                        user.setStatus(1);
                        user.setCreatedAt(new Date());
                        userMapper.insert(user);
                    }
                    Student student = new Student();
                    student.setUserId(user.getUserId());
                    student.setStudentNo(dto.getStudentNo());
                    student.setName(dto.getName());
                    student.setClassName(dto.getClassName());
                    student.setPhone(dto.getPhone());
                    student.setEmail(dto.getEmail());
                    studentMapper.insert(student);
                    insertCount++;
                }
            }
            return Result.success("导入完成：新增 " + insertCount + " 条，更新 " + updateCount + " 条");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("导入失败：" + e.getMessage());
        }
    }

    // ==================== 教师管理 ====================
    @GetMapping("/teachers")
    public Result listTeachers() {
        return Result.success(teacherMapper.selectList(null));
    }

    @PostMapping("/teacher")
    public Result addTeacher(@RequestBody Teacher teacher) {
        // 新增教师同步创建用户账号
        User existUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, teacher.getTeacherNo()));
        if (existUser != null) {
            return Result.error("该工号已存在账号，无法重复添加");
        }
        User user = new User();
        user.setUsername(teacher.getTeacherNo());
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole("teacher");
        user.setStatus(1);
        user.setCreatedAt(new Date());
        userMapper.insert(user);
        teacher.setUserId(user.getUserId());
        teacherMapper.insert(teacher);
        return Result.success("添加成功");
    }

    @PutMapping("/teacher")
    public Result updateTeacher(@RequestBody Teacher teacher) {
        teacherMapper.updateById(teacher);
        return Result.success("更新成功");
    }

    @DeleteMapping("/teacher/delete")
    public Result deleteTeacher(@RequestParam Long teacherId) {
        Teacher t = teacherMapper.selectById(teacherId);
        if (t != null) userMapper.deleteById(t.getUserId());
        teacherMapper.deleteById(teacherId);
        return Result.success("删除成功");
    }

    // ==================== 课程管理 ====================
    @GetMapping("/courses")
    public Result listCourses() {
        return Result.success(courseMapper.selectList(null));
    }

    @PostMapping("/course")
    public Result addCourse(@RequestBody Course course) {
        courseMapper.insert(course);
        return Result.success("添加成功");
    }

    @PutMapping("/course")
    public Result updateCourse(@RequestBody Course course) {
        courseMapper.updateById(course);
        return Result.success("更新成功");
    }

    @DeleteMapping("/course/delete")
    public Result deleteCourse(@RequestParam Long courseId) {
        courseMapper.deleteById(courseId);
        return Result.success("删除成功");
    }

    // ==================== 开课管理 ====================
    @GetMapping("/offers")
    public Result listOffers() {
        List<CourseOffer> list = courseOfferMapper.selectList(null);
        for (CourseOffer offer : list) {
            Course course = courseMapper.selectById(offer.getCourseId());
            if (course != null) offer.setCourseName(course.getCourseName());
            Teacher teacher = teacherMapper.selectById(offer.getTeacherId());
            if (teacher != null) offer.setTeacherName(teacher.getName());
        }
        return Result.success(list);
    }

    @PostMapping("/offer")
    public Result addOffer(@RequestBody CourseOffer offer) {
        offer.setEnrolledCount(0);
        offer.setStatus(1);
        courseOfferMapper.insert(offer);
        return Result.success("添加成功");
    }

    @PutMapping("/offer")
    public Result updateOffer(@RequestBody CourseOffer offer) {
        courseOfferMapper.updateById(offer);
        return Result.success("更新成功");
    }

    @DeleteMapping("/offer/delete")
    public Result deleteOffer(@RequestParam Long offerId) {
        courseOfferMapper.deleteById(offerId);
        return Result.success("删除成功");
    }

    @GetMapping("/users")
    public Result listUsers() {
        return Result.success(userMapper.selectList(null));
    }
}