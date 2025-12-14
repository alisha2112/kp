package com.hotel.system.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/management")
public class ManagementPageController {

    private boolean isManager(HttpSession session) {
        String role = (String) session.getAttribute("CURRENT_ROLE");
        return "MANAGER".equals(role) || "OWNER".equals(role);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isManager(session)) {
            return "redirect:/";
        }
        return "management/dashboard"; // Файл: templates/management/dashboard.html
    }
}