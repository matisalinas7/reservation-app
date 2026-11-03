import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  CreateReservationRequest,
  MotivoCancelacion,
  Reservation,
} from '../model/reservation.model';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/reservas`;

  // ADMIN / EMPLEADO
  getAll(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.baseUrl);
  }

  // CLIENTE
  getMisReservas(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.baseUrl}/mis-reservas`);
  }

  crear(request: CreateReservationRequest): Observable<Reservation> {
    return this.http.post<Reservation>(this.baseUrl, request);
  }

  // En el backend cancelar es PUT /reservas/{id} (no DELETE) y el motivo es opcional
  cancelar(id: number, motivo: MotivoCancelacion | null = null): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/${id}`, { motivo });
  }
}
