import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Categoria, Servicio, TurnoDisponible } from '../../model/catalog.model';
import { ClienteResumen } from '../../model/usuario.model';
import { AuthService } from '../../services/auth.service';
import { CatalogService } from '../../services/catalog.service';
import { ReservationService } from '../../services/reservation.service';
import { ToastService } from '../../services/toast.service';
import { UsuarioService } from '../../services/usuario.service';
import { formatHttpError } from '../../utils/http-error.util';
import { hhmm, horaActual, hoyISO } from '../../utils/horario.util';

@Component({
  selector: 'app-reservation-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './reservation-form.html',
  styleUrl: './reservation-form.css',
})
export class ReservationFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly catalog = inject(CatalogService);
  private readonly reservationService = inject(ReservationService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly authService = inject(AuthService);
  private readonly toast = inject(ToastService);
  private readonly router = inject(Router);

  readonly hhmm = hhmm;
  readonly hoy = hoyISO();

  /** El empleado reserva a nombre de un cliente; el cliente, a su propio nombre. */
  readonly esEmpleado = computed(() => this.authService.hasAnyRole('EMPLEADO'));

  readonly clientes = signal<ClienteResumen[]>([]);
  readonly categorias = signal<Categoria[]>([]);
  private readonly todosLosServicios = signal<Servicio[]>([]);
  readonly servicios = signal<Servicio[]>([]); // solo los de la categoría elegida
  readonly turnos = signal<TurnoDisponible[]>([]);
  readonly turnoSeleccionado = signal<TurnoDisponible | null>(null);
  readonly buscado = signal(false);
  readonly loadingTurnos = signal(false);
  readonly submitting = signal(false);

  readonly form = this.fb.nonNullable.group({
    clienteId: [0], // validadores solo si es empleado (ver constructor)
    categoriaId: [0, [Validators.required, Validators.min(1)]],
    servicioId: [0, [Validators.required, Validators.min(1)]],
    fecha: ['', Validators.required],
  });

  constructor() {
    // Un mismo form para los dos roles: el cliente no completa este campo, así que no lo validamos
    if (this.esEmpleado()) {
      this.form.controls.clienteId.setValidators([Validators.required, Validators.min(1)]);
      this.form.controls.clienteId.updateValueAndValidity();
    }

    // Cada cambio en un campo "arrastra" a los que dependen de él
    this.form.controls.categoriaId.valueChanges.subscribe((categoriaId) => {
      this.servicios.set(this.todosLosServicios().filter((s) => s.categoriaId === categoriaId));
      this.form.controls.servicioId.setValue(0); // esto dispara la búsqueda de abajo
    });
    this.form.controls.servicioId.valueChanges.subscribe(() => this.buscarTurnos());
    this.form.controls.fecha.valueChanges.subscribe(() => this.buscarTurnos());
  }

  ngOnInit(): void {
    if (this.esEmpleado()) {
      this.usuarioService.getClientes().subscribe({
        next: (data) => this.clientes.set(data),
        error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
      });
    }
    this.catalog.getCategorias().subscribe({
      next: (data) => this.categorias.set(data),
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
    this.catalog.getServicios().subscribe({
      next: (data) => this.todosLosServicios.set(data),
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }

  private buscarTurnos(): void {
    const { servicioId, fecha } = this.form.getRawValue();
    this.turnoSeleccionado.set(null);

    if (!servicioId || !fecha) {
      this.turnos.set([]);
      this.buscado.set(false);
      return;
    }

    this.loadingTurnos.set(true);
    this.catalog.getTurnosDisponibles(servicioId, fecha).subscribe({
      next: (data) => {
        this.turnos.set(this.ordenarYFiltrar(data, fecha));
        this.buscado.set(true);
        this.loadingTurnos.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.turnos.set([]);
        this.loadingTurnos.set(false);
        this.toast.showError(formatHttpError(err));
      },
    });
  }

  // El backend devuelve también los turnos de hoy que ya pasaron; los ocultamos (parche, ver pendientes)
  private ordenarYFiltrar(turnos: TurnoDisponible[], fecha: string): TurnoDisponible[] {
    const ordenados = [...turnos].sort((a, b) => a.horaInicio.localeCompare(b.horaInicio));
    if (fecha !== this.hoy) {
      return ordenados;
    }
    const ahora = horaActual();
    return ordenados.filter((t) => hhmm(t.horaInicio) > ahora);
  }

  reservar(): void {
    const turno = this.turnoSeleccionado();
    const usuario = this.authService.currentUser();
    if (!turno || !usuario) {
      return;
    }

    const { clienteId, servicioId } = this.form.getRawValue();
    const usuarioId = this.esEmpleado() ? clienteId : usuario.id;
    if (!usuarioId) {
      return; // empleado sin cliente elegido: el botón ya está deshabilitado
    }

    this.submitting.set(true);
    this.reservationService.crear({ usuarioId, turnoId: turno.id, servicioId }).subscribe({
      next: () => {
        this.submitting.set(false);
        const cliente = this.clientes().find((c) => c.id === usuarioId);
        this.toast.showSuccess(
          cliente
            ? `Reserva confirmada para ${cliente.nombre} ${cliente.apellido}.`
            : 'Reserva confirmada.',
        );
        void this.router.navigate(['/reservas']);
      },
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        this.toast.showError(formatHttpError(err));
        this.buscarTurnos(); // quizás alguien tomó ese turno: refrescamos la lista
      },
    });
  }
}
