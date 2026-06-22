package com.grade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private String username;
    private String password;
    private String role;
    private String avatar;
    private Date lastLogin;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}