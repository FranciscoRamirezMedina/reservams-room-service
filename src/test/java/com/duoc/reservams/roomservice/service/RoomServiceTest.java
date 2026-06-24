package com.duoc.reservams.roomservice.service;

import com.duoc.reservams.roomservice.client.HotelClient;
import com.duoc.reservams.roomservice.dto.HotelResponseDTO;
import com.duoc.reservams.roomservice.dto.RoomRequestDTO;
import com.duoc.reservams.roomservice.dto.RoomResponseDTO;
import com.duoc.reservams.roomservice.model.Room;
import com.duoc.reservams.roomservice.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// pruebas unitarias para RoomService
@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelClient hotelClient;

    @InjectMocks
    private RoomService roomService;

    @Test
    void findAll_shouldReturnRooms() {
        // Given
        when(roomRepository.findAll()).thenReturn(List.of(
                buildRoom(1L, 1L, "101", "AVAILABLE"),
                buildRoom(2L, 1L, "102", "AVAILABLE")
        ));

        // When
        List<RoomResponseDTO> response = roomService.findAll();

        // Then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("101", response.get(0).getRoomNumber());

        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnRoom_whenExists() {
        // Given
        Room room = buildRoom(1L, 1L, "101", "AVAILABLE");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        // When
        RoomResponseDTO response = roomService.findById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getHotelId());
        assertEquals("101", response.getRoomNumber());
        assertEquals("AVAILABLE", response.getStatus());

        verify(roomRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowException_whenRoomNotFound() {
        // Given
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roomService.findById(99L)
        );

        // Then
        assertEquals("Habitación no encontrada", exception.getMessage());

        verify(roomRepository, times(1)).findById(99L);
    }

    @Test
    void findByHotelId_shouldReturnRooms() {
        // Given
        when(roomRepository.findByHotelId(1L)).thenReturn(List.of(
                buildRoom(1L, 1L, "101", "AVAILABLE"),
                buildRoom(2L, 1L, "102", "MAINTENANCE")
        ));

        // When
        List<RoomResponseDTO> response = roomService.findByHotelId(1L);

        // Then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getHotelId());

        verify(roomRepository, times(1)).findByHotelId(1L);
    }

    @Test
    void findAvailableByHotel_shouldReturnAvailableRooms() {
        // Given
        when(roomRepository.findByHotelIdAndStatus(1L, "AVAILABLE")).thenReturn(List.of(
                buildRoom(1L, 1L, "101", "AVAILABLE"),
                buildRoom(2L, 1L, "102", "AVAILABLE")
        ));

        // When
        List<RoomResponseDTO> response = roomService.findAvailableByHotel(1L);

        // Then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("AVAILABLE", response.get(0).getStatus());

        verify(roomRepository, times(1)).findByHotelIdAndStatus(1L, "AVAILABLE");
    }

    @Test
    void create_shouldCreateRoom_whenHotelIsActive() {
        // Given
        RoomRequestDTO request = buildRoomRequest("AVAILABLE");
        HotelResponseDTO hotel = buildHotelResponse(1L, "ACTIVE");

        when(hotelClient.findById(1L)).thenReturn(hotel);

        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room room = invocation.getArgument(0);
            room.setId(1L);
            return room;
        });

        // When
        RoomResponseDTO response = roomService.create(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getHotelId());
        assertEquals("101", response.getRoomNumber());
        assertEquals("DOUBLE", response.getRoomType());
        assertEquals("AVAILABLE", response.getStatus());
        assertNotNull(response.getCreatedAt());

        verify(hotelClient, times(1)).findById(1L);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void create_shouldThrowException_whenHotelIsInactive() {
        // Given
        RoomRequestDTO request = buildRoomRequest("AVAILABLE");
        HotelResponseDTO hotel = buildHotelResponse(1L, "INACTIVE");

        when(hotelClient.findById(1L)).thenReturn(hotel);

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roomService.create(request)
        );

        // Then
        assertEquals("No se pudo validar el hotel: No se puede crear habitacion en un hotel inactivo",
                exception.getMessage());

        verify(hotelClient, times(1)).findById(1L);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void create_shouldThrowException_whenHotelClientFails() {
        // Given
        RoomRequestDTO request = buildRoomRequest("AVAILABLE");

        when(hotelClient.findById(1L)).thenThrow(new RuntimeException("Hotel no encontrado"));

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roomService.create(request)
        );

        // Then
        assertEquals("No se pudo validar el hotel: Hotel no encontrado", exception.getMessage());

        verify(hotelClient, times(1)).findById(1L);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void update_shouldUpdateRoom_whenRoomExistsAndHotelIsActive() {
        // Given
        Room room = buildRoom(1L, 1L, "101", "AVAILABLE");
        RoomRequestDTO request = buildRoomRequest("MAINTENANCE");
        HotelResponseDTO hotel = buildHotelResponse(1L, "ACTIVE");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(hotelClient.findById(1L)).thenReturn(hotel);

        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        RoomResponseDTO response = roomService.update(1L, request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("101", response.getRoomNumber());
        assertEquals("MAINTENANCE", response.getStatus());

        verify(roomRepository, times(1)).findById(1L);
        verify(hotelClient, times(1)).findById(1L);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void update_shouldThrowException_whenRoomNotFound() {
        // Given
        RoomRequestDTO request = buildRoomRequest("AVAILABLE");

        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roomService.update(99L, request)
        );

        // Then
        assertEquals("Habitacion no encontrada", exception.getMessage());

        verify(roomRepository, times(1)).findById(99L);
        verify(hotelClient, never()).findById(anyLong());
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void update_shouldThrowException_whenHotelIsInactive() {
        // Given
        Room room = buildRoom(1L, 1L, "101", "AVAILABLE");
        RoomRequestDTO request = buildRoomRequest("AVAILABLE");
        HotelResponseDTO hotel = buildHotelResponse(1L, "INACTIVE");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(hotelClient.findById(1L)).thenReturn(hotel);

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roomService.update(1L, request)
        );

        // Then
        assertEquals("No se pudo validar el hotel: No se puede asignar habitacion a un hotel inactivo",
                exception.getMessage());

        verify(roomRepository, times(1)).findById(1L);
        verify(hotelClient, times(1)).findById(1L);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void delete_shouldSetRoomInactive_whenExists() {
        // Given
        Room room = buildRoom(1L, 1L, "101", "AVAILABLE");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        roomService.delete(1L);

        // Then
        assertEquals("INACTIVE", room.getStatus());

        verify(roomRepository, times(1)).findById(1L);
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void delete_shouldThrowException_whenRoomNotFound() {
        // Given
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roomService.delete(99L)
        );

        // Then
        assertEquals("Habitación no encontrada", exception.getMessage());

        verify(roomRepository, times(1)).findById(99L);
        verify(roomRepository, never()).save(any(Room.class));
    }

    private RoomRequestDTO buildRoomRequest(String status) {
        RoomRequestDTO request = new RoomRequestDTO();
        request.setHotelId(1L);
        request.setRoomNumber("101");
        request.setRoomType("DOUBLE");
        request.setCapacity(2);
        request.setPricePerNight(new BigDecimal("45000"));
        request.setStatus(status);
        return request;
    }

    private Room buildRoom(Long id, Long hotelId, String roomNumber, String status) {
        Room room = new Room();
        room.setId(id);
        room.setHotelId(hotelId);
        room.setRoomNumber(roomNumber);
        room.setRoomType("DOUBLE");
        room.setCapacity(2);
        room.setPricePerNight(new BigDecimal("45000"));
        room.setStatus(status);
        room.setCreatedAt(LocalDateTime.now());
        return room;
    }

    private HotelResponseDTO buildHotelResponse(Long id, String status) {
        HotelResponseDTO hotel = mock(HotelResponseDTO.class);
        when(hotel.getId()).thenReturn(id);
        when(hotel.getStatus()).thenReturn(status);
        return hotel;
    }
}