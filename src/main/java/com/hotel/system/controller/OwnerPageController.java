package com.hotel.system.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/owner")
public class OwnerPageController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String role = (String) session.getAttribute("CURRENT_ROLE");
        // Пускаємо тільки Власника
        if (!"OWNER".equals(role)) {
            return "redirect:/login";
        }
        return "owner/dashboard"; // Файл: templates/owner/dashboard.html
    }
}