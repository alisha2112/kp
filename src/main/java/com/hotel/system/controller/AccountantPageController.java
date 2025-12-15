package com.hotel.system.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/accountant")
public class AccountantPageController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // 1. Перевірка ролі
        String role = (String) session.getAttribute("CURRENT_ROLE");
        if (!"ACCOUNTANT".equals(role)) {
            return "redirect:/login";
        }

        // 2. Передача даних у шаблон
        model.addAttribute("userId", session.getAttribute("USER_ID"));
        model.addAttribute("hotelId", session.getAttribute("HOTEL_ID"));

        return "accountant/dashboard"; // Шаблон: src/main/resources/templates/accountant/dashboard.html
    }
}