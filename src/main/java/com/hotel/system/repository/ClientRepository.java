package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ClientRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** 2 (Адмін) / 6 (Гість): Реєстрація */
    public Long registerClient(String firstName, String middleName, String lastName, String phone, String email, boolean isSelfRegistration) {
        String procName = isSelfRegistration ? "sp_self_register" : "sp_register_client";
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(procName);

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_first_name", firstName);
        inParams.put("p_middle_name", middleName);
        inParams.put("p_last_name", lastName);
        inParams.put("p_phone", phone);
        inParams.put("p_email", email);
        inParams.put(isSelfRegistration ? "p_client_id" : "p_new_client_id", null);

        Map<String, Object> out = jdbcCall.execute(inParams);
        String outParamName = isSelfRegistration ? "p_client_id" : "p_new_client_id";
        return ((Number) out.get(outParamName)).longValue();
    }

    /** 1.1 Оновлення профілю */
    public void updateProfile(Long clientId, String firstName, String lastName, String phone, String email) {
        jdbcTemplate.update("CALL sp_client_update_profile(?, ?, ?, ?, ?)", clientId, firstName, lastName, phone, email);
    }

    /** 1.2 Видалення профілю */
    public void deleteAccount(Long clientId) {
        jdbcTemplate.update("CALL sp_client_delete_account(?)", clientId);
    }

    /** 8.3 Додати в улюблене */
    public void addFavoriteRoom(Long clientId, Long roomId) {
        jdbcTemplate.update("CALL sp_add_favorite_room(?, ?)", clientId, roomId);
    }
}