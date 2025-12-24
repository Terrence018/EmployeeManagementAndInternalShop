package com.empmall.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    //  1.密鑰
    private static final String SECRET = "empmall-empmall-empmall-empmall-123456";

    // 2.過期時間：12 小時（毫秒）
    private static final long EXPIRE_TIME = 12 * 60 * 60 * 1000;

    // 3.生成 JWT 的方法
    public static String generateToken(Map<String, Object> claims) {

        Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

        return Jwts.builder()
                .setClaims(claims)   // 放自訂資料（id, username）
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME)) // 12小時
                .signWith(key)       // 使用 HS256 + SECRET 簽名
                .compact();          // 生成 Token
    }

    // 4.解析 JWT 的方法
    public static Claims parseToken(String token) throws Exception {

        Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
