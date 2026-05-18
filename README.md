\# ReservaMS - Room Service



\## Descripcion



Este microservicio administra las habitaciones de los hoteles.



Cada habitacion pertenece a un hotel, pero se guarda solo el hotelId porque el hotel vive en otro microservicio.



\## Responsabilidades



\- Crear habitaciones.

\- Listar habitaciones.

\- Buscar habitaciones por ID.

\- Listar habitaciones por hotel.

\- Listar habitaciones disponibles por hotel.

\- Actualizar habitaciones.

\- Desactivar habitaciones.



\## Puerto



8084



\## Base de datos



reservams\_room\_db



\## Endpoints principales



\- GET /api/v1/rooms

\- GET /api/v1/rooms/{id}

\- GET /api/v1/rooms/hotel/{hotelId}

\- GET /api/v1/rooms/hotel/{hotelId}/available

\- POST /api/v1/rooms

\- PUT /api/v1/rooms/{id}

\- DELETE /api/v1/rooms/{id}



\## Ejecucion



1\. Crear la base de datos reservams\_room\_db.

2\. Ejecutar el script SQL ubicado en la carpeta database.

3\. Levantar Eureka Server.

4\. Ejecutar el room-service.

5\. Probar los endpoints desde Postman o desde el API Gateway.



