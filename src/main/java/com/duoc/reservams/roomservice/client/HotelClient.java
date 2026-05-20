package com.duoc.reservams.roomservice.client;

import com.duoc.reservams.roomservice.dto.HotelResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// cliente Feign para comunicarse con hotel-service
@FeignClient(name = "reservams-hotel-service", url = "http://localhost:8083")
public interface HotelClient {

    // busca un hotel por ID en hotel-service
    @GetMapping("/api/v1/hotels/{id}")
    HotelResponseDTO findById(@PathVariable("id") Long id);
}