package com.programacion2.controlador;

import com.programacion2.dominio.TipoHabitacion;
import com.programacion2.repositorio.HabitacionRepositorio.DatosHabitacion;
import com.programacion2.servicio.HabitacionService;

import java.math.BigDecimal;

import javax.jdo.JDOObjectNotFoundException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HabitacionController {

    private final HabitacionService servicio;

    public HabitacionController(HabitacionService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/habitaciones")
    public String listar(Model model) {
        model.addAttribute("habitaciones", servicio.listar());
        return "habitaciones";
    }

    @GetMapping("/habitaciones/nueva")
    public String nueva(Model model) {
        return mostrarFormulario(model, null, null, "", "", "", "", "");
    }

    @GetMapping("/habitaciones/{id}/editar")
    public String editar(@PathVariable("id") int id, Model model,
                         RedirectAttributes redirect) {
        try {
            DatosHabitacion datos = servicio.buscar(id);
            return mostrarFormulario(
                model, id, datos.hotel(), "",
                Integer.toString(datos.numero()), datos.tipo().name(),
                Integer.toString(datos.capacidad()), datos.precioPorNoche().toPlainString()
            );
        } catch (JDOObjectNotFoundException error) {
            redirect.addFlashAttribute("mensaje", "La habitación ya no existe.");
            return "redirect:/habitaciones";
        }
    }

    @PostMapping({"/habitaciones", "/habitaciones/{id}/editar"})
    public String guardar(
            @PathVariable(name = "id", required = false) Integer id,
            @RequestParam(name = "hotelId", defaultValue = "") String hotelId,
            @RequestParam(name = "numero", defaultValue = "") String numero,
            @RequestParam(name = "tipo", defaultValue = "") String tipo,
            @RequestParam(name = "capacidad", defaultValue = "") String capacidad,
            @RequestParam(name = "precio", defaultValue = "") String precio,
            Model model, RedirectAttributes redirect) {
        String hotel = null;

        try {
            if (id != null) {
                hotel = servicio.buscar(id).hotel();
            }

            int numeroHabitacion;
            int capacidadHabitacion;
            TipoHabitacion tipoHabitacion;
            BigDecimal precioPorNoche;
            int idHotel;

            try {
                numeroHabitacion = Integer.parseInt(numero);
                capacidadHabitacion = Integer.parseInt(capacidad);
                tipoHabitacion = TipoHabitacion.valueOf(tipo);
                precioPorNoche = new BigDecimal(precio);
                idHotel = id == null ? Integer.parseInt(hotelId) : 0;
            } catch (IllegalArgumentException error) {
                throw new IllegalArgumentException(
                    "Completá todos los campos con valores válidos.", error
                );
            }

            if (id == null) {
                servicio.registrar(idHotel, numeroHabitacion, tipoHabitacion,
                    capacidadHabitacion, precioPorNoche);
                redirect.addFlashAttribute("mensaje", "Habitación registrada correctamente.");
            } else {
                servicio.actualizar(id, numeroHabitacion, tipoHabitacion,
                    capacidadHabitacion, precioPorNoche);
                redirect.addFlashAttribute("mensaje", "Habitación actualizada correctamente.");
            }
        } catch (JDOObjectNotFoundException error) {
            redirect.addFlashAttribute("mensaje", "La habitación ya no existe.");
        } catch (IllegalArgumentException error) {
            model.addAttribute("error", error.getMessage());
            return mostrarFormulario(model, id, hotel, hotelId, numero, tipo, capacidad, precio);
        }

        return "redirect:/habitaciones";
    }

    @PostMapping("/habitaciones/{id}/eliminar")
    public String eliminar(@PathVariable("id") int id, RedirectAttributes redirect) {
        try {
            servicio.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Habitación eliminada correctamente.");
        } catch (JDOObjectNotFoundException error) {
            redirect.addFlashAttribute("mensaje", "La habitación ya no existe.");
        } catch (IllegalStateException error) {
            redirect.addFlashAttribute("mensaje", error.getMessage());
        }

        return "redirect:/habitaciones";
    }

    private String mostrarFormulario(Model model, Integer id, String hotel, String hotelId,
                                     String numero, String tipo, String capacidad, String precio) {
        if (id == null) {
            model.addAttribute("hoteles", servicio.listarHoteles());
        }

        model.addAttribute("id", id);
        model.addAttribute("hotel", hotel);
        model.addAttribute("hotelId", hotelId);
        model.addAttribute("tipos", TipoHabitacion.values());
        model.addAttribute("numero", numero);
        model.addAttribute("tipo", tipo);
        model.addAttribute("capacidad", capacidad);
        model.addAttribute("precio", precio);
        return "habitacion-nueva";
    }
}