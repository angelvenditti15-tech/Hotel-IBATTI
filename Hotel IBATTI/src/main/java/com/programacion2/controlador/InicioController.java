package com.programacion2.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("nombreHotel", "Hotel IBATTI");
        return "inicio";
    }
}