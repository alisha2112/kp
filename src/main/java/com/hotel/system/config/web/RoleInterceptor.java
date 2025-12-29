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

        // Отримуємо реальні логін та пароль СУБД, які ми зберегли в AuthController
        String dbUser = (String) session.getAttribute("DB_USER");
        String dbPass = (String) session.getAttribute("DB_PASS");

        // Якщо користувач не залогінений (наприклад, на головній сторінці),
        // ми можемо залишити дані порожніми (DataSource сам підставить гостя)
        // або встановити дані гостя явно тут.
        if (dbUser != null && dbPass != null) {
            DbContextHolder.setCredentials(dbUser, dbPass);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Обов'язково очищаємо контекст після завершення запиту, щоб не було витоку даних
        DbContextHolder.clear();
    }
}
//
//import com.hotel.system.config.routing.DbContextHolder;
//import com.hotel.system.config.routing.DbRole;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//public class RoleInterceptor implements HandlerInterceptor {
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        HttpSession session = request.getSession();
//        String user = (String) session.getAttribute("DB_USER");
//        String pass = (String) session.getAttribute("DB_PASS");
//
//        DbContextHolder.setCredentials(user, pass);
//        return true;
//    }
//
////    @Override
////    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
////        HttpSession session = request.getSession();
////        String roleName = (String) session.getAttribute("CURRENT_ROLE");
////
////        DbRole role = DbRole.GUEST;
////        if (roleName != null) {
////            try {
////                role = DbRole.valueOf(roleName);
////            } catch (IllegalArgumentException e) { }
////        }
////
////        // ДОДАЙТЕ ЦЕЙ РЯДОК:
////        System.out.println(">>> [SECURITY] Request URI: " + request.getRequestURI() + " | Switching DB Connection to Role: " + role);
////
////        DbContextHolder.setCurrentRole(role);
////        return true;
//    }
//
////    @Override
////    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
////        HttpSession session = request.getSession();
////        // Отримуємо роль із сесії (якщо є), інакше - GUEST
////        String roleName = (String) session.getAttribute("CURRENT_ROLE");
////
////        DbRole role = DbRole.GUEST;
////        if (roleName != null) {
////            try {
////                role = DbRole.valueOf(roleName);
////            } catch (IllegalArgumentException e) {
////                // Ігноруємо помилку, залишаємо GUEST
////            }
////        }
////
////        // Встановлюємо роль у контекст БД
////        DbContextHolder.setCurrentRole(role);
////        return true;
////    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//        // Очищаємо контекст після запиту
//        DbContextHolder.clear();
//    }
//}