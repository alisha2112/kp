package com.hotel.system.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Реєструємо наш перехоплювач для всіх URL (/**)
        // Це гарантує, що перед виконанням будь-якого контролера
        // буде встановлено правильну роль користувача в БД
        registry.addInterceptor(new RoleInterceptor()).addPathPatterns("/**");
    }
}