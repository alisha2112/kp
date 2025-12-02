package com.hotel.system.config.routing;

/**
 * Перелік ролей, які відповідають ключам у нашому Routing DataSource.
 * Ці константи будуть використовуватися для перемикання підключень.
 */
public enum DbRole {
    GUEST,      // Для незареєстрованих користувачів
    CLIENT,     // Для зареєстрованих клієнтів
    ADMIN,      // Адміністратор рецепції
    MANAGER,    // Менеджер
    OWNER,      // Власник
    CLEANER,    // Прибиральниця
    ACCOUNTANT  // Бухгалтер
}