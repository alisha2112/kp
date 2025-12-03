package com.hotel.system.config.web;

import com.hotel.system.config.routing.DbContextHolder;
import com.hotel.system.config.routing.DbRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession();
        // Отримуємо роль із сесії (якщо є), інакше - GUEST
        String roleName = (String) session.getAttribute("CURRENT_ROLE");

        DbRole role = DbRole.GUEST;
        if (roleName != null) {
            try {
                role = DbRole.valueOf(roleName);
            } catch (IllegalArgumentException e) {
                // Ігноруємо помилку, залишаємо GUEST
            }
        }

        // Встановлюємо роль у контекст БД
        DbContextHolder.setCurrentRole(role);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Очищаємо контекст після запиту
        DbContextHolder.clear();
    }
}