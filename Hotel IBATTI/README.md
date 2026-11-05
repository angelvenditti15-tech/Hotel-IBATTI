# Hotel IBATTI - Sistema de Gestión de Reservas

## Integrantes
* 
* 
* Venditti Angel

## Descripción
App web en Java con Spring Boot para administrar las reservas de un hotel. Usamos MySQL para guardar los datos y Thymeleaf para las pantallas.

## Lo que hace el sistema
* **Huéspedes:** Carga, lista, edita y borra huéspedes (no deja repetir DNI).
* **Habitaciones:** Alta y listado con tipo, capacidad y precio por noche.
* **Reservas:** Permite reservar asociando un huésped con una habitación y controlando las fechas.

## Cómo ejecutarlo
1. Ejecutar el archivo `script_bd.sql` en MySQL Workbench para crear la base de datos.
2. Abrir la terminal en la carpeta del proyecto y ejecutar: mvn spring-boot:run