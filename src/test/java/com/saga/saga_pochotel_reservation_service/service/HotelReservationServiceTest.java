package com.saga.saga_pochotel_reservation_service.service;

import com.saga.saga_pochotel_reservation_service.model.Hotel;
import com.saga.saga_pochotel_reservation_service.model.HotelReservation;
import com.saga.saga_pochotel_reservation_service.model.HotelReservationRequest;
import com.saga.saga_pochotel_reservation_service.model.StatusEnum;
import com.saga.saga_pochotel_reservation_service.repository.HotelRepository;
import com.saga.saga_pochotel_reservation_service.repository.HotelReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelReservationServiceTest {

    @InjectMocks
    private HotelReservationService underTest;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelReservationRepository hotelReservationRepository;

    @Captor
    private ArgumentCaptor<HotelReservation> hotelReservationArgumentCaptor;

    private HotelReservation hotelReservation;

    private Long hotelReservationId;

    @BeforeEach
    void setup() {
        hotelReservationId = 3L;
        hotelReservation = new HotelReservation();
        hotelReservation.setId(hotelReservationId);
    }

    @Test
    void makeReservationSavesHotelPassedIn() throws ParseException {
        String hotelName = "Holiday Inn";
        Long hotelId = 1L;
        Long reservationId = 2L;
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        hotel.setName(hotelName);
        when(hotelRepository.findByName(anyString())).thenReturn(Optional.of(hotel));
        when (hotelReservationRepository.save(any(HotelReservation.class))).thenReturn(hotelReservation);
        final int roomNumber = 666;
        final Date checkinDate = new SimpleDateFormat("d MMM yyyy").parse("9 Feb 2022");
        final Date checkoutDate = new SimpleDateFormat("dd MMM yyyy").parse("12 Feb 2022");
        HotelReservationRequest request = HotelReservationRequest.builder()
                .reservationId(reservationId)
                .hotelName(hotelName)
                .room(roomNumber)
                .checkinDate(checkinDate)
                .checkoutDate(checkoutDate)
                .build();
        HotelReservation actual = underTest.makeReservation(request);
        verify(hotelRepository, times(1)).findByName(hotelName);
        verify(hotelReservationRepository, times(1)).save(any(HotelReservation.class));
        assertAll(() -> assertEquals(hotelReservationId, actual.getId()));
    }

    @Test
    void cancelReservationDeletesEntity() {
        underTest.cancelReservation(1L);
        verify(hotelReservationRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void testGetReservationByIdCallsFindById() {
        when(hotelReservationRepository.findById(anyLong())).thenReturn(Optional.of(hotelReservation));
        HotelReservation actual = underTest.getReservationById(1L);
        verify(hotelReservationRepository, times(1)).findById(anyLong());
        assertEquals(hotelReservation.getId(), actual.getId());
    }

    @Test
    void getAllReservationsCallsFindAll() {
        underTest.getAllReservations();
        verify(hotelReservationRepository, times(1)).findAll();
    }

    @Test
    void whenCannotFindHotelByNameReservationStatusIsCancelled() throws ParseException {
        String hotelName = "Holiday Inn";
        Long hotelId = 1L;
        Long reservationId = 2L;
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        hotel.setName(hotelName);
        when(hotelRepository.findByName(anyString())).thenReturn(Optional.empty());
        when (hotelReservationRepository.save(any(HotelReservation.class))).thenReturn(hotelReservation);
        final int roomNumber = 666;
        final Date checkinDate = new SimpleDateFormat("d MMM yyyy").parse("9 Feb 2022");
        final Date checkoutDate = new SimpleDateFormat("dd MMM yyyy").parse("12 Feb 2022");
        HotelReservationRequest request = HotelReservationRequest.builder()
                .reservationId(reservationId)
                .hotelName(hotelName)
                .room(roomNumber)
                .checkinDate(checkinDate)
                .checkoutDate(checkoutDate)
                .build();
        underTest.makeReservation(request);
        verify(hotelReservationRepository, times(1)).save(hotelReservationArgumentCaptor.capture());
        HotelReservation actual = hotelReservationArgumentCaptor.getValue();
        assertEquals(StatusEnum.CANCELLED, actual.getStatus());
    }

    @Test
    void whenReservationIdAlreadyExistsReturnExisting() throws ParseException {
        String hotelName = "Holiday Inn";
        Long hotelId = 1L;
        Long reservationId = 2L;
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        hotel.setName(hotelName);
        when(hotelRepository.findByName(anyString())).thenReturn(Optional.empty());
        final int roomNumber = 666;
        final Date checkinDate = new SimpleDateFormat("d MMM yyyy").parse("9 Feb 2022");
        final Date checkoutDate = new SimpleDateFormat("dd MMM yyyy").parse("12 Feb 2022");
        HotelReservationRequest request = HotelReservationRequest.builder()
                .reservationId(reservationId)
                .hotelName(hotelName)
                .room(roomNumber)
                .checkinDate(checkinDate)
                .checkoutDate(checkoutDate)
                .build();
        HotelReservation existingReservation = new HotelReservation();
        existingReservation.setHotelId(0L);
        existingReservation.setReservationId(2L);
        existingReservation.setCheckinDate(checkinDate);
        existingReservation.setCheckoutDate(checkoutDate);
        existingReservation.setRoom(roomNumber);
        existingReservation.setStatus(StatusEnum.CANCELLED);
        when(hotelReservationRepository.findByReservationId(2L)).thenReturn(existingReservation);
        HotelReservation actual = underTest.makeReservation(request);
        verify(hotelReservationRepository, times(1)).findByReservationId(2L);
        verify(hotelReservationRepository, times(0)).save(any(HotelReservation.class));
        assertEquals(StatusEnum.CANCELLED, actual.getStatus());
    }

    @Test
    void whenReservationIdDoesNotExistReturnNewReservation() throws ParseException {
        String hotelName = "Holiday Inn";
        Long hotelId = 1L;
        Long reservationId = 2L;
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        hotel.setName(hotelName);
        when(hotelRepository.findByName(anyString())).thenReturn(Optional.of(hotel));
        when (hotelReservationRepository.save(any(HotelReservation.class))).thenReturn(hotelReservation);
        final int roomNumber = 666;
        final Date checkinDate = new SimpleDateFormat("d MMM yyyy").parse("9 Feb 2022");
        final Date checkoutDate = new SimpleDateFormat("dd MMM yyyy").parse("12 Feb 2022");
        HotelReservationRequest request = HotelReservationRequest.builder()
                .reservationId(reservationId)
                .hotelName(hotelName)
                .room(roomNumber)
                .checkinDate(checkinDate)
                .checkoutDate(checkoutDate)
                .build();
        when(hotelReservationRepository.findByReservationId(2L)).thenReturn(null);
        underTest.makeReservation(request);
        verify(hotelReservationRepository, times(1)).findByReservationId(2L);
        verify(hotelReservationRepository, times(1)).save(hotelReservationArgumentCaptor.capture());
        HotelReservation actual = hotelReservationArgumentCaptor.getValue();
        assertEquals(StatusEnum.RESERVED, actual.getStatus());
    }
}