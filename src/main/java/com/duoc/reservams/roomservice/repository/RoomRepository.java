package com.duoc.reservams.roomservice.repository;

import com.duoc.reservams.roomservice.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// repository para acceder a la tabla rooms
public interface RoomRepository extends JpaRepository<Room, Long> {

    // lista habitaciones de un hotel específico
    List<Room> findByHotelId(Long hotelId);

    // lista habitaciones por estado
    List<Room> findByStatus(String status);

    // lista habitaciones de un hotel segun estado
    List<Room> findByHotelIdAndStatus(Long hotelId, String status);
}