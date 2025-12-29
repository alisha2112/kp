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

        // --- КЛЮЧОВІ НАЛАШТУВАННЯ ДЛЯ ДЕМОНСТРАЦІЇ ---
        config.setMinimumIdle(0);          // Не створювати з'єднання при старті
        config.setMaximumPoolSize(5);      // Максимум 5 з'єднань на роль
        config.setIdleTimeout(30000);      // Закрити з'єднання через 30 сек бездіяльності
        config.setInitializationFailTimeout(0);

        // Це допоможе вам у pgAdmin в колонці "Application Name" побачити хто це
        config.addDataSourceProperty("ApplicationName", "HotelApp_" + user.toUpperCase());

        return new HikariDataSource(config);
    }
}
