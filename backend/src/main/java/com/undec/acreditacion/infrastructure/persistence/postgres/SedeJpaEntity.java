package com.undec.acreditacion.infrastructure.persistence.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "institucion_sede")
public class SedeJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institucion_id", nullable = false)
    private InstitucionJpaEntity institucion;

    @Column(name = "es_sede_central", nullable = false)
    private boolean esSedeCentral;

    @Column(name = "calle", nullable = false)
    private String calle;

    @Column(name = "numero", nullable = false, length = 50)
    private String numero;

    @Column(name = "piso", length = 20)
    private String piso;

    @Column(name = "departamento", length = 20)
    private String departamento;

    @Column(name = "codigo_postal", nullable = false, length = 20)
    private String codigoPostal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provincia_id", nullable = false)
    private ProvinciaJpaEntity provincia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false)
    private DepartamentoJpaEntity departamentoGeografico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localidad_id", nullable = false)
    private LocalidadJpaEntity localidad;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    public SedeJpaEntity() {
    }

    public SedeJpaEntity(UUID id, boolean esSedeCentral, String calle, String numero,
                         String piso, String departamento, String codigoPostal,
                         ProvinciaJpaEntity provincia, DepartamentoJpaEntity departamentoGeografico,
                         LocalidadJpaEntity localidad, Instant creadoEn) {
        this.id = id;
        this.esSedeCentral = esSedeCentral;
        this.calle = calle;
        this.numero = numero;
        this.piso = piso;
        this.departamento = departamento;
        this.codigoPostal = codigoPostal;
        this.provincia = provincia;
        this.departamentoGeografico = departamentoGeografico;
        this.localidad = localidad;
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

    public boolean isEsSedeCentral() {
        return esSedeCentral;
    }

    public void setEsSedeCentral(boolean esSedeCentral) {
        this.esSedeCentral = esSedeCentral;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPiso() {
        return piso;
    }

    public void setPiso(String piso) {
        this.piso = piso;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public ProvinciaJpaEntity getProvincia() {
        return provincia;
    }

    public void setProvincia(ProvinciaJpaEntity provincia) {
        this.provincia = provincia;
    }

    public DepartamentoJpaEntity getDepartamentoGeografico() {
        return departamentoGeografico;
    }

    public void setDepartamentoGeografico(DepartamentoJpaEntity departamentoGeografico) {
        this.departamentoGeografico = departamentoGeografico;
    }

    public LocalidadJpaEntity getLocalidad() {
        return localidad;
    }

    public void setLocalidad(LocalidadJpaEntity localidad) {
        this.localidad = localidad;
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
        if (!(o instanceof SedeJpaEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
