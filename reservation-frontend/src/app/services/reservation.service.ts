import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CreateReservationRequest, Reservation } from '../model/reservation.model';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/reservas`;

  getAll(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.baseUrl);
  }

  crear(request: CreateReservationRequest): Observable<Reservation> {
    return this.http.post<Reservation>(this.baseUrl, request);
  }

  cancelar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
