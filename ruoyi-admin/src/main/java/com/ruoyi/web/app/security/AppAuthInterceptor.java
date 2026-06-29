package com.ruoyi.web.app.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ServletUtils;

/**
 * C端会员鉴权拦截器（仅作用于 /app/member/**）
 *
 * @author liwangfu
 */
@Component
public class AppAuthInterceptor implements HandlerInterceptor
{
    @Autowired
    private AppTokenService appTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {
        AppLoginUser loginUser = appTokenService.getLoginUser(request);
        if (loginUser == null)
        {
            ServletUtils.renderString(response,
                    JSON.toJSONString(AjaxResult.error(HttpStatus.UNAUTHORIZED, "登录状态已过期，请重新登录")));
            return false;
        }
        AppUserContext.set(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
    {
        AppUserContext.remove();
    }
}
