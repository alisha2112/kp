package com.hotel.system.config.routing;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ClientRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        // Spring візьме ключ (наприклад "ADMIN") і вибере відповідний пул Hikari
        String role = DbContextHolder.getRole();
        return (role != null) ? role : "GUEST";
    }
}