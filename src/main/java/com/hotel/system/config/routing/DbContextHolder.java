package com.hotel.system.config.routing;

public class DbContextHolder {
    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_PASS = new ThreadLocal<>();

    public static void setCredentials(String user, String pass) {
        CURRENT_USER.set(user);
        CURRENT_PASS.set(pass);
    }

    public static String getUser() { return CURRENT_USER.get(); }
    public static String getPass() { return CURRENT_PASS.get(); }

    public static void clear() {
        CURRENT_USER.remove();
        CURRENT_PASS.remove();
    }
}

//public class DbContextHolder {
//
//    private static final ThreadLocal<DbRole> CONTEXT = new ThreadLocal<>();
//
//    public static void setCurrentRole(DbRole role) {
//        Assert.notNull(role, "Role cannot be null");
//        CONTEXT.set(role);
//    }
//
//    public static DbRole getCurrentRole() {
//        return CONTEXT.get();
//    }
//
//    public static void clear() {
//        CONTEXT.remove();
//    }
//}