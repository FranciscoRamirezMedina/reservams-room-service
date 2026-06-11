package com.duoc.reservams.roomservice.service;

import com.duoc.reservams.roomservice.dto.RoomRequestDTO;
import com.duoc.reservams.roomservice.dto.RoomResponseDTO;
import com.duoc.reservams.roomservice.model.Room;
import com.duoc.reservams.roomservice.client.HotelClient;
import com.duoc.reservams.roomservice.dto.HotelResponseDTO;
import com.duoc.reservams.roomservice.repository.RoomRepository;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

// aqui va la logica de negocio de habitaciones
@Service
public class RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;

    private final HotelClient hotelClient;

    public RoomService(RoomRepository roomRepository,
                       HotelClient hotelClient) {
        this.roomRepository = roomRepository;
        this.hotelClient = hotelClient;
    }

    public List<RoomResponseDTO> findAll() {
        logger.info("Listando todas las habitaciones");

        List<RoomResponseDTO> rooms = roomRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();

        logger.info("Se encontraron {} habitaciones", rooms.size());

        return rooms;
    }

    public RoomResponseDTO findById(Long id) {
        logger.info("Buscando habitacion por ID {}", id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se encontro habitacion con ID {}", id);
                    return new RuntimeException("Habitación no encontrada");
                });

        logger.info("Habitacion encontrada con ID {}, hotel ID {} y numero {}",
                room.getId(),
                room.getHotelId(),
                room.getRoomNumber());

        return toResponseDTO(room);
    }

    public List<RoomResponseDTO> findByHotelId(Long hotelId) {
        logger.info("Listando habitaciones del hotel ID {}", hotelId);

        List<RoomResponseDTO> rooms = roomRepository.findByHotelId(hotelId)
                .stream()
                .map(this::toResponseDTO)
                .toList();

        logger.info("Se encontraron {} habitaciones para el hotel ID {}", rooms.size(), hotelId);

        return rooms;
    }

    public List<RoomResponseDTO> findAvailableByHotel(Long hotelId) {
        logger.info("Listando habitaciones disponibles del hotel ID {}", hotelId);

        List<RoomResponseDTO> rooms = roomRepository.findByHotelIdAndStatus(hotelId, "AVAILABLE")
                .stream()
                .map(this::toResponseDTO)
                .toList();

        logger.info("Se encontraron {} habitaciones disponibles para el hotel ID {}", rooms.size(), hotelId);

        return rooms;
    }

    public RoomResponseDTO create(RoomRequestDTO request) {
        logger.info("Iniciando creacion de habitacion numero {} para hotel ID {}",
                request.getRoomNumber(),
                request.getHotelId());

        // antes de crear la habitacion, verificamos que el hotel exista
        validateActiveHotel(request.getHotelId(), "crear habitacion");

        Room room = new Room();
        room.setHotelId(request.getHotelId());
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setCapacity(request.getCapacity());
        room.setPricePerNight(request.getPricePerNight());
        room.setStatus(request.getStatus());
        room.setCreatedAt(LocalDateTime.now());

        logger.info("Guardando habitacion numero {} para hotel ID {} con estado {}",
                room.getRoomNumber(),
                room.getHotelId(),
                room.getStatus());

        Room savedRoom = roomRepository.save(room);

        logger.info("Habitacion creada correctamente con ID {}, hotel ID {} y numero {}",
                savedRoom.getId(),
                savedRoom.getHotelId(),
                savedRoom.getRoomNumber());

        return toResponseDTO(savedRoom);
    }

    public RoomResponseDTO update(Long id, RoomRequestDTO request) {
        logger.info("Iniciando actualizacion de habitacion con ID {}", id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se pudo actualizar. Habitacion no encontrada con ID {}", id);
                    return new RuntimeException("Habitacion no encontrada");
                });

        // validamos que el hotel exista antes de actualizar la habitacion
        validateActiveHotel(request.getHotelId(), "actualizar habitacion");

        room.setHotelId(request.getHotelId());
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setCapacity(request.getCapacity());
        room.setPricePerNight(request.getPricePerNight());
        room.setStatus(request.getStatus());

        logger.info("Guardando cambios de habitacion ID {}", id);

        Room updatedRoom = roomRepository.save(room);

        logger.info("Habitacion actualizada correctamente con ID {}, hotel ID {} y estado {}",
                updatedRoom.getId(),
                updatedRoom.getHotelId(),
                updatedRoom.getStatus());

        return toResponseDTO(updatedRoom);
    }

    public void delete(Long id) {
        logger.info("Iniciando desactivacion de habitacion con ID {}", id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("No se pudo desactivar. Habitacion no encontrada con ID {}", id);
                    return new RuntimeException("Habitación no encontrada");
                });

        // no eliminamos fisicamente, solo cambiamos el estado
        room.setStatus("INACTIVE");
        roomRepository.save(room);

        logger.info("Habitacion con ID {} fue desactivada correctamente", id);
    }

    private void validateActiveHotel(Long hotelId, String operation) {
        try {
            logger.info("Validando hotel ID {} mediante OpenFeign para {}", hotelId, operation);

            HotelResponseDTO hotel = hotelClient.findById(hotelId);

            if (!hotel.getStatus().equals("ACTIVE")) {
                logger.warn("No se puede {}. Hotel ID {} se encuentra con estado {}",
                        operation,
                        hotel.getId(),
                        hotel.getStatus());

                if (operation.equals("crear habitacion")) {
                    throw new RuntimeException("No se puede crear habitacion en un hotel inactivo");
                }

                throw new RuntimeException("No se puede asignar habitacion a un hotel inactivo");
            }

            logger.info("Hotel ID {} validado correctamente para {}", hotel.getId(), operation);

        } catch (Exception ex) {
            logger.warn("No se pudo validar el hotel ID {} para {}. Motivo: {}",
                    hotelId,
                    operation,
                    ex.getMessage());

            throw new RuntimeException("No se pudo validar el hotel: " + ex.getMessage());
        }
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