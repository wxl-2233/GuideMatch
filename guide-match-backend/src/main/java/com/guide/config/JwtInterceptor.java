package com.guide.config;

import com.guide.util.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 跨域预检请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        String requestURI = request.getRequestURI();
        
        // 获取Token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        boolean isPublicRoute = requestURI.equals("/api/posts/list") || 
            requestURI.matches("/api/posts/\\d+/view") ||
            requestURI.equals("/api/guides/list") ||
            requestURI.matches("/api/guides/\\d+");
        
        if (token != null && jwtUtil.validateToken(token)) {
            Integer userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            request.setAttribute("userId", userId);
            request.setAttribute("role", role);
            
            if (requestURI.startsWith("/api/admin") && !"admin".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
            
            return true;
        }

        if (isPublicRoute) {
            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
