import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

import { Auth } from '../auth';

/**
 * Functional HTTP Interceptor for Angular 21.
 *
 * Attaches 'Authorization: Bearer <token>' to every outgoing request if an
 * active session exists. Automatically triggers signOut if an authenticated
 * request receives a 401 response.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(Auth);
  const token = auth.currentSession?.token;

  let request = req;
  if (token) {
    request = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !req.url.includes('/auth/login')) {
        auth.signOut();
      }
      return throwError(() => error);
    }),
  );
};
