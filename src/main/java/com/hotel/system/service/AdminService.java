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
public class AdminService {

    private final BookingRepository bookingRepository;
    private final ClientRepository clientRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;

    // 1. Бронювання - ОНОВЛЕНО
    public List<Map<String, Object>> checkRoomAvailability(LocalDate checkIn, LocalDate checkOut, Long hotelId) {
        // Передаємо hotelId далі в репозиторій
        return roomRepository.getAvailableRoomsAdmin(checkIn, checkOut, hotelId);
    }

    @Transactional
    public Long createBooking(Long clientId, Long roomId, LocalDate checkIn, LocalDate checkOut, Integer guests, String paymentMethod) {
        return bookingRepository.createBookingAdmin(clientId, roomId, checkIn, checkOut, guests, paymentMethod);
    }

    @Transactional
    public void cancelBooking(Long bookingId, String reason) {
        bookingRepository.cancelBookingAdmin(bookingId, reason);
    }

    @Transactional
    public void updateBooking(Long bookingId, Long newRoomId, LocalDate newCheckIn, LocalDate newCheckOut) {
        bookingRepository.updateBooking(bookingId, newRoomId, newCheckIn, newCheckOut);
    }

    // ОНОВЛЕНО: приймаємо hotelId
    public List<Map<String, Object>> getReceptionDashboard(Long hotelId) {
        return bookingRepository.getViewReceptionBookings(hotelId);
    }

    // 2. Клієнти
//    @Transactional
//    public Long registerClient(String firstName, String middleName, String lastName, String phone, String email) {
//        // isSelfRegistration = false (реєструє адмін)
//        return clientRepository.registerClient(firstName, middleName, lastName, phone, email, false);
//    }

    // У файлі com.hotel.system.service.AdminService

    @Transactional
    public Long registerClient(String firstName, String middleName, String lastName,
                               String phone, String email, String dbUser, String dbPass) {
        // Тепер передаємо 8 параметрів, як того вимагає ClientRepository
        // Останній параметр false, бо це реєстрація через адміна, а не самостійна
        return clientRepository.registerClient(
                firstName,
                middleName,
                lastName,
                phone,
                email,
                dbUser,
                dbPass,
                false
        );
    }

    // 3. Виселення / Статус - ОНОВЛЕНО
    public Map<String, Object> checkRoomStatus(Integer roomNumber, Long hotelId) {
        return roomRepository.getRoomExtendedStatus(roomNumber, hotelId);
    }

    // 3. Генерація рахунку - ОНОВЛЕНО
    public List<Map<String, Object>> generateBill(Long bookingId, Long hotelId) {
        return bookingRepository.getBillDetails(bookingId, hotelId);
    }

    // 3. Check-out - ОНОВЛЕНО
    @Transactional
    public void processCheckout(Long bookingId, String paymentMethod, Long hotelId) {
        bookingRepository.processCheckout(bookingId, paymentMethod, hotelId);
    }

    // Додавання відгуку вручну - ОНОВЛЕНО
    @Transactional
    public void addReviewManually(Long bookingId, Integer rating, String comment, Long hotelId) {
        reviewRepository.addReviewManually(bookingId, rating, comment, hotelId);
    }

    // 4. Оплата - ОНОВЛЕНО
    @Transactional
    public void acceptPayment(Long bookingId, String lastName, String firstName, String middleName, String method, Long hotelId) {
        paymentRepository.acceptPaymentAdmin(bookingId, lastName, firstName, middleName, method, hotelId);
    }

    // 4.2 Перевірка боргу - ОНОВЛЕНО
    public Map<String, Object> checkDebt(Long bookingId, Long hotelId) {
        return paymentRepository.getDebtStatus(bookingId, hotelId);
    }
}