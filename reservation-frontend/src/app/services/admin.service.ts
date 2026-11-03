import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  AdminCategoria,
  AdminHorario,
  AdminServicio,
  AdminTurno,
  AdminUser,
} from '../model/admin.model';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  getUsuarios(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(`${this.baseUrl}/usuarios`);
  }

  getCategorias(): Observable<AdminCategoria[]> {
    return this.http.get<AdminCategoria[]>(`${this.baseUrl}/categorias`);
  }

  crearCategoria(payload: { nombre: string; descripcion: string }): Observable<AdminCategoria> {
    return this.http.post<AdminCategoria>(`${this.baseUrl}/categorias`, payload);
  }

  actualizarCategoria(
    id: number,
    payload: { nombre: string; descripcion: string },
  ): Observable<AdminCategoria> {
    return this.http.put<AdminCategoria>(`${this.baseUrl}/categorias/${id}`, payload);
  }

  eliminarCategoria(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/categorias/${id}`);
  }

  reactivarCategoria(id: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/categorias/${id}/reactivar`, {});
  }

  getServicios(): Observable<AdminServicio[]> {
    return this.http.get<AdminServicio[]>(`${this.baseUrl}/servicios`);
  }

  crearServicio(payload: {
    nombre: string;
    descripcion: string;
    duracion: number;
    categoriaId: number;
  }): Observable<AdminServicio> {
    return this.http.post<AdminServicio>(`${this.baseUrl}/servicios`, payload);
  }

  actualizarServicio(
    id: number,
    payload: { nombre: string; descripcion: string; duracion: number; categoriaId: number },
  ): Observable<AdminServicio> {
    return this.http.put<AdminServicio>(`${this.baseUrl}/servicios/${id}`, payload);
  }

  eliminarServicio(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/servicios/${id}`);
  }

  reactivarServicio(id: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/servicios/${id}/reactivar`, {});
  }

  getHorarios(): Observable<AdminHorario[]> {
    return this.http.get<AdminHorario[]>(`${this.baseUrl}/horarios`);
  }

  crearHorario(payload: {
    diaSemana: string;
    horaInicio: string;
    horaFin: string;
    servicioId: number;
  }): Observable<AdminHorario> {
    return this.http.post<AdminHorario>(`${this.baseUrl}/horarios`, payload);
  }

  actualizarHorario(
    id: number,
    payload: { diaSemana: string; horaInicio: string; horaFin: string; servicioId: number },
  ): Observable<AdminHorario> {
    return this.http.put<AdminHorario>(`${this.baseUrl}/horarios/${id}`, payload);
  }

  eliminarHorario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/horarios/${id}`);
  }

  reactivarHorario(id: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/horarios/${id}/reactivar`, {});
  }

  getTurnos(): Observable<AdminTurno[]> {
    return this.http.get<AdminTurno[]>(`${this.baseUrl}/turnos`);
  }

  generarTurnos(payload: { horarioId: number; fecha: string }): Observable<AdminTurno[]> {
    return this.http.post<AdminTurno[]>(`${this.baseUrl}/turnos`, payload);
  }

  eliminarTurno(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/turnos/${id}`);
  }

  reactivarTurno(id: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/turnos/${id}/reactivar`, {});
  }

  eliminarUsuario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/usuarios/${id}`);
  }

  reactivarUsuario(id: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/usuarios/${id}/reactivar`, {});
  }

  actualizarRol(id: number, rol: 'CLIENTE' | 'EMPLEADO' | 'ADMIN'): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/usuarios/${id}/rol`, { rol });
  }
}
