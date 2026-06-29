package com.ruoyi.web.app.security;

/**
 * 当前C端会员上下文（ThreadLocal）
 *
 * @author liwangfu
 */
public class AppUserContext
{
    private static final ThreadLocal<AppLoginUser> HOLDER = new ThreadLocal<>();

    public static void set(AppLoginUser user)
    {
        HOLDER.set(user);
    }

    public static AppLoginUser get()
    {
        return HOLDER.get();
    }

    public static Long getMemberId()
    {
        AppLoginUser user = HOLDER.get();
        return user == null ? null : user.getMemberId();
    }

    public static void remove()
    {
        HOLDER.remove();
    }
}
