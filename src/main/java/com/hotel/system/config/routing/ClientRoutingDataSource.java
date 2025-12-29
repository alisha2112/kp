package com.hotel.system.config.routing;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class ClientRoutingDataSource extends AbstractRoutingDataSource {
    private Map<Object, Object> targetDataSources = new HashMap<>();

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        this.targetDataSources = targetDataSources;
        super.setTargetDataSources(targetDataSources);
    }

    public void addDataSource(String key, DataSource ds) {
        this.targetDataSources.put(key, ds);
        this.setTargetDataSources(targetDataSources); // Оновлюємо внутрішню мапу Spring
        this.afterPropertiesSet(); // Примушуємо Spring переініціалізувати мапу
    }

    // Додати в ClientRoutingDataSource.java
    public boolean hasDataSource(String key) {
        return this.targetDataSources.containsKey(key);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        // Spring візьме ключ (наприклад "ADMIN") і вибере відповідний пул Hikari
        String role = DbContextHolder.getRole();
        return (role != null) ? role : "GUEST";
    }
}