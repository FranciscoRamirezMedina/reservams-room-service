package com.duoc.reservams.roomservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// esta clase representa una habitación dentro de un hotel
@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    // ID principal de la habitacion
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID logico del hotel que viene desde hotel-service
    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    // numero o codigo de la habitación
    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    // tipo de habitacion, SINGLE, DOUBLE, SUITE, FAMILY
    @Column(name = "room_type", nullable = false, length = 30)
    private String roomType;

    // cantidad maxima de personas
    @Column(nullable = false)
    private Integer capacity;

    // precio por noche
    @Column(name = "price_per_night", nullable = false)
    private BigDecimal pricePerNight;

    // estado, AVAILABLE, MAINTENANCE o INACTIVE
    @Column(nullable = false, length = 30)
    private String status;

    // fecha en que se creo la habitación
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}