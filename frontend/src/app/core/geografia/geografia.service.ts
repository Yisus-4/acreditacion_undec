import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Departamento, Localidad, Provincia } from './geografia.types';

export * from './geografia.types';

@Injectable({
  providedIn: 'root',
})
export class GeografiaService {
  private readonly http = inject(HttpClient);

  obtenerProvincias(): Observable<Provincia[]> {
    return this.http.get<Provincia[]>('/api/geografia/provincias');
  }

  obtenerDepartamentos(provinciaId: string | number): Observable<Departamento[]> {
    return this.http.get<Departamento[]>(`/api/geografia/provincias/${provinciaId}/departamentos`);
  }

  obtenerLocalidades(departamentoId: string | number): Observable<Localidad[]> {
    return this.http.get<Localidad[]>(`/api/geografia/departamentos/${departamentoId}/localidades`);
  }
}
