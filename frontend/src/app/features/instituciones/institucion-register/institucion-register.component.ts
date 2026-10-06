import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { catchError, finalize, of, switchMap, tap } from 'rxjs';

import {
  GeografiaService,
  Departamento,
  Localidad,
  Provincia,
} from '../../../core/geografia/geografia.service';
import { InstitucionService } from '../../../core/instituciones/institucion.service';
import { RegistrarInstitucionData } from '../../../core/instituciones/institucion.types';

const EMAIL_PATTERN = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;

@Component({
  selector: 'app-institucion-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './institucion-register.component.html',
  styleUrl: './institucion-register.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InstitucionRegisterComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly institucionService = inject(InstitucionService);
  private readonly geografiaService = inject(GeografiaService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  // Status signals
  readonly submitting = signal<boolean>(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  // Geographic signals
  readonly provincias = signal<Provincia[]>([]);
  readonly departamentos = signal<Departamento[]>([]);
  readonly localidades = signal<Localidad[]>([]);
  readonly loadingProvincias = signal<boolean>(false);
  readonly loadingDepartamentos = signal<boolean>(false);
  readonly loadingLocalidades = signal<boolean>(false);

  // Logo file signals
  readonly selectedFile = signal<File | null>(null);
  readonly previewUrl = signal<string | null>(null);
  readonly fileError = signal<string | null>(null);
  readonly isDragging = signal<boolean>(false);

  // Reactive Form
  readonly form = this.fb.group({
    nombre: ['', [Validators.required, Validators.maxLength(255)]],
    sigla: ['', [Validators.required, Validators.maxLength(50)]],
    maximaAutoridad: this.fb.group({
      apellido: ['', [Validators.required, Validators.maxLength(100)]],
      primerNombre: ['', [Validators.required, Validators.maxLength(100)]],
      segundoNombre: ['', [Validators.maxLength(100)]],
      telefono: ['', [Validators.required, Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.pattern(EMAIL_PATTERN), Validators.maxLength(320)]],
    }),
    administradorInstitucional: this.fb.group({
      apellido: ['', [Validators.required, Validators.maxLength(100)]],
      primerNombre: ['', [Validators.required, Validators.maxLength(100)]],
      segundoNombre: ['', [Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.pattern(EMAIL_PATTERN), Validators.maxLength(320)]],
      cargo: ['', [Validators.required, Validators.maxLength(150)]],
      ambito: ['', [Validators.required, Validators.maxLength(150)]],
      telefono: ['', [Validators.required, Validators.maxLength(50)]],
    }),
    sedeCentral: this.fb.group({
      calle: ['', [Validators.required, Validators.maxLength(255)]],
      numero: ['', [Validators.required, Validators.maxLength(50)]],
      piso: ['', [Validators.maxLength(20)]],
      departamento: ['', [Validators.maxLength(20)]],
      codigoPostal: ['', [Validators.required, Validators.maxLength(20)]],
      provinciaId: [null as number | null, [Validators.required]],
      departamentoId: [{ value: null as number | null, disabled: true }, [Validators.required]],
      localidadId: [{ value: null as number | null, disabled: true }, [Validators.required]],
    }),
  });

  ngOnInit(): void {
    this.cargarProvincias();
    this.setupGeografiaCascade();
  }

  // Helper getters for template
  get maximaAutoridadGroup(): FormGroup {
    return this.form.get('maximaAutoridad') as FormGroup;
  }

  get administradorGroup(): FormGroup {
    return this.form.get('administradorInstitucional') as FormGroup;
  }

  get sedeCentralGroup(): FormGroup {
    return this.form.get('sedeCentral') as FormGroup;
  }

  isFieldInvalid(control: FormControl | null): boolean {
    return !!(control && control.invalid && (control.touched || control.dirty));
  }

  // --- Carga de catálogos geográficos ---
  cargarProvincias(): void {
    this.loadingProvincias.set(true);
    this.geografiaService
      .obtenerProvincias()
      .pipe(
        finalize(() => this.loadingProvincias.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (provincias) => this.provincias.set(provincias),
        error: (err) => {
          console.error('Error al cargar provincias:', err);
          this.errorMessage.set('No se pudieron cargar las provincias.');
        },
      });
  }

  private setupGeografiaCascade(): void {
    const provinciaControl = this.form.get('sedeCentral.provinciaId');
    const deptoControl = this.form.get('sedeCentral.departamentoId');
    const locControl = this.form.get('sedeCentral.localidadId');

    // Asegurar estado inicial deshabilitado
    deptoControl?.disable();
    locControl?.disable();

    // Cambio en Provincia -> Cargar Departamentos y resetear Localidad
    provinciaControl?.valueChanges
      .pipe(
        tap(() => {
          this.departamentos.set([]);
          this.localidades.set([]);
          deptoControl?.setValue(null, { emitEvent: false });
          locControl?.setValue(null, { emitEvent: false });
          deptoControl?.disable();
          locControl?.disable();
        }),
        switchMap((provinciaId) => {
          if (!provinciaId) {
            this.loadingDepartamentos.set(false);
            return of<Departamento[]>([]);
          }
          this.loadingDepartamentos.set(true);
          return this.geografiaService.obtenerDepartamentos(Number(provinciaId)).pipe(
            finalize(() => this.loadingDepartamentos.set(false)),
            catchError((err) => {
              console.error('Error al cargar departamentos:', err);
              this.errorMessage.set('No se pudieron cargar los departamentos.');
              return of<Departamento[]>([]);
            })
          );
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((deps) => {
        this.departamentos.set(deps);
        if (deps.length > 0) {
          deptoControl?.enable();
        } else {
          deptoControl?.disable();
        }
      });

    // Cambio en Departamento -> Cargar Localidades
    deptoControl?.valueChanges
      .pipe(
        tap(() => {
          this.localidades.set([]);
          locControl?.setValue(null, { emitEvent: false });
          locControl?.disable();
        }),
        switchMap((departamentoId) => {
          if (!departamentoId) {
            this.loadingLocalidades.set(false);
            return of<Localidad[]>([]);
          }
          this.loadingLocalidades.set(true);
          return this.geografiaService.obtenerLocalidades(Number(departamentoId)).pipe(
            finalize(() => this.loadingLocalidades.set(false)),
            catchError((err) => {
              console.error('Error al cargar localidades:', err);
              this.errorMessage.set('No se pudieron cargar las localidades.');
              return of<Localidad[]>([]);
            })
          );
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((locs) => {
        this.localidades.set(locs);
        if (locs.length > 0) {
          locControl?.enable();
        } else {
          locControl?.disable();
        }
      });

    // Cambio en Localidad -> Auto-sugerir Código Postal
    locControl?.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((localidadId) => {
        if (!localidadId) return;
        const loc = this.localidades().find((l) => l.id === Number(localidadId));
        if (loc?.codigoPostal) {
          const cpControl = this.form.get('sedeCentral.codigoPostal');
          if (!cpControl?.value) {
            cpControl?.setValue(loc.codigoPostal);
          }
        }
      });
  }

  // --- Manejo del logotipo ---
  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging.set(true);
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging.set(false);
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging.set(false);

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.procesarArchivo(files[0]);
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.procesarArchivo(input.files[0]);
    }
  }

  private procesarArchivo(file: File): void {
    this.fileError.set(null);

    // Validación de extensión y mime type
    const validExtensions = ['.png', '.jpg', '.jpeg'];
    const validMimes = ['image/png', 'image/jpeg', 'image/jpg'];
    const extension = '.' + file.name.split('.').pop()?.toLowerCase();

    if (!validExtensions.includes(extension) || !validMimes.includes(file.type)) {
      this.fileError.set('Formato no válido. Debe adjuntar un archivo PNG o JPG.');
      this.selectedFile.set(null);
      this.previewUrl.set(null);
      return;
    }

    // Tamaño máximo sugerido (5 MB)
    if (file.size > 5 * 1024 * 1024) {
      this.fileError.set('El archivo no puede superar los 5 MB de tamaño.');
      this.selectedFile.set(null);
      this.previewUrl.set(null);
      return;
    }

    this.selectedFile.set(file);

    // Generar preview visual inmediato
    const reader = new FileReader();
    reader.onload = () => {
      this.previewUrl.set(reader.result as string);
    };
    reader.readAsDataURL(file);
  }

  eliminarArchivo(): void {
    this.selectedFile.set(null);
    this.previewUrl.set(null);
    this.fileError.set(null);
  }

  // --- Envío del formulario ---
  guardar(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const file = this.selectedFile();

    // Validación según CU-ADM-01.1.1 (5.A: Campos obligatorios y logotipo)
    if (this.form.invalid || !file) {
      this.form.markAllAsTouched();
      if (!file) {
        this.fileError.set('Debe adjuntar el logotipo de la institución.');
      }
      this.errorMessage.set(
        'Debe completar todos los campos obligatorios y adjuntar un formato de imagen válido'
      );
      // Scroll to top
      window.scrollTo({ top: 0, behavior: 'smooth' });
      return;
    }

    this.submitting.set(true);

    const formValues = this.form.getRawValue();

    const requestData: RegistrarInstitucionData = {
      nombre: formValues.nombre?.trim() ?? '',
      sigla: formValues.sigla?.trim() ?? '',
      maximaAutoridad: {
        apellido: formValues.maximaAutoridad.apellido?.trim() ?? '',
        primerNombre: formValues.maximaAutoridad.primerNombre?.trim() ?? '',
        segundoNombre: formValues.maximaAutoridad.segundoNombre?.trim() || null,
        telefono: formValues.maximaAutoridad.telefono?.trim() ?? '',
        email: formValues.maximaAutoridad.email?.trim() ?? '',
      },
      administradorInstitucional: {
        apellido: formValues.administradorInstitucional.apellido?.trim() ?? '',
        primerNombre: formValues.administradorInstitucional.primerNombre?.trim() ?? '',
        segundoNombre: formValues.administradorInstitucional.segundoNombre?.trim() || null,
        telefono: formValues.administradorInstitucional.telefono?.trim() ?? '',
        email: formValues.administradorInstitucional.email?.trim() ?? '',
        cargo: formValues.administradorInstitucional.cargo?.trim() ?? '',
        ambito: formValues.administradorInstitucional.ambito?.trim() ?? '',
      },
      sedeCentral: {
        calle: formValues.sedeCentral.calle?.trim() ?? '',
        numero: formValues.sedeCentral.numero?.trim() ?? '',
        piso: formValues.sedeCentral.piso?.trim() || null,
        departamento: formValues.sedeCentral.departamento?.trim() || null,
        codigoPostal: formValues.sedeCentral.codigoPostal?.trim() ?? '',
        provinciaId: Number(formValues.sedeCentral.provinciaId),
        departamentoId: Number(formValues.sedeCentral.departamentoId),
        localidadId: Number(formValues.sedeCentral.localidadId),
      },
    };

    const formData = this.institucionService.crearRegistroFormData(requestData, file);

    this.institucionService
      .registrarInstitucion(formData)
      .pipe(
        finalize(() => this.submitting.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: () => {
          this.successMessage.set('Institución registrada correctamente');
          // Redirección automática a /admin/instituciones (Paso 8 del CU)
          setTimeout(() => {
            void this.router.navigate(['/admin/instituciones']);
          }, 1500);
        },
        error: (err) => {
          if (err.status === 409) {
            // 6.A Duplicidad
            this.errorMessage.set(
              'La institución ingresada ya se encuentra registrada en el sistema'
            );
          } else {
            const backendMsg = err.error?.error;
            this.errorMessage.set(
              backendMsg ||
                'Ocurrió un error al registrar la institución. Por favor, intente nuevamente.'
            );
          }
          window.scrollTo({ top: 0, behavior: 'smooth' });
        },
      });
  }

  // --- 3.A Flujo alternativo: Cancelar ---
  cancelar(): void {
    void this.router.navigate(['/admin/instituciones']);
  }
}
