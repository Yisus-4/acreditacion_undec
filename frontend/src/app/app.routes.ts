import { Routes } from '@angular/router';

/**
 * Rutas mínimas de la Fase 1.
 *
 * El login es la pantalla inicial. No existe dashboard todavía, por lo que el
 * resto de rutas redirigen a `/login`. Cuando se apruebe el dashboard y el
 * backend exponga autenticación, se añadirán rutas protegidas con `canMatch`.
 */
export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  {
    path: 'login',
    title: 'Acceso — Sistema de Acreditación UNdeC',
    loadComponent: () =>
      import('./features/auth/login/login').then((m) => m.Login),
  },
  { path: '**', redirectTo: 'login' },
];
