import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  CreateUserPayload,
  RoleItem,
  UpdateUserPayload,
  UserItem,
} from './users.models';

export * from './users.models';

@Injectable({
  providedIn: 'root',
})
export class UsersService {
  private readonly http = inject(HttpClient);

  getUsers(): Observable<UserItem[]> {
    return this.http.get<UserItem[]>('/api/users');
  }

  getRoles(): Observable<RoleItem[]> {
    return this.http.get<RoleItem[]>('/api/roles');
  }

  createUser(payload: CreateUserPayload): Observable<UserItem> {
    return this.http.post<UserItem>('/api/users', payload);
  }

  updateUser(id: string, payload: UpdateUserPayload): Observable<UserItem> {
    return this.http.put<UserItem>(`/api/users/${id}`, payload);
  }

  toggleStatus(id: string, active: boolean): Observable<UserItem> {
    return this.http.patch<UserItem>(`/api/users/${id}/status`, { active });
  }
}