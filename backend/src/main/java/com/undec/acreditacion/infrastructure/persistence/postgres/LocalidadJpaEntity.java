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
@Table(name = "geografia_localidad")
public class LocalidadJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false)
    private DepartamentoJpaEntity departamento;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "codigo_postal", nullable = false, length = 20)
    private String codigoPostal;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    public LocalidadJpaEntity() {
    }

    public LocalidadJpaEntity(Long id, DepartamentoJpaEntity departamento, String nombre,
                              String codigoPostal, Instant creadoEn) {
        this.id = id;
        this.departamento = departamento;
        this.nombre = nombre;
        this.codigoPostal = codigoPostal;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DepartamentoJpaEntity getDepartamento() {
        return departamento;
    }

    public void setDepartamento(DepartamentoJpaEntity departamento) {
        this.departamento = departamento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
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
        if (!(o instanceof LocalidadJpaEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
