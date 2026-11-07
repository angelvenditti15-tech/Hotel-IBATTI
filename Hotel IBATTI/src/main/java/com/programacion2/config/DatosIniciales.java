package com.programacion2.config;

import com.programacion2.dominio.Hotel;
import com.programacion2.dominio.TipoHabitacion;
import com.programacion2.repositorio.HabitacionRepositorio.FilaHabitacion;
import com.programacion2.repositorio.HotelRepositorio;
import com.programacion2.repositorio.HuespedRepositorio.FilaHuesped;
import com.programacion2.servicio.HabitacionService;
import com.programacion2.servicio.HuespedService;
import com.programacion2.servicio.ReservaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/*
 * Se ejecuta una vez al arrancar la aplicación.
 *
 * 1. Siempre: si no existe ningún hotel, crea "Hotel Ibatti".
 * 2. Solo si application.properties tiene hotel.datos-prueba=true:
 *    carga habitaciones, huéspedes y reservas de ejemplo.
 *
 * Es seguro ejecutarlo varias veces: cada bloque se carga solamente
 * si su tabla está vacía.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final HotelRepositorio hotelRepositorio;
    private final HabitacionService habitacionService;
    private final HuespedService huespedService;
    private final ReservaService reservaService;
    private final boolean cargarDatosPrueba;

    public DatosIniciales(HotelRepositorio hotelRepositorio,
                          HabitacionService habitacionService,
                          HuespedService huespedService,
                          ReservaService reservaService,
                          @Value("${hotel.datos-prueba:false}") boolean cargarDatosPrueba) {
        this.hotelRepositorio = hotelRepositorio;
        this.habitacionService = habitacionService;
        this.huespedService = huespedService;
        this.reservaService = reservaService;
        this.cargarDatosPrueba = cargarDatosPrueba;
    }

    @Override
    public void run(String... args) {
        crearHotelSiNoExiste();

        if (cargarDatosPrueba) {
            try {
                cargarHabitaciones();
                cargarHuespedes();
                cargarReservas();
                System.out.println("Datos de prueba verificados.");
            } catch (RuntimeException error) {
                System.out.println("No se pudieron cargar los datos de prueba: "
                    + error.getMessage());
            }
        }
    }

    private void crearHotelSiNoExiste() {
        if (hotelRepositorio.contar() == 0) {
            hotelRepositorio.guardar(new Hotel(
                "Hotel Ibatti", "Calle Falsa 123",
                "0299-4000000", "info@hotelibatti.com"
            ));
            System.out.println("Hotel Ibatti creado.");
        }
    }

    private void cargarHabitaciones() {
        if (!habitacionService.listar().isEmpty()) {
            return;
        }

        int hotelId = habitacionService.listarHoteles().get(0).id();

        habitacionService.registrar(hotelId, 101, TipoHabitacion.INDIVIDUAL, 1, new BigDecimal("30000.00"));
        habitacionService.registrar(hotelId, 102, TipoHabitacion.DOBLE, 2, new BigDecimal("45000.00"));
        habitacionService.registrar(hotelId, 103, TipoHabitacion.DOBLE, 2, new BigDecimal("45000.00"));
        habitacionService.registrar(hotelId, 201, TipoHabitacion.TRIPLE, 3, new BigDecimal("60000.00"));
        habitacionService.registrar(hotelId, 202, TipoHabitacion.TRIPLE, 3, new BigDecimal("60000.00"));
        habitacionService.registrar(hotelId, 301, TipoHabitacion.SUITE, 4, new BigDecimal("95000.00"));
    }

    private void cargarHuespedes() {
        if (!huespedService.listar().isEmpty()) {
            return;
        }

        // Los campos opcionales se pasan como "" (el servicio los convierte en null).
        huespedService.registrar("Ana", "Gómez", "30111222", "ana.gomez@mail.com", "299-4111111");
        huespedService.registrar("Pedro", "López", "28333444", "pedro.lopez@mail.com", "299-4222222");
        huespedService.registrar("Lucía", "Martínez", "35555666", "", "299-4333333");
        huespedService.registrar("Carlos", "Pérez", "40777888", "carlos.perez@mail.com", "");
        huespedService.registrar("Marta", "Ruiz", "25999000", "", "");
    }

    private void cargarReservas() {
        if (!reservaService.listarPorHabitacion("").isEmpty()) {
            return;
        }

        List<FilaHuesped> huespedes = huespedService.listar();
        if (huespedes.size() < 4) {
            return;
        }

        Map<Integer, Integer> habitaciones = new HashMap<>();
        for (FilaHabitacion habitacion : habitacionService.listar()) {
            habitaciones.put(habitacion.numero(), habitacion.id());
        }

        // Fechas relativas a hoy: el servicio no acepta ingresos en el pasado.
        LocalDate hoy = LocalDate.now();

        reservar(huespedes.get(0).id(), habitaciones.get(101), hoy.plusDays(1), hoy.plusDays(3), 1);
        reservar(huespedes.get(1).id(), habitaciones.get(102), hoy.plusDays(2), hoy.plusDays(6), 2);
        reservar(huespedes.get(2).id(), habitaciones.get(201), hoy.plusDays(3), hoy.plusDays(5), 3);
        reservar(huespedes.get(3).id(), habitaciones.get(301), hoy.plusDays(10), hoy.plusDays(15), 4);
        reservar(huespedes.get(0).id(), habitaciones.get(103), hoy.plusDays(20), hoy.plusDays(22), 2);
    }

    private void reservar(int huespedId, int habitacionId,
                          LocalDate ingreso, LocalDate salida, int personas) {
        reservaService.crearReserva(huespedId, habitacionId, ingreso, salida, personas);
    }
}
