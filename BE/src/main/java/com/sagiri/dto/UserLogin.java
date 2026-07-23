package com.sagiri.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户登录请求DTO
 *
 * <p>接收前端登录表单提交的工号和密码。</p>
 *
 * @author sagiri
 */
@Getter
@Setter
public class UserLogin {

    /** 工号 */
    private String empNo;
    /** 密码（明文传输，服务端BCrypt比对） */
    private String password;
}
