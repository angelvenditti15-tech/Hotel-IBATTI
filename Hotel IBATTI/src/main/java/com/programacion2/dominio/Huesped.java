package com.programacion2.dominio;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Unique;

@PersistenceCapable(detachable = "true")
public class Huesped extends Persona {

    @Unique
    @Column(allowsNull = "false")
    private String dni;

    private String email;
    private String telefono;

    public Huesped() {
        super();
    }

    public Huesped(String nombre, String apellido, String dni,
                   String email, String telefono) {
        super(nombre, apellido);
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}