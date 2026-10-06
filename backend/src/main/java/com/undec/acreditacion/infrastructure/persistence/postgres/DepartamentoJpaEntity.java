package com.undec.acreditacion.infrastructure.persistence.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "geografia_departamento")
public class DepartamentoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provincia_id", nullable = false)
    private ProvinciaJpaEntity provincia;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    public DepartamentoJpaEntity() {
    }

    public DepartamentoJpaEntity(Long id, ProvinciaJpaEntity provincia, String nombre, Instant creadoEn) {
        this.id = id;
        this.provincia = provincia;
        this.nombre = nombre;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProvinciaJpaEntity getProvincia() {
        return provincia;
    }

    public void setProvincia(ProvinciaJpaEntity provincia) {
        this.provincia = provincia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
        if (!(o instanceof DepartamentoJpaEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
