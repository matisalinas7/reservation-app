import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { NgTemplateOutlet } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  ESTADOS_RESERVA,
  MOTIVOS_CANCELACION,
  MotivoCancelacion,
  Reservation,
  ReservationStatus,
} from '../model/reservation.model';
import { AuthService } from '../services/auth.service';
import { ReservationService } from '../services/reservation.service';
import { ToastService } from '../services/toast.service';
import { formatHttpError } from '../utils/http-error.util';
import { FechaBloqueComponent } from './fecha-bloque/fecha-bloque';
import {
  diaCorto,
  diaDelMes,
  fechaLarga,
  hhmm,
  horaActual,
  hoyISO,
  mesCorto,
} from '../utils/horario.util';

@Component({
  selector: 'app-reservation-list',
  imports: [RouterLink, NgTemplateOutlet, FechaBloqueComponent],
  templateUrl: './reservation-list.html',
  styleUrl: './reservation-list.css',
})
export class ReservationListComponent implements OnInit {
  private readonly reservationService = inject(ReservationService);
  private readonly authService = inject(AuthService);
  private readonly toast = inject(ToastService);

  readonly hhmm = hhmm;
  readonly ReservationStatus = ReservationStatus;
  readonly MOTIVOS = MOTIVOS_CANCELACION;

  readonly esStaff = computed(() => this.authService.hasAnyRole('ADMIN', 'EMPLEADO'));
  readonly puedeReservar = computed(() => this.authService.hasAnyRole('CLIENTE', 'EMPLEADO'));

  readonly reservations = signal<Reservation[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  /** Fila que muestra el select de motivo (null = ninguna). */
  readonly confirmandoId = signal<number | null>(null);
  /** Motivo elegido en la fila que se está confirmando. */
  readonly motivo = signal<MotivoCancelacion | null>(null);
  /** Fila con el request de cancelación en curso. */
  readonly cancellingId = signal<number | null>(null);

  // Más recientes primero (se ordena una copia para no mutar el signal)
  readonly ordenadas = computed(() =>
    [...this.reservations()].sort((a, b) =>
      (b.fecha + b.horaInicio).localeCompare(a.fecha + a.horaInicio),
    ),
  );

  // ---- Solo para la vista del CLIENTE (tarjetas) ----

  private readonly hoy = hoyISO();

  /** Activas que todavía no empezaron: de la más cercana a la más lejana. */
  readonly proximas = computed(() => {
    const ahora = horaActual();
    return this.ordenadas()
      .filter((r) => this.esProxima(r, ahora))
      .reverse();
  });

  /** Todo lo demás (canceladas, completadas y activas que ya pasaron): más recientes primero. */
  readonly historial = computed(() => {
    const ahora = horaActual();
    return this.ordenadas().filter((r) => !this.esProxima(r, ahora));
  });

  ngOnInit(): void {
    this.load();
  }

  private esProxima(r: Reservation, ahora: string): boolean {
    return (
      r.estado === ReservationStatus.ACTIVE &&
      (r.fecha > this.hoy || (r.fecha === this.hoy && hhmm(r.horaInicio) >= ahora))
    );
  }

  etiquetaEstado(valor: ReservationStatus): string {
    return ESTADOS_RESERVA.find((e) => e.valor === valor)?.etiqueta ?? valor;
  }

  fechaLarga(fecha: string): string {
    return fechaLarga(fecha);
  }

  /** Para la tabla: "Jue 12 nov" (+ año si no es el actual). */
  fechaCorta(fecha: string): string {
    const texto = `${diaCorto(fecha)} ${diaDelMes(fecha)} ${mesCorto(fecha)}`;
    return fecha.slice(0, 4) === this.hoy.slice(0, 4) ? texto : `${texto} ${fecha.slice(0, 4)}`;
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

  etiquetaMotivo(valor: MotivoCancelacion): string {
    return this.MOTIVOS.find((m) => m.valor === valor)?.etiqueta ?? valor;
  }

  /** Abre la confirmación en la fila. Si había otra abierta, la reemplaza. */
  iniciarCancelacion(r: Reservation): void {
    this.confirmandoId.set(r.id);
    this.motivo.set(null);
  }

  volver(): void {
    this.confirmandoId.set(null);
    this.motivo.set(null);
  }

  onMotivoChange(event: Event): void {
    // El value de un <select> siempre es string; las opciones salen de MOTIVOS, por eso el cast es seguro
    const valor = (event.target as HTMLSelectElement).value;
    this.motivo.set(valor ? (valor as MotivoCancelacion) : null);
  }

  confirmarCancelacion(r: Reservation): void {
    // El motivo es opcional: null = "Prefiero no decirlo" (el backend lo acepta)
    const motivo = this.motivo();
    this.cancellingId.set(r.id);
    this.reservationService.cancelar(r.id, motivo).subscribe({
      next: () => {
        this.cancellingId.set(null);
        this.volver();
        this.toast.showSuccess('Reserva cancelada correctamente.');
        this.load();
      },
      error: (err: HttpErrorResponse) => {
        this.cancellingId.set(null);
        this.volver();
        this.toast.showError(formatHttpError(err));
        // Un 409 puede significar que el estado cambió (ej: ya cancelada): recargamos para mostrar lo real
        this.load();
      },
    });
  }
}
