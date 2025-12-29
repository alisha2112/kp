package com.hotel.system.config;

import com.hotel.system.config.routing.ClientRoutingDataSource;
import com.hotel.system.config.routing.DbRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;

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
        return new ClientRoutingDataSource(url);
    }
}


/*
@Configuration
public class DataSourceConfig {

    @Value("${hotel.datasource.url}")
    private String url;

    // Читаємо налаштування для кожної ролі
    @Value("${hotel.datasource.guest.username}") private String guestUser;
    @Value("${hotel.datasource.guest.password}") private String guestPass;

    @Value("${hotel.datasource.client.username}") private String clientUser;
    @Value("${hotel.datasource.client.password}") private String clientPass;

    @Value("${hotel.datasource.admin.username}") private String adminUser;
    @Value("${hotel.datasource.admin.password}") private String adminPass;

    @Value("${hotel.datasource.manager.username}") private String managerUser;
    @Value("${hotel.datasource.manager.password}") private String managerPass;

    @Value("${hotel.datasource.owner.username}") private String ownerUser;
    @Value("${hotel.datasource.owner.password}") private String ownerPass;

    @Value("${hotel.datasource.cleaner.username}") private String cleanerUser;
    @Value("${hotel.datasource.cleaner.password}") private String cleanerPass;

    @Value("${hotel.datasource.accountant.username}") private String accountantUser;
    @Value("${hotel.datasource.accountant.password}") private String accountantPass;

    @Bean
    @Primary
    public DataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();

        // Створюємо реальні підключення
        targetDataSources.put(DbRole.GUEST, buildDataSource(guestUser, guestPass));
        targetDataSources.put(DbRole.CLIENT, buildDataSource(clientUser, clientPass));
        targetDataSources.put(DbRole.ADMIN, buildDataSource(adminUser, adminPass));
        targetDataSources.put(DbRole.MANAGER, buildDataSource(managerUser, managerPass));
        targetDataSources.put(DbRole.OWNER, buildDataSource(ownerUser, ownerPass));
        targetDataSources.put(DbRole.CLEANER, buildDataSource(cleanerUser, cleanerPass));
        targetDataSources.put(DbRole.ACCOUNTANT, buildDataSource(accountantUser, accountantPass));

        ClientRoutingDataSource routingDataSource = new ClientRoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);

        // За замовчуванням - Гість (безпека понад усе)
        routingDataSource.setDefaultTargetDataSource(buildDataSource(guestUser, guestPass));

        return routingDataSource;
    }

    private DataSource buildDataSource(String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(this.url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");

        // КЛЮЧОВІ НАЛАШТУВАННЯ ДЛЯ ВИДИМОСТІ:
        // 1. Мінімальна кількість з'єднань, які ЗАВЖДИ відкриті
        config.setMinimumIdle(1);
        // 2. Максимальна кількість з'єднань у пулі
        config.setMaximumPoolSize(5);
        // 3. Унікальне ім'я для моніторингу (буде видно в Postgres)
        config.addDataSourceProperty("ApplicationName", "HotelApp_" + username);

        return new HikariDataSource(config);
    }

//    private DriverManagerDataSource buildDataSource(String username, String password) {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.postgresql.Driver");
//        dataSource.setUrl(this.url);
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//        return dataSource;
//    }
}*/