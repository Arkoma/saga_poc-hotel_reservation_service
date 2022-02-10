package com.saga.saga_pochotel_reservation_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.saga_pochotel_reservation_service.model.Hotel;
import com.saga.saga_pochotel_reservation_service.model.HotelReservation;
import com.saga.saga_pochotel_reservation_service.model.HotelReservationRequest;
import com.saga.saga_pochotel_reservation_service.model.StatusEnum;
import com.saga.saga_pochotel_reservation_service.repository.HotelRepository;
import com.saga.saga_pochotel_reservation_service.repository.HotelReservationRepository;
import com.saga.saga_pochotel_reservation_service.service.HotelReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebAppConfiguration
class HotelReservationControllerIT {

    private final WebApplicationContext webApplicationContext;
    private final HotelReservationService hotelReservationService;
    private final HotelRepository hotelRepository;
    private final HotelReservationRepository hotelReservationRepository;
    private final HotelReservationController underTest;

    @Autowired
    public HotelReservationControllerIT(WebApplicationContext webApplicationContext,
                                        HotelReservationService hotelReservationService,
                                        HotelReservationController hotelReservationController,
                                        HotelRepository hotelRepository,
                                        HotelReservationRepository hotelReservationRepository) throws ParseException {
        this.webApplicationContext = webApplicationContext;
        this.hotelReservationService = hotelReservationService;
        this.underTest = hotelReservationController;
        this.hotelRepository = hotelRepository;
        this.hotelReservationRepository = hotelReservationRepository;
    }

    private MockMvc mockMvc;
    private HotelReservationRequest hotelReservationRequest;
    private Hotel hotel;
    private final Long reservationId = 1L;
    private final int roomNumber = 666;
    private final Date checkinDate = new SimpleDateFormat("d MMM yyyy").parse("9 Feb 2022");
    private final Date checkoutDate = new SimpleDateFormat("dd MMM yyyy").parse("12 Feb 2022");
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        hotel = new Hotel();
        hotel.setName("Holiday Inn");
        hotel = this.hotelRepository.save(hotel);
        hotelReservationRequest = HotelReservationRequest.builder()
                .reservationId(reservationId)
                .hotel(hotel)
                .room(roomNumber)
                .checkinDate(checkinDate)
                .checkoutDate(checkoutDate)
                .build();
        this.hotelReservationRepository.deleteAll();
    }

    @Test
    void hotelReservationControllerExistsAsABean() {
        assertTrue(webApplicationContext.containsBean("hotelReservationController"));
    }

    @Test
    void hotelReservationServiceIsInjectedInTheController() {
        HotelReservationService  injectedHotelReservationService =(HotelReservationService) ReflectionTestUtils.getField(underTest, "hotelReservationService");
        assertSame(hotelReservationService, injectedHotelReservationService);
    }

    @Test
    void makeReservationEndpointExists() throws Exception {
        this.mockMvc.perform(post("/reservation").contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isCreated());
    }

    @Test
    void makeReservationEndpointReturnsReservation() throws Exception {
        String json = mapper.writeValueAsString(this.hotelReservationRequest);
        final MvcResult result = this.mockMvc.perform(post("/reservation").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        HotelReservation actualResponse = mapper.readValue(responseJson, HotelReservation.class);
        assertAll(() -> {
            assertEquals(StatusEnum.RESERVED ,actualResponse.getStatus());
            assertEquals(hotel.getId(), actualResponse.getHotelId());
            assertEquals(this.reservationId, actualResponse.getReservationId());
            assertEquals(this.checkinDate, actualResponse.getCheckinDate());
            assertEquals(this.checkoutDate, actualResponse.getCheckoutDate());
            assertEquals(this.roomNumber, actualResponse.getRoom());
                }
        );
    }

    @Test
    void makeReservationEndpointSavesReservation() throws Exception {
        String json = mapper.writeValueAsString(this.hotelReservationRequest);
        MvcResult mvcResult = this.mockMvc.perform(post("/reservation").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String responseJson = mvcResult.getResponse().getContentAsString();
        HotelReservation actualResponse = mapper.readValue(responseJson, HotelReservation.class);
        HotelReservation actualEntity = this.hotelReservationRepository.getById(actualResponse.getId());
        assertAll(() -> {
            assertEquals(StatusEnum.RESERVED ,actualEntity.getStatus());
            assertEquals(hotel.getId(), actualEntity.getHotelId());
            assertEquals(this.reservationId, actualEntity.getReservationId());
            assertEquals(this.checkinDate, actualEntity.getCheckinDate());
            assertEquals(this.checkoutDate, actualEntity.getCheckoutDate());
            assertEquals(this.roomNumber, actualEntity.getRoom());
                }
        );
    }

}

