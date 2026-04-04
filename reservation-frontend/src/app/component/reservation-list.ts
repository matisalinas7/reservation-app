import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Reservation, ReservationStatus } from '../model/reservation.model';
import { ReservationService } from '../services/reservation.service';
import { ToastService } from '../services/toast.service';
import { formatHttpError } from '../utils/http-error.util';

@Component({
  selector: 'app-reservation-list',
  imports: [CommonModule, RouterLink],
  templateUrl: './reservation-list.html',
  styleUrl: './reservation-list.css',
})
export class ReservationListComponent implements OnInit {
  private readonly reservationService = inject(ReservationService);
  private readonly toast = inject(ToastService);

  readonly reservations = signal<Reservation[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly cancellingId = signal<number | null>(null);

  readonly ReservationStatus = ReservationStatus;

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.reservationService.getAll().subscribe({
      next: (data) => {
        this.reservations.set(data);
        this.loading.set(false);
        this.error.set(null);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(formatHttpError(err));
      },
    });
  }

  cancelar(reservation: Reservation): void {
    if (reservation.status !== ReservationStatus.ACTIVE) {
      return;
    }
    this.cancellingId.set(reservation.id);
    this.reservationService.cancelar(reservation.id).subscribe({
      next: () => {
        this.cancellingId.set(null);
        this.toast.showSuccess('Reserva cancelada correctamente.');
        this.load();
      },
      error: (err: HttpErrorResponse) => {
        this.cancellingId.set(null);
        this.toast.showError(formatHttpError(err));
      },
    });
  }

  puedeCancelar(r: Reservation): boolean {
    return r.status === ReservationStatus.ACTIVE;
  }
}
