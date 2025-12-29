package com.hotel.system.config.web;

import com.hotel.system.config.routing.DbContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession();
        String dbUser = (String) session.getAttribute("DB_USER"); // Беремо реальне ім'я (app_admin_user)
        String dbPass = (String) session.getAttribute("DB_PASS");

        if (dbUser != null) {
            // Встановлюємо саме dbUser як ключ для DataSource
            DbContextHolder.setCredentials(dbUser, dbPass, dbUser);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Обов'язково очищаємо контекст після завершення запиту, щоб не було витоку даних
        DbContextHolder.clear();
    }
}