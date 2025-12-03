package com.hotel.system.controller;

import com.hotel.system.service.ClientService;
import com.hotel.system.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final ClientService clientService;
    private final CommonService commonService;

    @GetMapping("/hotels")
    public ResponseEntity<List<Map<String, Object>>> getCatalog() {
        return ResponseEntity.ok(clientService.getPublicHotelCatalog());
    }

    @GetMapping("/rooms/search")
    public ResponseEntity<List<Map<String, Object>>> searchRooms(
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "1") Integer guests,
            @RequestParam(required = false) String comfortLevel) {

        return ResponseEntity.ok(clientService.searchRoomsPublic(city, checkIn, checkOut, guests, comfortLevel));
    }

    @GetMapping("/reviews/{hotelName}")
    public ResponseEntity<List<Map<String, Object>>> getReviews(@PathVariable String hotelName) {
        return ResponseEntity.ok(clientService.getHotelReviews(hotelName));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String firstName,
                                      @RequestParam String middleName,
                                      @RequestParam String lastName,
                                      @RequestParam String phone,
                                      @RequestParam String email) {
        Long id = clientService.registerSelf(firstName, middleName, lastName, phone, email);
        return ResponseEntity.ok(Map.of("message", "Registered successfully", "clientId", id));
    }

    @GetMapping("/services")
    public ResponseEntity<List<Map<String, Object>>> getServicesList() {
        return ResponseEntity.ok(commonService.getHotelServices());
    }
}