import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Reservation, ReservationStatus } from '../../model/reservation.model';
import { AuthService } from '../../services/auth.service';
import { ReservationService } from '../../services/reservation.service';
import { formatHttpError } from '../../utils/http-error.util';
import {
  capitalizar,
  fechaLarga,
  hhmm,
  horaActual,
  hoyISO,
  sumarDias,
} from '../../utils/horario.util';
import { rolLabel } from '../../utils/rol.util';
import { FechaBloqueComponent } from '../fecha-bloque/fecha-bloque';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, FechaBloqueComponent],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class DashboardComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly reservationService = inject(ReservationService);

  readonly currentUser = this.authService.currentUser;

  readonly esAdmin = computed(() => this.authService.hasAnyRole('ADMIN'));
  readonly esEmpleado = computed(() => this.authService.hasAnyRole('EMPLEADO'));
  readonly esCliente = computed(() => this.authService.hasAnyRole('CLIENTE'));
  readonly rolLabel = computed(() => rolLabel(this.currentUser()?.rol));

  readonly hhmm = hhmm;
  readonly hoy = hoyISO();
  readonly hoyLarga = capitalizar(fechaLarga(this.hoy));

  readonly reservas = signal<Reservation[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  /** CLIENTE: reservas activas que todavía no empezaron, de la más cercana a la más lejana. */
  readonly proximas = computed(() => {
    const ahora = horaActual();
    return this.reservas()
      .filter(
        (r) =>
          r.estado === ReservationStatus.ACTIVE &&
          (r.fecha > this.hoy || (r.fecha === this.hoy && hhmm(r.horaInicio) >= ahora)),
      )
      .sort((a, b) => (a.fecha + a.horaInicio).localeCompare(b.fecha + b.horaInicio));
  });
  readonly proximoTurno = computed(() => this.proximas()[0] ?? null);

  /** EMPLEADO: reservas activas de hoy, por horario. */
  readonly turnosDeHoy = computed(() =>
    this.reservas()
      .filter((r) => r.estado === ReservationStatus.ACTIVE && r.fecha === this.hoy)
      .sort((a, b) => a.horaInicio.localeCompare(b.horaInicio)),
  );

  readonly adminLinks = [
    { label: 'Categorías', detalle: 'Agrupá los servicios por tipo.', path: '/admin/categorias' },
    { label: 'Servicios', detalle: 'Nombre, categoría y duración de cada servicio.', path: '/admin/servicios' },
    { label: 'Horarios', detalle: 'Días y franjas en que se atiende cada servicio.', path: '/admin/horarios' },
    { label: 'Turnos', detalle: 'Generá los turnos a partir de los horarios.', path: '/admin/turnos' },
    { label: 'Usuarios', detalle: 'Roles, altas y bajas de cuentas.', path: '/admin/usuarios' },
  ];

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    // El ADMIN no tiene bloque de reservas en el inicio: no hace falta pedir nada.
    if (!this.esCliente() && !this.esEmpleado()) return;

    this.loading.set(true);
    this.error.set(null);
    const request$ = this.esEmpleado()
      ? this.reservationService.getAll()
      : this.reservationService.getMisReservas();

    request$.subscribe({
      next: (data) => {
        this.reservas.set(data);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(formatHttpError(err));
      },
    });
  }

  /** "Hoy", "Mañana" o "Jueves 12 de noviembre". */
  cuando(fecha: string): string {
    if (fecha === this.hoy) return 'Hoy';
    if (fecha === sumarDias(this.hoy, 1)) return 'Mañana';
    return capitalizar(fechaLarga(fecha));
  }
}
