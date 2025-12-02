package com.hotel.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// exclude = DataSourceAutoConfiguration.class потрібен, бо ми налаштували свій власний DataSourceConfig
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class HotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelApplication.class, args);
    }
}