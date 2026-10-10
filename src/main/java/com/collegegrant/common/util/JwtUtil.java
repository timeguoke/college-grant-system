package com.collegegrant.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
    // 密钥（随便写一串字符，越长越安全）
    private static final String SECRET = "college_grant_system_secret_key_2026";
    // 过期时间（比如24小时）
    private static final long EXPIRE = 24 * 60 * 60 * 1000;

    // 生成token
    public static String createToken(Long userId, String username, Integer role) {
        return Jwts.builder()
                .setId(userId != null ? userId.toString() : "0")  // 用户ID，admin默认0
                .setSubject(username)             // 用户名
                .claim("role", role)              // 自定义字段：角色
                .setIssuedAt(new Date())          // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))  // 过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET)  // 签名算法+密钥
                .compact();                       // 生成字符串
    }

    // 解析token
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}