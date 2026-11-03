import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Categoria, Servicio, TurnoDisponible } from '../model/catalog.model';

@Injectable({
  providedIn: 'root',
})
export class CatalogService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  getCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(`${this.baseUrl}/categorias/disponibles`);
  }

  getServicios(): Observable<Servicio[]> {
    return this.http.get<Servicio[]>(`${this.baseUrl}/servicios/disponibles`);
  }

  getTurnosDisponibles(servicioId: number, fecha: string): Observable<TurnoDisponible[]> {
    return this.http.get<TurnoDisponible[]>(`${this.baseUrl}/turnos/servicio/${servicioId}`, {
      params: { fecha },
    });
  }
}
