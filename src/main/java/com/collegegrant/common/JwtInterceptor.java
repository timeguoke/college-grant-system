package com.collegegrant.common;

import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从Header里取token
        String token = request.getHeader("Authorization");
        //判断token是否存在
        if (token == null || token.isEmpty()) {
            // 没带token，说明没登录
            response.setStatus(401);
            response.getWriter().write("未登录");
            return false;  // false=拦截，不让继续
        }

        //解析token
        try {
            Claims claims = JwtUtil.parseToken(token);
            // 解析成功，把用户信息存到request里，后面Controller可以用
            request.setAttribute("userId", claims.getId());
            request.setAttribute("username", claims.getSubject());
            request.setAttribute("role", claims.get("role"));
            return true;  // true=放行
        } catch (Exception e) {
            // 解析失败，token过期或伪造
            response.setStatus(401);
            response.getWriter().write("token已过期或无效");
            return false;  // 拦截
        }

    }
}
