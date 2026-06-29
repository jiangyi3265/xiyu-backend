package com.ruoyi.web.app.security;

import java.io.Serializable;

/**
 * C端会员登录身份（存于Redis）
 *
 * @author liwangfu
 */
public class AppLoginUser implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long memberId;
    private String phone;
    private String name;
    private String token;
    private Long loginTime;

    public AppLoginUser()
    {
    }

    public AppLoginUser(Long memberId, String phone, String name, String token, Long loginTime)
    {
        this.memberId = memberId;
        this.phone = phone;
        this.name = name;
        this.token = token;
        this.loginTime = loginTime;
    }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getLoginTime() { return loginTime; }
    public void setLoginTime(Long loginTime) { this.loginTime = loginTime; }
}
