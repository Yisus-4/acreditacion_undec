export interface Provincia {
  readonly id: number;
  readonly nombre: string;
  readonly codigo: string;
}

export interface Departamento {
  readonly id: number;
  readonly provinciaId: number;
  readonly nombre: string;
}

export interface Localidad {
  readonly id: number;
  readonly departamentoId: number;
  readonly nombre: string;
  readonly codigoPostal: string;
}
