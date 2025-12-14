package com.hotel.system.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPageController {

    // Допоміжний метод для перевірки доступу рядок 18 || "MANAGER".equals(role)
    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("CURRENT_ROLE");
        return "ADMIN".equals(role);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // 1. ЗАХИСТ: Якщо це не адмін, перенаправляємо на логін або головну
        if (!isAdmin(session)) {
            return "redirect:/";
        }

        // Тут ми повертаємо шаблон саме для адміна
        return "admin/dashboard"; // файл: resources/templates/admin/dashboard.html
    }
}