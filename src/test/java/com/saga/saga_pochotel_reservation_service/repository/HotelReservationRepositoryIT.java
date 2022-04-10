package com.saga.saga_pochotel_reservation_service.repository;

import com.saga.saga_pochotel_reservation_service.model.HotelReservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
class HotelReservationRepositoryIT {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HotelReservationRepository underTest;

    @BeforeEach
    void setup() {
        underTest.deleteAll();
    }

    @Test
    void testHotelReservationRepository() {
        assertTrue(applicationContext.containsBean("hotelReservationRepository"));
    }

    @Test
    void findsReservationByReservationId() {
        HotelReservation hotelReservation = new HotelReservation();
        hotelReservation.setReservationId(1L);
        underTest.save(hotelReservation);
        HotelReservation saved = underTest.findByReservationId(1L);
        HotelReservation notSaved = underTest.findByReservationId(2L);
        assertEquals(1L, saved.getReservationId());
        assertNull(notSaved);
    }
}