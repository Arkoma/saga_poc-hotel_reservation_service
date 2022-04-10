package com.saga.saga_pochotel_reservation_service.service;

import com.saga.saga_pochotel_reservation_service.model.Hotel;
import com.saga.saga_pochotel_reservation_service.model.HotelReservation;
import com.saga.saga_pochotel_reservation_service.model.HotelReservationRequest;
import com.saga.saga_pochotel_reservation_service.model.StatusEnum;
import com.saga.saga_pochotel_reservation_service.repository.HotelRepository;
import com.saga.saga_pochotel_reservation_service.repository.HotelReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class HotelReservationService {

    private final HotelRepository hotelRepository;
    private final HotelReservationRepository hotelReservationRepository;

    public HotelReservationService(HotelRepository hotelRepository, HotelReservationRepository hotelReservationRepository) {
        this.hotelRepository = hotelRepository;
        this.hotelReservationRepository = hotelReservationRepository;
    }

    public HotelReservation makeReservation(HotelReservationRequest request) throws NoSuchElementException{
        Optional<Hotel> hotel = hotelRepository.findByName(request.getHotelName());
        HotelReservation existingReservation = hotelReservationRepository.findByReservationId(request.getReservationId());
        if (existingReservation != null) {
            return existingReservation;
        }
        HotelReservation hotelReservation = new HotelReservation();
        final long hotelId = hotel.isPresent() ? hotel.get().getId() : 0;
        hotelReservation.setHotelId(hotelId);
        hotelReservation.setReservationId(request.getReservationId());
        hotelReservation.setCheckinDate(request.getCheckinDate());
        hotelReservation.setCheckoutDate(request.getCheckoutDate());
        hotelReservation.setRoom(request.getRoom());
        try {
            hotelReservation.setStatus(hotelId == 0 ? StatusEnum.CANCELLED : StatusEnum.RESERVED);
            hotelReservation = hotelReservationRepository.save(hotelReservation);
        } catch (Exception e) {
            hotelReservation.setStatus(StatusEnum.CANCELLED);
            hotelReservation = hotelReservationRepository.save(hotelReservation);
        }
        return hotelReservation;
    }

    public void cancelReservation(Long id) {
        this.hotelReservationRepository.deleteById(id);
    }

    public HotelReservation getReservationById(Long id) {
        return this.hotelReservationRepository.findById(id).orElse(null);
    }

    public List<HotelReservation> getAllReservations() {
        return this.hotelReservationRepository.findAll();
    }
}
