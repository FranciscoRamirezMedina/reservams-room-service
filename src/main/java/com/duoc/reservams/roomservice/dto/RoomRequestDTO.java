package com.duoc.reservams.roomservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

// DTO para crear o actualizar habitaciones
@Data
public class RoomRequestDTO {

    @NotNull(message = "El hotelId es obligatorio")
    private Long hotelId;

    @NotBlank(message = "El numero de habitación es obligatorio")
    private String roomNumber;

    @NotBlank(message = "El tipo de habitacion es obligatorio")
    private String roomType;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    private Integer capacity;

    @NotNull(message = "El precio por noche es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal pricePerNight;

    @NotBlank(message = "El estado es obligatorio")
    private String status;
}