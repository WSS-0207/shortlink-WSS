package org.myproject.shortlink.admin.common.constant;


/*
* 后管redis缓存常量类
* */
public class RedisCacheConstant {
    // 用户注册分布式锁
    public static final String LOCK_USER_REGISTER_KEY = "short-link:user-register_lock:";
    //用户登录
    public static final String USER_LOGIN_KEY = "short-link:user-login:";

    // 分组创建分布式锁
    public static final String LOCK_GROUP_CREATE_KEY = "short-link:lock_group-create:%s";
}
