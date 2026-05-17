package com.duoc.reservams.roomservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// DTO para responder datos de habitaciones
@Data
@AllArgsConstructor
public class RoomResponseDTO {

    private Long id;
    private Long hotelId;
    private String roomNumber;
    private String roomType;
    private Integer capacity;
    private BigDecimal pricePerNight;
    private String status;
    private LocalDateTime createdAt;
}