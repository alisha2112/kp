package com.hotel.system.config;

import com.hotel.system.config.routing.ClientRoutingDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Value("${hotel.datasource.url}")
    private String url;

    @Bean
    @Primary
    public DataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();

        // Створюємо окремі пули для кожної ролі (креди з вашої методички)
        targetDataSources.put("ADMIN", createPool("app_admin_user", "admin123"));
        targetDataSources.put("MANAGER", createPool("app_manager_user", "manager123"));
        targetDataSources.put("OWNER", createPool("app_owner_user", "owner123"));
        targetDataSources.put("ACCOUNTANT", createPool("app_accountant_user", "money123"));
        targetDataSources.put("CLEANER", createPool("app_cleaner_user", "cleaner123"));
        targetDataSources.put("CLIENT", createPool("app_client_user", "client123"));
        targetDataSources.put("GUEST", createPool("app_guest_user", "guest123"));

        ClientRoutingDataSource routingDataSource = new ClientRoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(targetDataSources.get("GUEST"));

        return routingDataSource;
    }

    private DataSource createPool(String user, String pass) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(pass);
        config.setDriverClassName("org.postgresql.Driver");

        config.setMinimumIdle(1); // Тримати 1 з'єднання завжди
        config.setMaximumPoolSize(2);
        config.setInitializationFailTimeout(0); // Не зупиняти додаток, якщо база тимчасово недоступна

        config.addDataSourceProperty("ApplicationName", "HotelApp_" + user);

        return new HikariDataSource(config);
    }
}
