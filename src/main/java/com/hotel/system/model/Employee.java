package com.hotel.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private Long employeeId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phone;
    private String position; // Можна використати Enum, але String простіше для JDBC
    private Long hotelId;    // Зберігаємо ID, а не об'єкт Hotel (специфіка JDBC)

    // Додаткове поле для зручності відображення (немає в таблиці, але корисне в коді)
    public String getFullName() {
        return firstName + " " + lastName;
    }
}