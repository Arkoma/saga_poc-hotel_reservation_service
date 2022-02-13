package com.saga.saga_pochotel_reservation_service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

import java.util.Date;

@Setter
@Getter
@ToString
@Entity
public class HotelReservation {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private Long reservationId;
    private StatusEnum status;
    private Long hotelId;
    private int room;
    private Date checkinDate;
    private Date checkoutDate;
}
