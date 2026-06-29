package com.ruoyi.web.app.security;

import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.hotel.domain.LwfMember;

/**
 * C端会员令牌服务（基于Redis，与后台sys_user令牌体系隔离）
 *
 * @author liwangfu
 */
@Component
public class AppTokenService
{
    /** Redis令牌前缀 */
    private static final String TOKEN_PREFIX = "app_tokens:";

    /** 令牌有效期（分钟）= 7天 */
    private static final int EXPIRE_MINUTES = 7 * 24 * 60;

    @Autowired
    private RedisCache redisCache;

    /**
     * 创建令牌并缓存会员身份
     */
    public String createToken(LwfMember member)
    {
        String token = IdUtils.fastUUID();
        AppLoginUser loginUser = new AppLoginUser(member.getMemberId(), member.getPhone(), member.getName(), token,
                System.currentTimeMillis());
        redisCache.setCacheObject(TOKEN_PREFIX + token, loginUser, EXPIRE_MINUTES, TimeUnit.MINUTES);
        return token;
    }

    /**
     * 从请求获取登录会员，并续期
     */
    public AppLoginUser getLoginUser(HttpServletRequest request)
    {
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            AppLoginUser loginUser = redisCache.getCacheObject(TOKEN_PREFIX + token);
            if (loginUser != null)
            {
                // 访问续期
                redisCache.expire(TOKEN_PREFIX + token, EXPIRE_MINUTES, TimeUnit.MINUTES);
            }
            return loginUser;
        }
        return null;
    }

    /**
     * 注销令牌
     */
    public void removeToken(HttpServletRequest request)
    {
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            redisCache.deleteObject(TOKEN_PREFIX + token);
        }
    }

    /**
     * 解析请求头中的令牌（兼容 Bearer 前缀）
     */
    private String getToken(HttpServletRequest request)
    {
        String token = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(token) && token.startsWith("Bearer "))
        {
            token = token.substring(7);
        }
        return token;
    }
}
