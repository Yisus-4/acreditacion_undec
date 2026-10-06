package com.undec.acreditacion.infrastructure.persistence.postgres;

import com.undec.acreditacion.domain.entities.AutoridadInstitucional;
import com.undec.acreditacion.domain.entities.Institucion;
import com.undec.acreditacion.domain.entities.SedeCentral;
import com.undec.acreditacion.domain.entities.TipoAutoridad;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InstitucionPersistenceMapper {

    public Institucion toDomain(InstitucionJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        List<AutoridadInstitucional> autoridades = new ArrayList<>();
        if (entity.getAutoridades() != null) {
            for (AutoridadJpaEntity a : entity.getAutoridades()) {
                TipoAutoridad tipo = TipoAutoridad.valueOf(a.getTipoAutoridad());
                AutoridadInstitucional autoridad = AutoridadInstitucional.rehydrate(
                        a.getId(),
                        tipo,
                        a.getApellido(),
                        a.getPrimerNombre(),
                        a.getSegundoNombre(),
                        a.getTelefono(),
                        a.getEmail(),
                        a.getCargo(),
                        a.getAmbito(),
                        a.getCreadoEn()
                );
                autoridades.add(autoridad);
            }
        }

        SedeCentral sedeCentral = null;
        if (entity.getSede() != null) {
            SedeJpaEntity s = entity.getSede();
            sedeCentral = SedeCentral.rehydrate(
                    s.getId(),
                    s.isEsSedeCentral(),
                    s.getCalle(),
                    s.getNumero(),
                    s.getPiso(),
                    s.getDepartamento(),
                    s.getCodigoPostal(),
                    s.getProvincia() != null ? s.getProvincia().getId() : null,
                    s.getDepartamentoGeografico() != null ? s.getDepartamentoGeografico().getId() : null,
                    s.getLocalidad() != null ? s.getLocalidad().getId() : null,
                    s.getCreadoEn()
            );
        }

        return Institucion.rehydrate(
                entity.getId(),
                entity.getNombre(),
                entity.getSigla(),
                entity.getLogoPath(),
                entity.isActivo(),
                entity.getCreadoEn(),
                entity.getActualizadoEn(),
                autoridades,
                sedeCentral
        );
    }

    public InstitucionJpaEntity toEntity(Institucion domain) {
        if (domain == null) {
            return null;
        }

        InstitucionJpaEntity entity = new InstitucionJpaEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getSigla(),
                domain.getLogoPath(),
                domain.isActivo(),
                domain.getCreadoEn(),
                domain.getActualizadoEn()
        );

        if (domain.getAutoridades() != null) {
            for (AutoridadInstitucional a : domain.getAutoridades()) {
                AutoridadJpaEntity aEntity = new AutoridadJpaEntity(
                        a.getId(),
                        a.getTipoAutoridad().name(),
                        a.getApellido(),
                        a.getPrimerNombre(),
                        a.getSegundoNombre(),
                        a.getTelefono(),
                        a.getEmail(),
                        a.getCargo(),
                        a.getAmbito(),
                        a.getCreadoEn()
                );
                entity.addAutoridad(aEntity);
            }
        }

        if (domain.getSedeCentral() != null) {
            SedeCentral s = domain.getSedeCentral();
            SedeJpaEntity sEntity = new SedeJpaEntity();
            sEntity.setId(s.getId());
            sEntity.setEsSedeCentral(s.isEsSedeCentral());
            sEntity.setCalle(s.getCalle());
            sEntity.setNumero(s.getNumero());
            sEntity.setPiso(s.getPiso());
            sEntity.setDepartamento(s.getDepartamento());
            sEntity.setCodigoPostal(s.getCodigoPostal());
            sEntity.setCreadoEn(s.getCreadoEn());

            if (s.getProvinciaId() != null) {
                ProvinciaJpaEntity prov = new ProvinciaJpaEntity();
                prov.setId(s.getProvinciaId());
                sEntity.setProvincia(prov);
            }
            if (s.getDepartamentoId() != null) {
                DepartamentoJpaEntity depto = new DepartamentoJpaEntity();
                depto.setId(s.getDepartamentoId());
                sEntity.setDepartamentoGeografico(depto);
            }
            if (s.getLocalidadId() != null) {
                LocalidadJpaEntity loc = new LocalidadJpaEntity();
                loc.setId(s.getLocalidadId());
                sEntity.setLocalidad(loc);
            }

            entity.setSede(sEntity);
        }

        return entity;
    }
}
