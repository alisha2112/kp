package com.hotel.system.service;

import com.hotel.system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;
    private final MarketingRepository marketingRepository;
    private final ServiceRepository serviceRepository;
    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;

    // --- ГІСТЬ (GUEST) ---

    public List<Map<String, Object>> getPublicHotelCatalog() {
        return hotelRepository.getPublicCatalog();
    }

    public List<Map<String, Object>> searchRoomsPublic(String city, LocalDate checkIn, LocalDate checkOut, Integer guests, String comfortLevel) {
        return roomRepository.searchRoomsPublic(city, checkIn, checkOut, guests, comfortLevel);
    }

    public List<Map<String, Object>> getHotelReviews(String hotelName) {
        return reviewRepository.getPublicReviews(hotelName);
    }

    @Transactional
    public Long registerSelf(String firstName, String middleName, String lastName, String phone, String email) {
        // isSelfRegistration = true
        return clientRepository.registerClient(firstName, middleName, lastName, phone, email, true);
    }

    // --- КЛІЄНТ (CLIENT) ---

    // 1. Профіль
    @Transactional
    public void updateProfile(Long clientId, String firstName, String lastName, String phone, String email) {
        clientRepository.updateProfile(clientId, firstName, lastName, phone, email);
    }

    @Transactional
    public void deleteAccount(Long clientId) {
        clientRepository.deleteAccount(clientId);
    }

    public Map<String, Object> getClientProfile(Long clientId) {
        return clientRepository.getClientById(clientId);
    }

    // 2. Бронювання
    public List<Map<String, Object>> searchRoomsForClient(LocalDate checkIn, LocalDate checkOut, String city) {
        return roomRepository.getRoomsForClient(checkIn, checkOut, city);
    }

    @Transactional
    public Long bookRoom(Long clientId, Long roomId, LocalDate checkIn, LocalDate checkOut, Integer guests, String promoCode) {
        return clientRepository.bookRoom(clientId, roomId, checkIn, checkOut, guests, promoCode);
    }

    @Transactional
    public void cancelBooking(Long clientId, Long bookingId, String reason) {
        bookingRepository.cancelBookingClient(clientId, bookingId, reason);
    }

    @Transactional
    public Long repeatBooking(Long clientId, Long oldBookingId, LocalDate newCheckIn, LocalDate newCheckOut) {
        return bookingRepository.repeatBooking(clientId, oldBookingId, newCheckIn, newCheckOut);
    }

    public List<Map<String, Object>> getBookingHistory(Long clientId) {
        return bookingRepository.getClientHistory(clientId);
    }

    // 3. Оплата
    @Transactional
    public void payOnline(Long clientId, Long bookingId, String cardToken) {
        paymentRepository.payOnlineClient(clientId, bookingId, cardToken);
    }

    public List<Map<String, Object>> getPaymentHistory(Long clientId) {
        return paymentRepository.getClientPaymentHistory(clientId);
    }

    // 4 & 5. Маркетинг (Промокоди та Акції)
    @Transactional
    public Integer usePromocode(Long clientId, String code) {
        return marketingRepository.usePromocode(clientId, code);
    }

    public List<Map<String, Object>> getActivePromotions() {
        return marketingRepository.getActivePromotions();
    }

    public List<Map<String, Object>> getAllServices() {
        return serviceRepository.getAllServices();
    }
    // 6. Послуги
    @Transactional
    public void requestService(Long clientId, Long bookingId, Long serviceId) {
        serviceRepository.requestService(clientId, bookingId, serviceId);
    }

    public List<Map<String, Object>> getMenu() {
        return serviceRepository.getMenu();
    }

    public List<Map<String, Object>> getActiveRooms(Long clientId) {
        return serviceRepository.getActiveRooms(clientId);
    }

    @Transactional
    public void orderFood(Long clientId, Long roomId, String itemsJson) {
        serviceRepository.orderFood(clientId, roomId, itemsJson);
    }

    public List<Map<String, Object>> getBookingsForReview(Long clientId) {
        return reviewRepository.getBookingsForReview(clientId);
    }

    @Transactional
    public void leaveReview(Long clientId, Long bookingId, Integer rating, String comment) {
        reviewRepository.leaveReview(clientId, bookingId, rating, comment);
    }

    public List<Map<String, Object>> getFavoriteRooms(Long clientId) {
        return clientRepository.getFavoriteRooms(clientId);
    }

    public void removeFromFavorites(Long clientId, Long roomId) {
        clientRepository.removeFromFavorites(clientId, roomId);
    }

    @Transactional
    public void addToFavorites(Long clientId, Long roomId) {
        clientRepository.addFavoriteRoom(clientId, roomId);
    }

    // Додайте цей метод у клас ClientService
    public Map<String, Object> getRoomDetails(Long roomId) {
        return clientRepository.getRoomDetails(roomId); // Викликаємо репозиторій
    }
}