package com.grade.util;

public class UserContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();
    private static final ThreadLocal<Long> STUDENT_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> TEACHER_ID = new ThreadLocal<>();

    public static void setUserId(Long id) { USER_ID.set(id); }
    public static Long getUserId() { return USER_ID.get(); }
    public static void setRole(String r) { ROLE.set(r); }
    public static String getRole() { return ROLE.get(); }
    public static void setStudentId(Long id) { STUDENT_ID.set(id); }
    public static Long getStudentId() { return STUDENT_ID.get(); }
    public static void setTeacherId(Long id) { TEACHER_ID.set(id); }
    public static Long getTeacherId() { return TEACHER_ID.get(); }
    public static void clear() {
        USER_ID.remove(); ROLE.remove(); STUDENT_ID.remove(); TEACHER_ID.remove();
    }
}