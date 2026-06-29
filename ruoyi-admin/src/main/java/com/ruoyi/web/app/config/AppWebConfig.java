package com.ruoyi.web.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.ruoyi.web.app.security.AppAuthInterceptor;

/**
 * C端会员鉴权拦截器注册：仅拦截 /app/member/**，其余 /app/** 公开
 *
 * @author liwangfu
 */
@Configuration
public class AppWebConfig implements WebMvcConfigurer
{
    @Autowired
    private AppAuthInterceptor appAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(appAuthInterceptor).addPathPatterns("/app/member/**");
    }
}
