import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Reservation, ReservationStatus } from '../model/reservation.model';
import { AuthService } from '../services/auth.service';
import { ReservationService } from '../services/reservation.service';
import { ToastService } from '../services/toast.service';
import { formatHttpError } from '../utils/http-error.util';
import { hhmm } from '../utils/horario.util';

@Component({
  selector: 'app-reservation-list',
  imports: [RouterLink],
  templateUrl: './reservation-list.html',
  styleUrl: './reservation-list.css',
})
export class ReservationListComponent implements OnInit {
  private readonly reservationService = inject(ReservationService);
  private readonly authService = inject(AuthService);
  private readonly toast = inject(ToastService);

  readonly hhmm = hhmm;
  readonly ReservationStatus = ReservationStatus;

  readonly esStaff = computed(() => this.authService.hasAnyRole('ADMIN', 'EMPLEADO'));
  readonly puedeReservar = computed(() => this.authService.hasAnyRole('CLIENTE', 'EMPLEADO'));

  readonly reservations = signal<Reservation[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly cancellingId = signal<number | null>(null);

  // Más recientes primero (se ordena una copia para no mutar el signal)
  readonly ordenadas = computed(() =>
    [...this.reservations()].sort((a, b) =>
      (b.fecha + b.horaInicio).localeCompare(a.fecha + a.horaInicio),
    ),
  );

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);
    const request$ = this.esStaff()
      ? this.reservationService.getAll()
      : this.reservationService.getMisReservas();

    request$.subscribe({
      next: (data) => {
        this.reservations.set(data);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(formatHttpError(err));
      },
    });
  }

  puedeCancelar(r: Reservation): boolean {
    return r.estado === ReservationStatus.ACTIVE;
  }

  cancelar(reservation: Reservation): void {
    if (!confirm('¿Cancelar esta reserva?')) {
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
}
