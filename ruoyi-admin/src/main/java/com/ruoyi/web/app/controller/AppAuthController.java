package com.ruoyi.web.app.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.hotel.domain.LwfMember;
import com.ruoyi.hotel.service.ILwfMemberService;
import com.ruoyi.web.app.security.AppTokenService;
import com.ruoyi.web.app.service.AppBizService;

/**
 * C端会员登录/注册
 *
 * @author liwangfu
 */
@Anonymous
@RestController
@RequestMapping("/app/auth")
public class AppAuthController extends BaseController
{
    @Autowired
    private ILwfMemberService memberService;
    @Autowired
    private AppTokenService appTokenService;
    @Autowired
    private AppBizService appBizService;

    /** 登录 */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody Map<String, String> body)
    {
        String phone = body.get("phone");
        String password = body.get("password");
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password))
        {
            return error("请输入手机号和密码");
        }
        LwfMember member = memberService.selectLwfMemberByPhone(phone);
        if (member == null)
        {
            return error("账号不存在，请先注册");
        }
        if (!"0".equals(member.getStatus()))
        {
            return error("账号已被停用");
        }
        if (!SecurityUtils.matchesPassword(password, member.getPassword()))
        {
            return error("密码错误");
        }
        String token = appTokenService.createToken(member);
        AjaxResult ajax = success();
        ajax.put("token", token);
        ajax.put("member", member);
        return ajax;
    }

    /** 微信一键登录：前端 uni.login 拿 code，可带昵称/头像 */
    @PostMapping("/wxlogin")
    public AjaxResult wxlogin(@RequestBody Map<String, String> body)
    {
        String code = body.get("code");
        LwfMember member = appBizService.wxLogin(code, body.get("nickName"), body.get("avatarUrl"));
        String token = appTokenService.createToken(member);
        AjaxResult ajax = success();
        ajax.put("token", token);
        ajax.put("member", member);
        return ajax;
    }

    /** 注册 */
    @PostMapping("/register")
    public AjaxResult register(@RequestBody Map<String, String> body)
    {
        String phone = body.get("phone");
        String password = body.get("password");
        String name = body.get("name");
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password))
        {
            return error("请输入手机号和密码");
        }
        LwfMember member = appBizService.register(phone, password, name);
        String token = appTokenService.createToken(member);
        AjaxResult ajax = success();
        ajax.put("token", token);
        ajax.put("member", member);
        return ajax;
    }

    /** 退出登录 */
    @PostMapping("/logout")
    public AjaxResult logout(HttpServletRequest request)
    {
        appTokenService.removeToken(request);
        return success();
    }
}
