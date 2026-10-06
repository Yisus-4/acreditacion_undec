package com.undec.acreditacion.infrastructure.persistence.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "institucion_autoridad")
public class AutoridadJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institucion_id", nullable = false)
    private InstitucionJpaEntity institucion;

    @Column(name = "tipo_autoridad", nullable = false, length = 50)
    private String tipoAutoridad;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "primer_nombre", nullable = false, length = 100)
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 100)
    private String segundoNombre;

    @Column(name = "telefono", nullable = false, length = 50)
    private String telefono;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "cargo", length = 150)
    private String cargo;

    @Column(name = "ambito", length = 150)
    private String ambito;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    public AutoridadJpaEntity() {
    }

    public AutoridadJpaEntity(UUID id, String tipoAutoridad, String apellido, String primerNombre,
                              String segundoNombre, String telefono, String email, String cargo,
                              String ambito, Instant creadoEn) {
        this.id = id;
        this.tipoAutoridad = tipoAutoridad;
        this.apellido = apellido;
        this.primerNombre = primerNombre;
        this.segundoNombre = segundoNombre;
        this.telefono = telefono;
        this.email = email;
        this.cargo = cargo;
        this.ambito = ambito;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public InstitucionJpaEntity getInstitucion() {
        return institucion;
    }

    public void setInstitucion(InstitucionJpaEntity institucion) {
        this.institucion = institucion;
    }

    public String getTipoAutoridad() {
        return tipoAutoridad;
    }

    public void setTipoAutoridad(String tipoAutoridad) {
        this.tipoAutoridad = tipoAutoridad;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getAmbito() {
        return ambito;
    }

    public void setAmbito(String ambito) {
        this.ambito = ambito;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AutoridadJpaEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
