import { Routes } from '@angular/router';

import { authGuard } from './core/auth/guards/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  {
    path: 'login',
    title: 'Acceso — Sistema de Acreditación UNdeC',
    loadComponent: () =>
      import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'dashboard',
    title: 'Dashboard — Sistema de Acreditación UNdeC',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/dashboard').then((m) => m.Dashboard),
  },
  {
    path: 'admin/users',
    title: 'Administración de Usuarios — Sistema de Acreditación UNdeC',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/admin/users/users').then((m) => m.UserManagement),
  },
  { path: '**', redirectTo: 'dashboard' },
];
