package com.programacion2.controlador;

import com.programacion2.repositorio.HuespedRepositorio.DatosHuesped;
import com.programacion2.servicio.HuespedService;

import javax.jdo.JDOObjectNotFoundException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HuespedController {

    private final HuespedService servicio;

    public HuespedController(HuespedService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/huespedes")
    public String listar(Model model) {
        model.addAttribute("huespedes", servicio.listar());
        return "huespedes";
    }

    @GetMapping("/huespedes/nuevo")
    public String nuevo() {
        return "huesped-nuevo";
    }

    @PostMapping("/huespedes")
    public String registrar(
            @RequestParam(name = "nombre", defaultValue = "") String nombre,
            @RequestParam(name = "apellido", defaultValue = "") String apellido,
            @RequestParam(name = "dni", defaultValue = "") String dni,
            @RequestParam(name = "email", defaultValue = "") String email,
            @RequestParam(name = "telefono", defaultValue = "") String telefono,
            Model model, RedirectAttributes redirect) {
        try {
            servicio.registrar(nombre, apellido, dni, email, telefono);
        } catch (IllegalArgumentException error) {
            model.addAttribute("error", error.getMessage());
            return mostrarFormulario(model, new DatosHuesped(nombre, apellido, dni, email, telefono));
        }

        redirect.addFlashAttribute("mensaje", "Huésped registrado correctamente.");
        return "redirect:/huespedes";
    }

    @GetMapping("/huespedes/{id}/editar")
    public String editar(@PathVariable("id") int id, Model model,
                         RedirectAttributes redirect) {
        try {
            DatosHuesped datos = servicio.buscar(id);
            model.addAttribute("id", id);
            return mostrarFormulario(model, datos);
        } catch (JDOObjectNotFoundException error) {
            redirect.addFlashAttribute("mensaje", "El huésped ya no existe.");
            return "redirect:/huespedes";
        }
    }

    @PostMapping("/huespedes/{id}/editar")
    public String actualizar(
            @PathVariable("id") int id,
            @RequestParam(name = "nombre", defaultValue = "") String nombre,
            @RequestParam(name = "apellido", defaultValue = "") String apellido,
            @RequestParam(name = "dni", defaultValue = "") String dni,
            @RequestParam(name = "email", defaultValue = "") String email,
            @RequestParam(name = "telefono", defaultValue = "") String telefono,
            Model model, RedirectAttributes redirect) {
        try {
            servicio.actualizar(id, nombre, apellido, dni, email, telefono);
        } catch (IllegalArgumentException error) {
            model.addAttribute("error", error.getMessage());
            model.addAttribute("id", id);
            return mostrarFormulario(model, new DatosHuesped(nombre, apellido, dni, email, telefono));
        } catch (JDOObjectNotFoundException error) {
            redirect.addFlashAttribute("mensaje", "El huésped ya no existe.");
            return "redirect:/huespedes";
        }

        redirect.addFlashAttribute("mensaje", "Huésped actualizado correctamente.");
        return "redirect:/huespedes";
    }

    @PostMapping("/huespedes/{id}/eliminar")
    public String eliminar(@PathVariable("id") int id, RedirectAttributes redirect) {
        try {
            servicio.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Huésped eliminado correctamente.");
        } catch (JDOObjectNotFoundException error) {
            redirect.addFlashAttribute("mensaje", "El huésped ya no existe.");
        } catch (IllegalStateException error) {
            redirect.addFlashAttribute("mensaje", error.getMessage());
        }

        return "redirect:/huespedes";
    }

    private String mostrarFormulario(Model model, DatosHuesped datos) {
        model.addAttribute("nombre", datos.nombre());
        model.addAttribute("apellido", datos.apellido());
        model.addAttribute("dni", datos.dni());
        model.addAttribute("email", datos.email());
        model.addAttribute("telefono", datos.telefono());
        return "huesped-nuevo";
    }
}