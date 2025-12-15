package com.hotel.system.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cleaner")
public class CleanerPageController {

    @GetMapping("/schedule")
    public String schedule(HttpSession session, Model model) {
        // 1. Перевірка безпеки: чи це прибиральник?
        String role = (String) session.getAttribute("CURRENT_ROLE");

        // Дозволяємо доступ лише прибиральникам
        if (!"CLEANER".equals(role)) {
            return "redirect:/";
        }

        // 2. Передаємо ID користувача для відображення на сторінці
        model.addAttribute("userId", session.getAttribute("USER_ID"));

        // Повертаємо шаблон: src/main/resources/templates/cleaner/schedule.html
        return "cleaner/schedule";
    }
}