package com.undec.acreditacion.infrastructure.persistence.postgres;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "institucion")
public class InstitucionJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "sigla", nullable = false, length = 50)
    private String sigla;

    @Column(name = "logo_path", nullable = false, length = 500)
    private String logoPath;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    @OneToMany(mappedBy = "institucion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AutoridadJpaEntity> autoridades = new ArrayList<>();

    @OneToOne(mappedBy = "institucion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SedeJpaEntity sede;

    public InstitucionJpaEntity() {
    }

    public InstitucionJpaEntity(UUID id, String nombre, String sigla, String logoPath,
                                boolean activo, Instant creadoEn, Instant actualizadoEn) {
        this.id = id;
        this.nombre = nombre;
        this.sigla = sigla;
        this.logoPath = logoPath;
        this.activo = activo;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : this.creadoEn;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Instant getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Instant actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public List<AutoridadJpaEntity> getAutoridades() {
        return autoridades;
    }

    public void setAutoridades(List<AutoridadJpaEntity> autoridades) {
        this.autoridades = autoridades;
    }

    public void addAutoridad(AutoridadJpaEntity autoridad) {
        autoridades.add(autoridad);
        autoridad.setInstitucion(this);
    }

    public SedeJpaEntity getSede() {
        return sede;
    }

    public void setSede(SedeJpaEntity sede) {
        this.sede = sede;
        if (sede != null) {
            sede.setInstitucion(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstitucionJpaEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
