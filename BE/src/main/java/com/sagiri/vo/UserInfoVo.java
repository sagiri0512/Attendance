package com.sagiri.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 当前用户信息视图对象
 *
 * <p>登录成功后返回给前端的用户基本信息，
 * 包含身份标识和组织归属，供前端展示和权限控制。</p>
 *
 * @author sagiri
 */
@Getter
@Setter
public class UserInfoVo {

    /** 真实姓名 */
    private String username;
    /** 工号 */
    private String empNo;
    /** 角色：0-员工 1-PL 2-PM 3-HR 4-管理员 */
    private Byte role;
    /** 所属项目组长姓名 */
    private String PLName;
    /** 所属项目经理姓名 */
    private String PMName;
}
