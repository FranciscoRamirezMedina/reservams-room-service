package com.duoc.reservams.roomservice.controller;

import com.duoc.reservams.roomservice.dto.RoomRequestDTO;
import com.duoc.reservams.roomservice.dto.RoomResponseDTO;
import com.duoc.reservams.roomservice.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// controlador REST para habitaciones
@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // lista todas las habitaciones
    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> findAll() {
        return ResponseEntity.ok(roomService.findAll());
    }

    // busca una habitacion por su ID
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.findById(id));
    }

    // lista habitaciones de un hotel
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomResponseDTO>> findByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.findByHotelId(hotelId));
    }

    // lista habitaciones disponibles de un hotel
    @GetMapping("/hotel/{hotelId}/available")
    public ResponseEntity<List<RoomResponseDTO>> findAvailableByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.findAvailableByHotel(hotelId));
    }

    // crea una nueva habitacion
    @PostMapping
    public ResponseEntity<RoomResponseDTO> create(@Valid @RequestBody RoomRequestDTO request) {
        return ResponseEntity.ok(roomService.create(request));
    }

    // actualiza una habitacion
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequestDTO request) {

        return ResponseEntity.ok(roomService.update(id, request));
    }

    // desactiva una habitacion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}