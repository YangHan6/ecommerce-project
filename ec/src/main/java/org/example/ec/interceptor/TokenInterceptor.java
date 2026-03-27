package org.example.ec.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if ("/api/login".equals(request.getRequestURI())){
            return true;
        }
        if("/api/register".equals(request.getRequestURI())){
            return true;
        }
        if ("/api/sendCode".equals(request.getRequestURI())){
            return true;
        }
        //放行图片
        if (request.getRequestURI().startsWith("/images/")) return true;
        //放行websocket握手
        if (request.getRequestURI().startsWith("/ws/")) return true;
        // 3) 取 token（前端放在 Authorization）
        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()) {
            response.setStatus(401);
            return false;
        }

        //从redis里查token
        String userId = redisTemplate.opsForValue().get("token:"+ token);
        if (userId == null) {
            response.setStatus(401);
            return false;
        }

        return true; // 放行
    }
}