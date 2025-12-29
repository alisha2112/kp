package com.hotel.system.config.routing;

public class DbContextHolder {
    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_PASS = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_ROLE = new ThreadLocal<>();

    // Метод для встановлення всього одразу
    public static void setCredentials(String user, String pass, String role) {
        CURRENT_USER.set(user);
        CURRENT_PASS.set(pass);
        CURRENT_ROLE.set(role);
    }

    // Окремий метод для ролі (який шукає помилка)
    public static void setRole(String role) {
        CURRENT_ROLE.set(role);
    }

    public static String getUser() { return CURRENT_USER.get(); }
    public static String getPass() { return CURRENT_PASS.get(); }
    public static String getRole() { return CURRENT_ROLE.get(); }

    public static void clear() {
        CURRENT_USER.remove();
        CURRENT_PASS.remove();
        CURRENT_ROLE.remove();
    }
}
