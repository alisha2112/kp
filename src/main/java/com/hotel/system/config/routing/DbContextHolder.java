package com.hotel.system.config.routing;

import org.springframework.util.Assert;

public class DbContextHolder {

    private static final ThreadLocal<DbRole> CONTEXT = new ThreadLocal<>();

    public static void setCurrentRole(DbRole role) {
        Assert.notNull(role, "Role cannot be null");
        CONTEXT.set(role);
    }

    public static DbRole getCurrentRole() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}