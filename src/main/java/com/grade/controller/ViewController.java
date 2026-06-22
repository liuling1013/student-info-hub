package com.grade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/login.html")
    public String login() { return "login"; }
    @GetMapping("/")
    public String root() { return "redirect:/login.html"; }
    @GetMapping("/admin/dashboard")
    public String adminDashboard() { return "admin/dashboard"; }
    @GetMapping("/admin/statistics")
    public String adminStatistics() { return "admin/statistics"; }
    @GetMapping("/admin/grade_summary")
    public String gradeSummary() { return "admin/grade_summary"; }
    @GetMapping("/teacher/dashboard")
    public String teacherDashboard() { return "teacher/dashboard"; }
    @GetMapping("/teacher/grade_input")
    public String teacherGradeInput() { return "teacher/grade_input"; }
    @GetMapping("/teacher/statistics")
    public String teacherStatistics() { return "teacher/statistics"; }
    @GetMapping("/student/course_list")
    public String studentCourseList() { return "student/course_list"; }
    @GetMapping("/student/grades")
    public String studentGrades() { return "student/grades"; }
}