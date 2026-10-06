package com.undec.acreditacion.domain.entities;

import com.undec.acreditacion.domain.exceptions.DomainValidationException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class SedeCentral {

    private final UUID id;
    private final boolean esSedeCentral;
    private final String calle;
    private final String numero;
    private final String piso;
    private final String departamento;
    private final String codigoPostal;
    private final Long provinciaId;
    private final Long departamentoId;
    private final Long localidadId;
    private final Instant creadoEn;

    public static SedeCentral crear(String calle,
                                    String numero,
                                    String piso,
                                    String departamento,
                                    String codigoPostal,
                                    Long provinciaId,
                                    Long departamentoId,
                                    Long localidadId) {
        return new SedeCentral(
                UUID.randomUUID(),
                true,
                calle,
                numero,
                piso,
                departamento,
                codigoPostal,
                provinciaId,
                departamentoId,
                localidadId,
                Instant.now()
        );
    }

    public static SedeCentral rehydrate(UUID id,
                                       boolean esSedeCentral,
                                       String calle,
                                       String numero,
                                       String piso,
                                       String departamento,
                                       String codigoPostal,
                                       Long provinciaId,
                                       Long departamentoId,
                                       Long localidadId,
                                       Instant creadoEn) {
        return new SedeCentral(
                id,
                esSedeCentral,
                calle,
                numero,
                piso,
                departamento,
                codigoPostal,
                provinciaId,
                departamentoId,
                localidadId,
                creadoEn
        );
    }

    public SedeCentral(UUID id,
                       boolean esSedeCentral,
                       String calle,
                       String numero,
                       String piso,
                       String departamento,
                       String codigoPostal,
                       Long provinciaId,
                       Long departamentoId,
                       Long localidadId,
                       Instant creadoEn) {
        this.id = Objects.requireNonNull(id, "El id de la sede es obligatorio");
        this.esSedeCentral = esSedeCentral;
        this.calle = requireNonBlank(calle, "La calle de la sede es obligatoria");
        this.numero = requireNonBlank(numero, "El número de la sede es obligatorio");
        this.piso = piso != null && !piso.isBlank() ? piso.trim() : null;
        this.departamento = departamento != null && !departamento.isBlank() ? departamento.trim() : null;
        this.codigoPostal = requireNonBlank(codigoPostal, "El código postal es obligatorio");
        this.provinciaId = Objects.requireNonNull(provinciaId, "La provincia es obligatoria");
        this.departamentoId = Objects.requireNonNull(departamentoId, "El departamento es obligatorio");
        this.localidadId = Objects.requireNonNull(localidadId, "La localidad es obligatoria");
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public boolean isEsSedeCentral() {
        return esSedeCentral;
    }

    public String getCalle() {
        return calle;
    }

    public String getNumero() {
        return numero;
    }

    public String getPiso() {
        return piso;
    }

    public String getDepartamento() {
        return departamento;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public Long getProvinciaId() {
        return provinciaId;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public Long getLocalidadId() {
        return localidadId;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public String getDireccionCompleta() {
        StringBuilder sb = new StringBuilder(calle).append(" ").append(numero);
        if (piso != null && !piso.isBlank()) {
            sb.append(", Piso ").append(piso);
        }
        if (departamento != null && !departamento.isBlank()) {
            sb.append(" Dpto ").append(departamento);
        }
        return sb.toString();
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}
