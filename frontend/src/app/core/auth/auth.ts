import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { delay } from 'rxjs/operators';

import { Credentials, Session } from './auth.models';

/**
 * `Auth` es el servicio singleton de autenticación.
 *
 * **Estado actual**: implementación MOCK temporal y aislada. El backend Spring
 * todavía no expone un endpoint de autenticación ni JWT, por lo que este
 * servicio NO realiza llamadas HTTP reales. La sección marcada como
 * `MOCK — reemplazar por HTTP` es lo único que debe cambiarse cuando el backend
 * esté listo; el resto del contrato (sesión, observables, signOut) se conserva.
 *
 * Sustitución futura:
 *   return this.http.post<SessionResponse>('/auth/login', credentials).pipe(
 *     map(toSession), tap((s) => this.session$.next(s)),
 *   );
 */
@Injectable({
  providedIn: 'root',
})
export class Auth {
  private readonly session$ = new BehaviorSubject<Session | null>(null);

  /** Observable de la sesión actual (null cuando no hay sesión). */
  readonly currentSession$: Observable<Session | null> = this.session$.asObservable();

  get currentSession(): Session | null {
    return this.session$.value;
  }

  get isAuthenticated(): boolean {
    return this.session$.value !== null;
  }

  /**
   * Autentica credenciales. Devuelve un `Observable<Session>` que emite la
   * sesión en éxito o un `InvalidCredentialsError` en fracaso.
   *
   * La latencia simulada existe para que el UI ejercite el estado `loading`.
   */
  signIn(credentials: Credentials): Observable<Session> {
    // ────────────────────────────────────────────────────────────────────────
    // MOCK — reemplazar por HTTP real cuando el backend exponga /auth/login.
    // No usar este bloque en producción: las credenciales viven en el cliente.
    // ────────────────────────────────────────────────────────────────────────
    const match = MOCK_USERS.find(
      (u) => u.email === credentials.email && u.password === credentials.password,
    );
    // ────────────────────────────────────────────────────────────────────────

    if (!match) {
      return throwError(() => new InvalidCredentialsError()).pipe(delay(MOCK_LATENCY_MS));
    }

    const session: Session = {
      user: {
        id: match.id,
        email: match.email,
        displayName: match.displayName,
        roleCodes: match.roleCodes,
      },
      token: `mock-token-${Date.now()}`,
      issuedAt: Date.now(),
    };
    this.session$.next(session);
    return of(session).pipe(delay(MOCK_LATENCY_MS));
  }

  /** Cierra la sesión actual. */
  signOut(): void {
    this.session$.next(null);
  }
}

/** Error de dominio para credenciales inválidas (distinto de un error de red). */
export class InvalidCredentialsError extends Error {
  constructor() {
    super('Credenciales inválidas');
    this.name = 'InvalidCredentialsError';
    Object.setPrototypeOf(this, InvalidCredentialsError.prototype);
  }
}

// ────────────────────────────────────────────────────────────────────────────
// MOCK — datos de demo. Eliminar al conectar el backend real.
// ────────────────────────────────────────────────────────────────────────────
const MOCK_LATENCY_MS = 700;

interface MockUser {
  readonly id: string;
  readonly email: string;
  readonly password: string;
  readonly displayName: string;
  readonly roleCodes: readonly string[];
}

const MOCK_USERS: readonly MockUser[] = [
  {
    id: '1',
    email: 'coordinadora@undec.edu.ar',
    password: 'undec2026',
    displayName: 'Coordinadora de Acreditación',
    roleCodes: ['COORDINADORA_AC'],
  },
  {
    id: '2',
    email: 'dea@undec.edu.ar',
    password: 'undec2026',
    displayName: 'Director/a de Carrera',
    roleCodes: ['DEA'],
  },
];
