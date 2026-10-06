import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { InstitucionService } from '../../../core/instituciones/institucion.service';
import { InstitucionResponse } from '../../../core/instituciones/institucion.types';

@Component({
  selector: 'app-institucion-list',
  imports: [RouterLink],
  templateUrl: './institucion-list.component.html',
  styleUrl: './institucion-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InstitucionListComponent implements OnInit {
  private readonly institucionService = inject(InstitucionService);
  private readonly destroyRef = inject(DestroyRef);

  readonly instituciones = signal<InstitucionResponse[]>([]);
  readonly loading = signal<boolean>(true);
  readonly errorMessage = signal<string | null>(null);
  readonly searchQuery = signal<string>('');
  readonly imageErrorMap = signal<Record<string, boolean>>({});

  ngOnInit(): void {
    this.cargarInstituciones();
  }

  cargarInstituciones(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.institucionService
      .listarInstituciones()
      .pipe(
        finalize(() => this.loading.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (items) => this.instituciones.set(items),
        error: (err) => {
          console.error('Error al listar instituciones:', err);
          this.errorMessage.set(
            'No se pudo cargar el listado de instituciones. Por favor, intente nuevamente.'
          );
        },
      });
  }

  onSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchQuery.set(input.value);
  }

  filteredInstituciones(): InstitucionResponse[] {
    const q = this.searchQuery().toLowerCase().trim();
    if (!q) {
      return this.instituciones();
    }
    return this.instituciones().filter((inst) => {
      const nombre = inst.nombre.toLowerCase();
      const sigla = inst.sigla.toLowerCase();
      const rector = inst.maximaAutoridad?.nombreCompleto?.toLowerCase() ?? '';
      const direccion = inst.sedeCentral?.direccionCompleta?.toLowerCase() ?? '';
      return (
        nombre.includes(q) ||
        sigla.includes(q) ||
        rector.includes(q) ||
        direccion.includes(q)
      );
    });
  }

  handleImageError(institucionId: string): void {
    this.imageErrorMap.update((map) => ({ ...map, [institucionId]: true }));
  }

  isImageBroken(institucionId: string): boolean {
    return !!this.imageErrorMap()[institucionId];
  }
}
