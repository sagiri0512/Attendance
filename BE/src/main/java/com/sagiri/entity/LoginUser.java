package com.sagiri.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security 用户主体
 *
 * <p>实现 {@link UserDetails} 接口，将 {@link Employee} 实体适配为
 * Spring Security 框架可识别的用户对象。角色值直接作为权限字符串使用。</p>
 *
 * @author sagiri
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser implements UserDetails {

    /** 员工实体 */
    private Employee employee;

    /**
     * 返回用户权限集合
     *
     * <p>将员工的 role 字段值（Byte）转为字符串，作为单一权限。</p>
     *
     * @return 包含单一 SimpleGrantedAuthority 的列表
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(employee.getRole().toString()));
    }

    /**
     * 返回加密后的密码
     *
     * @return BCrypt加密密码
     */
    @Override
    public String getPassword() {
        return employee.getPassword();
    }

    /**
     * 返回用户名（使用工号）
     *
     * @return 员工工号
     */
    @Override
    public String getUsername() {
        return employee.getEmpNo();
    }
}
