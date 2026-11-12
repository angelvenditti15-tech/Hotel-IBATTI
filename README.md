# Hotel IBATTI - Sistema de Gestión de Reservas

## Integrantes
- Venditti Angel
- [Nombre de tu compañero 2]
- [Nombre de tu compañero 3]

## Descripción
Aplicación web desarrollada en Java con Spring Boot para administrar de forma integral las reservas de un hotel. Utiliza MySQL como motor de persistencia y Thymeleaf para las vistas de la interfaz de usuario[cite: 7].

## Lo que hace el sistema
- **Huéspedes:** Carga, listado, edición y baja de huéspedes con validación de DNI[cite: 7].
- **Habitaciones:** Alta y listado especificando tipo, capacidad y precio por noche[cite: 7].
- **Reservas:** Permite asociar un huésped con una habitación controlando disponibilidad y fechas, con gestión de estados (Confirmada / Cancelada).

## Requisitos
- JDK 17 o superior.
- Maven disponible en la terminal.
- MySQL Server en ejecución en `127.0.0.1:3306`.

## Preparar MySQL
1. Abrir `script_bd.sql` en MySQL Workbench con una cuenta administradora.
2. Si el usuario `hotel_app` todavía no existe, reemplazar `ELEGIR_CLAVE_LOCAL` por una contraseña propia en una copia local del script.
3. Ejecutar el script. Si `hotel_app` ya existía, usar su contraseña actual.
4. DataNucleus crea las tablas faltantes al iniciar la aplicación. La aplicación también carga datos iniciales de ejemplo cuando corresponde.

## Ejecutar en Windows
Abrir PowerShell en la carpeta `Hotel IBATTI` que contiene `pom.xml`. Ejecutar la primera línea ingresando la contraseña de `hotel_app`, y luego ejecutar la segunda línea en la misma terminal:

```powershell
$env:HOTEL_DB_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'Contrasenä de MySQL para hotel_app' -AsSecureString)).Password
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.address=127.0.0.1 --server.port=8081"
