export interface AutoridadResponse {
  readonly id: string;
  readonly tipoAutoridad: 'MAXIMA_AUTORIDAD' | 'ADMINISTRADOR_INSTITUCIONAL' | string;
  readonly apellido: string;
  readonly primerNombre: string;
  readonly segundoNombre?: string | null;
  readonly nombreCompleto: string;
  readonly telefono: string;
  readonly email: string;
  readonly cargo?: string | null;
  readonly ambito?: string | null;
  readonly creadoEn: string;
}

export interface SedeResponse {
  readonly id: string;
  readonly esSedeCentral: boolean;
  readonly calle: string;
  readonly numero: string;
  readonly piso?: string | null;
  readonly departamento?: string | null;
  readonly codigoPostal: string;
  readonly provinciaId: number;
  readonly departamentoId: number;
  readonly localidadId: number;
  readonly direccionCompleta: string;
  readonly creadoEn: string;
}

export interface InstitucionResponse {
  readonly id: string;
  readonly nombre: string;
  readonly sigla: string;
  readonly logoPath?: string | null;
  readonly logoUrl?: string | null;
  readonly activo: boolean;
  readonly creadoEn: string;
  readonly actualizadoEn: string;
  readonly maximaAutoridad?: AutoridadResponse | null;
  readonly administradorInstitucional?: AutoridadResponse | null;
  readonly sedeCentral?: SedeResponse | null;
}

export interface AutoridadPayload {
  readonly apellido: string;
  readonly primerNombre: string;
  readonly segundoNombre?: string | null;
  readonly telefono: string;
  readonly email: string;
  readonly cargo?: string | null;
  readonly ambito?: string | null;
}

export interface SedePayload {
  readonly calle: string;
  readonly numero: string;
  readonly piso?: string | null;
  readonly departamento?: string | null;
  readonly codigoPostal: string;
  readonly provinciaId: number;
  readonly departamentoId: number;
  readonly localidadId: number;
}

export interface RegistrarInstitucionData {
  readonly nombre: string;
  readonly sigla: string;
  readonly maximaAutoridad: AutoridadPayload;
  readonly administradorInstitucional: AutoridadPayload;
  readonly sedeCentral: SedePayload;
}
