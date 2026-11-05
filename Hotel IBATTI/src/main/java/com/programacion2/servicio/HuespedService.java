package com.programacion2.servicio;

import com.programacion2.dominio.Huesped;
import com.programacion2.repositorio.HuespedRepositorio;
import com.programacion2.repositorio.HuespedRepositorio.DatosHuesped;
import com.programacion2.repositorio.HuespedRepositorio.FilaHuesped;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class HuespedService {

    private final HuespedRepositorio repositorio;

    public HuespedService(HuespedRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<FilaHuesped> listar() {
        return repositorio.listar();
    }

    public DatosHuesped buscar(int id) {
        return repositorio.buscar(id);
    }

    public void registrar(String nombre, String apellido, String dni,
                          String email, String telefono) {
        repositorio.guardar(validarDatos(nombre, apellido, dni, email, telefono));
    }

    public void actualizar(int id, String nombre, String apellido, String dni,
                           String email, String telefono) {
        repositorio.actualizar(id, validarDatos(nombre, apellido, dni, email, telefono));
    }

    public void eliminar(int id) {
        if (!repositorio.eliminarSiNoTieneReservas(id)) {
            throw new IllegalStateException(
                "No se puede eliminar el huésped porque tiene reservas asociadas."
            );
        }
    }

    private Huesped validarDatos(String nombre, String apellido, String dni,
                                 String email, String telefono) {
        String nombreLimpio = nombre.strip();
        String apellidoLimpio = apellido.strip();
        String dniLimpio = dni.strip();
        String emailLimpio = email.strip();
        String telefonoLimpio = telefono.strip();

        if (nombreLimpio.isEmpty() || apellidoLimpio.isEmpty()) {
            throw new IllegalArgumentException("Ingresá el nombre y el apellido.");
        }

        if (!dniLimpio.matches("[0-9]{7,8}")) {
            throw new IllegalArgumentException("Ingresá un DNI de 7 u 8 dígitos, sin puntos.");
        }

        if (!emailLimpio.isEmpty()
                && !emailLimpio.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Ingresá un correo electrónico válido.");
        }

        return new Huesped(
            nombreLimpio, apellidoLimpio, dniLimpio,
            emailLimpio.isEmpty() ? null : emailLimpio,
            telefonoLimpio.isEmpty() ? null : telefonoLimpio
        );
    }
}