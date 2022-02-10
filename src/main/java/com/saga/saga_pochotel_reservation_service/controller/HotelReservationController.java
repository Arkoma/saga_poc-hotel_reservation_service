package com.saga.saga_pochotel_reservation_service.controller;

import com.saga.saga_pochotel_reservation_service.model.HotelReservation;
import com.saga.saga_pochotel_reservation_service.model.HotelReservationRequest;
import com.saga.saga_pochotel_reservation_service.service.HotelReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HotelReservationController {

    private final HotelReservationService hotelReservationService;

    public HotelReservationController(HotelReservationService hotelReservationService) {
        this.hotelReservationService = hotelReservationService;
    }


    @PostMapping("/reservation")
    public ResponseEntity<HotelReservation> makeReservation(@RequestBody HotelReservationRequest request) {
        HotelReservation hotelReservation = this.hotelReservationService.makeReservation(request);
        return new ResponseEntity<>(hotelReservation, HttpStatus.CREATED);
    }
}
