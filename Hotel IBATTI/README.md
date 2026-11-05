# Hotel IBATTI - Sistema de Gestión de Reservas

## Integrantes
* 

## Descripción del proyecto
Aplicación web desarrollada en Java con Spring Boot y arquitectura MVC para la gestión de un hotel. Utiliza JDO (DataNucleus) para la persistencia en MySQL y Thymeleaf para la interfaz gráfica.

## Funcionalidades principales
* **Huéspedes:** ABM completo (alta, listado, modificación y eliminación) con validación de DNI único.
* **Habitaciones:** ABM completo con número, tipo, capacidad y precio por noche.
* **Reservas:** Gestión de reservas asociando huéspedes y habitaciones, validación de fechas coherentes y control de superposición.

## Instrucciones de ejecución
1. Ejecutar el archivo `script_bd.sql` en MySQL Workbench para crear la base de datos y el usuario con sus permisos.
2. Abrir la terminal en la raíz del proyecto y compilar con: