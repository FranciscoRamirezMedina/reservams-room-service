package com.duoc.reservams.roomservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ReservamsRoomServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservamsRoomServiceApplication.class, args);
	}

}
