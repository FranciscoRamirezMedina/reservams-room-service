package com.duoc.reservams.roomservice.service;

import com.duoc.reservams.roomservice.dto.RoomRequestDTO;
import com.duoc.reservams.roomservice.dto.RoomResponseDTO;
import com.duoc.reservams.roomservice.model.Room;
import com.duoc.reservams.roomservice.client.HotelClient;
import com.duoc.reservams.roomservice.dto.HotelResponseDTO;
import com.duoc.reservams.roomservice.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// aqui va la logica de negocio de habitaciones
@Service
public class RoomService {

    private final RoomRepository roomRepository;

    private final HotelClient hotelClient;

    public RoomService(RoomRepository roomRepository,
                       HotelClient hotelClient) {
        this.roomRepository = roomRepository;
        this.hotelClient = hotelClient;
    }

    public List<RoomResponseDTO> findAll() {
        return roomRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public RoomResponseDTO findById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        return toResponseDTO(room);
    }

    public List<RoomResponseDTO> findByHotelId(Long hotelId) {
        return roomRepository.findByHotelId(hotelId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<RoomResponseDTO> findAvailableByHotel(Long hotelId) {
        return roomRepository.findByHotelIdAndStatus(hotelId, "AVAILABLE")
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public RoomResponseDTO create(RoomRequestDTO request) {
        try {
            // antes de crear la habitacion, verificamos que el hotel exista
            HotelResponseDTO hotel = hotelClient.findById(request.getHotelId());

            // no permitimos crear habitaciones en hoteles inactivos
            if (!hotel.getStatus().equals("ACTIVE")) {
                throw new RuntimeException("No se puede crear habitacion en un hotel inactivo");
            }

        } catch (Exception ex) {
            throw new RuntimeException("No se pudo validar el hotel: " + ex.getMessage());
        }

        Room room = new Room();
        room.setHotelId(request.getHotelId());
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setCapacity(request.getCapacity());
        room.setPricePerNight(request.getPricePerNight());
        room.setStatus(request.getStatus());
        room.setCreatedAt(LocalDateTime.now());

        Room savedRoom = roomRepository.save(room);

        return toResponseDTO(savedRoom);
    }

    public RoomResponseDTO update(Long id, RoomRequestDTO request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habitacion no encontrada"));

        try {
            // validamos que el hotel exista antes de actualizar la habitacion
            HotelResponseDTO hotel = hotelClient.findById(request.getHotelId());

            if (!hotel.getStatus().equals("ACTIVE")) {
                throw new RuntimeException("No se puede asignar habitacion a un hotel inactivo");
            }

        } catch (Exception ex) {
            throw new RuntimeException("No se pudo validar el hotel: " + ex.getMessage());
        }

        room.setHotelId(request.getHotelId());
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setCapacity(request.getCapacity());
        room.setPricePerNight(request.getPricePerNight());
        room.setStatus(request.getStatus());

        Room updatedRoom = roomRepository.save(room);

        return toResponseDTO(updatedRoom);
    }

    public void delete(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        // no eliminamos fisicamente, solo cambiamos el estado
        room.setStatus("INACTIVE");
        roomRepository.save(room);
    }

    // convierte una entidad Room a DTO de respuesta
    private RoomResponseDTO toResponseDTO(Room room) {
        return new RoomResponseDTO(
                room.getId(),
                room.getHotelId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getCapacity(),
                room.getPricePerNight(),
                room.getStatus(),
                room.getCreatedAt()
        );
    }
}