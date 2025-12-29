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
        String dbUser = (String) session.getAttribute("DB_USER");
        String dbPass = (String) session.getAttribute("DB_PASS");
        String role = (String) session.getAttribute("CURRENT_ROLE");

        if (dbUser != null && role != null) {
            // Ми встановлюємо і креди, і РОЛЬ, щоб RoutingDataSource вибрав правильний пул
            DbContextHolder.setCredentials(dbUser, dbPass, role);

            // Додай цей рядок для дебагу в консоль IntelliJ - ти побачиш, чи перемикається роль
            System.out.println(">>> [ROUTING] Request: " + request.getRequestURI() + " | Role: " + role);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Обов'язково очищаємо контекст після завершення запиту, щоб не було витоку даних
        DbContextHolder.clear();
    }
}