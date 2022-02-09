package com.saga.saga_pochotel_reservation_service.service;

import com.saga.saga_pochotel_reservation_service.model.Hotel;
import com.saga.saga_pochotel_reservation_service.model.HotelReservation;
import com.saga.saga_pochotel_reservation_service.model.HotelReservationRequest;
import com.saga.saga_pochotel_reservation_service.model.StatusEnum;
import com.saga.saga_pochotel_reservation_service.repository.HotelRepository;
import com.saga.saga_pochotel_reservation_service.repository.HotelReservationRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class HotelReservationService {

    private final HotelRepository hotelRepository;
    private final HotelReservationRepository hotelReservationRepository;

    public HotelReservationService(HotelRepository hotelRepository, HotelReservationRepository hotelReservationRepository) {
        this.hotelRepository = hotelRepository;
        this.hotelReservationRepository = hotelReservationRepository;
    }

    public HotelReservation makeReservation(HotelReservationRequest request) throws NoSuchElementException{
        Hotel hotel = hotelRepository.findByName(request.getHotel().getName()).orElseThrow();
        HotelReservation hotelReservation = new HotelReservation();
        hotelReservation.setHotelId(hotel.getId());
        hotelReservation.setReservationId(request.getReservationId());
        hotelReservation.setCheckinDate(request.getCheckinDate());
        hotelReservation.setCheckoutDate(request.getCheckoutDate());
        hotelReservation.setRoom(request.getRoom());
        try {
            hotelReservation = hotelReservationRepository.save(hotelReservation);
            hotelReservation.setStatus(StatusEnum.RESERVED);
        } catch (Exception e) {
            hotelReservation.setStatus(StatusEnum.CANCELLED);
        }
        return hotelReservation;
    }
}
