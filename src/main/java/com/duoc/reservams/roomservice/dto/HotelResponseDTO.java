package com.duoc.reservams.roomservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

// DTO que recibe datos basicos de un hotel desde hotel-service
@Data
public class HotelResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String status;
    private LocalDateTime createdAt;
}