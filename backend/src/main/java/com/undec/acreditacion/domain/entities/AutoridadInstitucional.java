package com.undec.acreditacion.domain.entities;

import com.undec.acreditacion.domain.exceptions.DomainValidationException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public final class AutoridadInstitucional {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UUID id;
    private final TipoAutoridad tipoAutoridad;
    private final String apellido;
    private final String primerNombre;
    private final String segundoNombre;
    private final String telefono;
    private final String email;
    private final String cargo;
    private final String ambito;
    private final Instant creadoEn;

    public static AutoridadInstitucional maximaAutoridad(String apellido,
                                                        String primerNombre,
                                                        String segundoNombre,
                                                        String telefono,
                                                        String email) {
        return new AutoridadInstitucional(
                UUID.randomUUID(),
                TipoAutoridad.MAXIMA_AUTORIDAD,
                apellido,
                primerNombre,
                segundoNombre,
                telefono,
                email,
                null,
                null,
                Instant.now()
        );
    }

    public static AutoridadInstitucional administradorInstitucional(String apellido,
                                                                   String primerNombre,
                                                                   String segundoNombre,
                                                                   String telefono,
                                                                   String email,
                                                                   String cargo,
                                                                   String ambito) {
        return new AutoridadInstitucional(
                UUID.randomUUID(),
                TipoAutoridad.ADMINISTRADOR_INSTITUCIONAL,
                apellido,
                primerNombre,
                segundoNombre,
                telefono,
                email,
                cargo,
                ambito,
                Instant.now()
        );
    }

    public static AutoridadInstitucional rehydrate(UUID id,
                                                  TipoAutoridad tipoAutoridad,
                                                  String apellido,
                                                  String primerNombre,
                                                  String segundoNombre,
                                                  String telefono,
                                                  String email,
                                                  String cargo,
                                                  String ambito,
                                                  Instant creadoEn) {
        return new AutoridadInstitucional(
                id,
                tipoAutoridad,
                apellido,
                primerNombre,
                segundoNombre,
                telefono,
                email,
                cargo,
                ambito,
                creadoEn
        );
    }

    public AutoridadInstitucional(UUID id,
                                  TipoAutoridad tipoAutoridad,
                                  String apellido,
                                  String primerNombre,
                                  String segundoNombre,
                                  String telefono,
                                  String email,
                                  String cargo,
                                  String ambito,
                                  Instant creadoEn) {
        this.id = Objects.requireNonNull(id, "El id de la autoridad es obligatorio");
        this.tipoAutoridad = Objects.requireNonNull(tipoAutoridad, "El tipo de autoridad es obligatorio");
        this.apellido = requireNonBlank(apellido, "El apellido de la autoridad es obligatorio");
        this.primerNombre = requireNonBlank(primerNombre, "El primer nombre de la autoridad es obligatorio");
        this.segundoNombre = segundoNombre != null && !segundoNombre.isBlank() ? segundoNombre.trim() : null;
        this.telefono = requireNonBlank(telefono, "El teléfono de la autoridad es obligatorio");
        this.email = validateEmail(email);

        if (tipoAutoridad == TipoAutoridad.ADMINISTRADOR_INSTITUCIONAL) {
            this.cargo = requireNonBlank(cargo, "El cargo del administrador institucional es obligatorio");
            this.ambito = requireNonBlank(ambito, "El ámbito del administrador institucional es obligatorio");
        } else {
            this.cargo = cargo != null && !cargo.isBlank() ? cargo.trim() : null;
            this.ambito = ambito != null && !ambito.isBlank() ? ambito.trim() : null;
        }

        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public TipoAutoridad getTipoAutoridad() {
        return tipoAutoridad;
    }

    public String getApellido() {
        return apellido;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public String getNombreCompleto() {
        if (segundoNombre != null && !segundoNombre.isBlank()) {
            return primerNombre + " " + segundoNombre + " " + apellido;
        }
        return primerNombre + " " + apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getCargo() {
        return cargo;
    }

    public String getAmbito() {
        return ambito;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }

    private static String validateEmail(String email) {
        String clean = requireNonBlank(email, "El correo electrónico es obligatorio");
        if (!EMAIL_PATTERN.matcher(clean).matches()) {
            throw new DomainValidationException("El correo electrónico '" + clean + "' tiene un formato inválido");
        }
        return clean;
    }
}
