package com.saga.saga_pochotel_reservation_service.service;

import com.saga.saga_pochotel_reservation_service.model.Hotel;
import com.saga.saga_pochotel_reservation_service.model.HotelReservation;
import com.saga.saga_pochotel_reservation_service.model.HotelReservationRequest;
import com.saga.saga_pochotel_reservation_service.model.StatusEnum;
import com.saga.saga_pochotel_reservation_service.repository.HotelRepository;
import com.saga.saga_pochotel_reservation_service.repository.HotelReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelReservationServiceTest {

    @InjectMocks
    private HotelReservationService underTest;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelReservationRepository hotelReservationRepository;

    @Test
    void makeReservationSavesHotelPassedIn() throws ParseException {
        String hotelName = "Holiday Inn";
        Long hotelId = 1L;
        Long reservationId = 2L;
        Long hotelReservationId = 3L;
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        hotel.setName(hotelName);
        HotelReservation hotelReservation = new HotelReservation();
        hotelReservation.setId(hotelReservationId);
        when(hotelRepository.findByName(anyString())).thenReturn(Optional.of(hotel));
        when (hotelReservationRepository.save(any(HotelReservation.class))).thenReturn(hotelReservation);
        final int roomNumber = 666;
        final Date checkinDate = new SimpleDateFormat("d MMM yyyy").parse("9 Feb 2022");
        final Date checkoutDate = new SimpleDateFormat("dd MMM yyyy").parse("12 Feb 2022");
        HotelReservationRequest request = HotelReservationRequest.builder()
                .reservationId(reservationId)
                .hotel(hotel)
                .room(roomNumber)
                .checkinDate(checkinDate)
                .checkoutDate(checkoutDate)
                .build();
        HotelReservation actual = underTest.makeReservation(request);
        verify(hotelRepository, times(1)).findByName(hotelName);
        verify(hotelReservationRepository, times(1)).save(any(HotelReservation.class));
        assertAll(() -> {
            assertEquals(hotelReservationId, actual.getId());
            assertEquals(StatusEnum.RESERVED, actual.getStatus());
        });
    }
}