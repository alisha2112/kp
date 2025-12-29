//package com.hotel.system.controller;
//
//import com.hotel.system.config.routing.DbContextHolder;
//import com.hotel.system.config.routing.DbRole;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class TestDbController {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @GetMapping("/test-role")
//    public String checkCurrentUser(@RequestParam(defaultValue = "GUEST") String role) {
//        try {
//            // 1. Встановлюємо роль
//            DbRole selectedRole = DbRole.valueOf(role.toUpperCase());
//            DbContextHolder.setCurrentRole(selectedRole);
//
//            // 2. Виконуємо запит до БД (PostgreSQL скаже, хто ми насправді)
//            String dbUser = jdbcTemplate.queryForObject("SELECT current_user", String.class);
//
//            return "Java Role: " + selectedRole + " -> DB User: " + dbUser;
//
//        } catch (Exception e) {
//            return "Error: " + e.getMessage();
//        } finally {
//            DbContextHolder.clear();
//        }
//    }
//}