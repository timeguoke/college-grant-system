package com.collegegrant.common.context;

/**
 * 用户上下文：存储当前登录用户的信息（基于ThreadLocal）
 * 在拦截器中设置，Controller/Service 中直接获取
 */
public class UserContext {

    /** 当前用户ID */
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    /** 当前用户名 */
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();

    /** 当前用户角色 */
    private static final ThreadLocal<Integer> ROLE = new ThreadLocal<>();

    /**
     * 设置用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return USER_ID.get();
    }

    /**
     * 设置用户名
     */
    public static void setUsername(String username) {
        USERNAME.set(username);
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        return USERNAME.get();
    }

    /**
     * 设置用户角色
     */
    public static void setRole(Integer role) {
        ROLE.set(role);
    }

    /**
     * 获取用户角色
     */
    public static Integer getRole() {
        return ROLE.get();
    }

    /**
     * 清空当前线程的用户信息（必须在请求结束后调用，防止内存泄漏）
     */
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        ROLE.remove();
    }
}
