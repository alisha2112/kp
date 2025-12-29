package com.hotel.system.config.routing;
//
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//import org.springframework.lang.Nullable;
//
//public class ClientRoutingDataSource extends AbstractRoutingDataSource {
//
//    @Override
//    @Nullable
//    protected Object determineCurrentLookupKey() {
//        return DbContextHolder.getCurrentRole();
//    }
//}

import org.springframework.jdbc.datasource.AbstractDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ClientRoutingDataSource extends AbstractDataSource {

    private String url;

    public ClientRoutingDataSource(String url) {
        this.url = url;
    }

    @Override
    public Connection getConnection() throws SQLException {
        String user = DbContextHolder.getUser();
        String pass = DbContextHolder.getPass();

        // Якщо користувач не залогінився, використовуємо гостя (пароль гостя можна лишити або вимагати вхід)
        if (user == null) {
            user = "app_guest_user";
            pass = "guest123";
        }

        return DriverManager.getConnection(url, user, pass);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}