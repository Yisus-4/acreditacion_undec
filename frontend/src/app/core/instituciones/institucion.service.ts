import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  InstitucionResponse,
  RegistrarInstitucionData,
} from './institucion.types';

export * from './institucion.types';

@Injectable({
  providedIn: 'root',
})
export class InstitucionService {
  private readonly http = inject(HttpClient);

  registrarInstitucion(formData: FormData): Observable<InstitucionResponse> {
    return this.http.post<InstitucionResponse>('/api/instituciones', formData);
  }

  listarInstituciones(): Observable<InstitucionResponse[]> {
    return this.http.get<InstitucionResponse[]>('/api/instituciones');
  }

  crearRegistroFormData(datos: RegistrarInstitucionData, logotipo?: File | null): FormData {
    const formData = new FormData();
    formData.append('datos', JSON.stringify(datos));
    if (logotipo) {
      formData.append('logotipo', logotipo, logotipo.name);
    }
    return formData;
  }
}
