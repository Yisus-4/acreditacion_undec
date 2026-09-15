import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';

import { Credentials, Session } from './auth.models';

/**
 * `Auth` es el servicio singleton de autenticación.
 *
 * Se conecta al backend Spring Boot mediante el endpoint HTTP `/api/auth/login`.
 * En desarrollo, las peticiones a `/api/*` son redirigidas por proxy.conf.json a http://localhost:8080.
 */
@Injectable({
  providedIn: 'root',
})
export class Auth {
  private readonly http = inject(HttpClient);
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
   * Autentica credenciales contra el backend.
   * Devuelve un `Observable<Session>` que emite la sesión en éxito o un
   * `InvalidCredentialsError` en 401.
   */
  signIn(credentials: Credentials): Observable<Session> {
    return this.http.post<Session>('/api/auth/login', credentials).pipe(
      tap((session) => this.session$.next(session)),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          return throwError(() => new InvalidCredentialsError());
        }
        return throwError(() => error);
      }),
    );
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
