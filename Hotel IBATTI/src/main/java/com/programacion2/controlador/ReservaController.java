package com.programacion2.controlador;

import com.programacion2.repositorio.ReservaRepositorio.OpcionesReserva;
import com.programacion2.servicio.ReservaService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReservaController {

    private final ReservaService servicio;

    public ReservaController(ReservaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/reservas")
    public String listar(
            @RequestParam(name = "habitacion", defaultValue = "") String habitacion,
            Model model) {
        model.addAttribute("reservas", servicio.listarPorHabitacion(habitacion));
        model.addAttribute("habitacion", habitacion.strip());
        return "reservas";
    }

    @GetMapping("/reservas/nueva")
    public String nueva(Model model) {
        return mostrarFormulario(model, "", "", "", "", "1");
    }

    @PostMapping("/reservas")
    public String guardar(
            @RequestParam(name = "huespedId", defaultValue = "") String huespedId,
            @RequestParam(name = "habitacionId", defaultValue = "") String habitacionId,
            @RequestParam(name = "ingreso", defaultValue = "") String ingreso,
            @RequestParam(name = "salida", defaultValue = "") String salida,
            @RequestParam(name = "personas", defaultValue = "") String personas,
            Model model, RedirectAttributes redirect) {
        try {
            int id = servicio.crearReserva(
                Integer.parseInt(huespedId),
                Integer.parseInt(habitacionId),
                LocalDate.parse(ingreso),
                LocalDate.parse(salida),
                Integer.parseInt(personas)
            );

            redirect.addFlashAttribute("mensaje", "Reserva guardada. ID: " + id);
            return "redirect:/reservas";
        } catch (NumberFormatException | DateTimeParseException error) {
            model.addAttribute(
                "error", "Completá el huésped, la habitación, las fechas y la cantidad de personas."
            );
        } catch (IllegalArgumentException | IllegalStateException error) {
            model.addAttribute("error", error.getMessage());
        }

        return mostrarFormulario(model, huespedId, habitacionId, ingreso, salida, personas);
    }

    private String mostrarFormulario(Model model, String huespedId, String habitacionId,
                                     String ingreso, String salida, String personas) {
        OpcionesReserva opciones = servicio.cargarOpciones();
        model.addAttribute("huespedes", opciones.huespedes());
        model.addAttribute("habitaciones", opciones.habitaciones());
        model.addAttribute("huespedId", huespedId);
        model.addAttribute("habitacionId", habitacionId);
        model.addAttribute("ingreso", ingreso);
        model.addAttribute("salida", salida);
        model.addAttribute("personas", personas);
        return "reserva-nueva";
    }
}