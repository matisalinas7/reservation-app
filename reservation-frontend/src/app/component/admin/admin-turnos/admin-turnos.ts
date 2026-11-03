import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminHorario, AdminServicio, AdminTurno } from '../../../model/admin.model';
import { AdminService } from '../../../services/admin.service';
import { ToastService } from '../../../services/toast.service';
import { formatHttpError } from '../../../utils/http-error.util';
import { diaDeLaSemana, diaLabel, hhmm, hoyISO } from '../../../utils/horario.util';

@Component({
  selector: 'app-admin-turnos',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './admin-turnos.html',
  styleUrl: '../admin-shared.css',
})
export class AdminTurnosComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly adminService = inject(AdminService);
  private readonly toast = inject(ToastService);

  readonly hhmm = hhmm;
  readonly hoy = hoyISO();

  readonly turnos = signal<AdminTurno[]>([]);
  readonly horarios = signal<AdminHorario[]>([]);
  readonly servicios = signal<AdminServicio[]>([]);
  readonly horariosActivos = computed(() => this.horarios().filter((h) => !h.fechaBaja));

  readonly filtroFecha = signal('');
  // Depende de dos signals (turnos y filtroFecha): se recalcula cuando cambia cualquiera
  readonly turnosVisibles = computed(() => {
    const fecha = this.filtroFecha();
    const lista = fecha ? this.turnos().filter((t) => t.fecha === fecha) : this.turnos();
    return [...lista].sort((a, b) =>
      (a.fecha + a.horaInicio).localeCompare(b.fecha + b.horaInicio),
    );
  });

  readonly loading = signal(false);
  readonly submitting = signal(false);

  readonly form = this.fb.nonNullable.group({
    horarioId: [0, [Validators.required, Validators.min(1)]],
    fecha: ['', Validators.required],
  });

  ngOnInit(): void {
    this.cargarHorariosYServicios();
    this.cargar();
  }

  cargarHorariosYServicios(): void {
    this.adminService.getHorarios().subscribe({
      next: (data) => this.horarios.set(data),
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
    this.adminService.getServicios().subscribe({
      next: (data) => this.servicios.set(data),
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }

  cargar(): void {
    this.loading.set(true);
    this.adminService.getTurnos().subscribe({
      next: (data) => {
        this.turnos.set(data);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.toast.showError(formatHttpError(err));
      },
    });
  }

  nombreServicio(servicioId: number): string {
    return this.servicios().find((s) => s.id === servicioId)?.nombre ?? `#${servicioId}`;
  }

  // Del turno llegamos al servicio pasando por el horario: turno -> horario -> servicio
  servicioDelTurno(turno: AdminTurno): string {
    const horario = this.horarios().find((h) => h.id === turno.horarioId);
    return horario ? this.nombreServicio(horario.servicioId) : `Horario #${turno.horarioId}`;
  }

  etiquetaHorario(h: AdminHorario): string {
    return `${this.nombreServicio(h.servicioId)} · ${diaLabel(h.diaSemana)} ${hhmm(h.horaInicio)}–${hhmm(h.horaFin)}`;
  }

  onFiltroFecha(event: Event): void {
    this.filtroFecha.set((event.target as HTMLInputElement).value);
  }

  generar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { horarioId, fecha } = this.form.getRawValue();
    const horario = this.horarios().find((h) => h.id === horarioId);
    if (!horario) {
      return;
    }
    if (fecha < this.hoy) {
      this.toast.showError('La fecha no puede ser anterior a hoy.');
      return;
    }
    if (diaDeLaSemana(fecha) !== horario.diaSemana) {
      this.toast.showError(
        `El horario elegido es de ${diaLabel(horario.diaSemana)}, pero la fecha cae en otro día de la semana.`,
      );
      return;
    }

    this.submitting.set(true);
    this.adminService.generarTurnos({ horarioId, fecha }).subscribe({
      next: (generados) => {
        this.submitting.set(false);
        if (generados.length === 0) {
          this.toast.showError('No se generó ningún turno. Revisá el rango horario del horario.');
        } else {
          this.toast.showSuccess(`Se generaron ${generados.length} turnos.`);
        }
        this.form.reset();
        this.cargar();
      },
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        this.toast.showError(formatHttpError(err));
      },
    });
  }

  darDeBaja(turno: AdminTurno): void {
    const ok = confirm('Si el turno tiene una reserva activa, también se cancelará. ¿Continuar?');
    if (!ok) {
      return;
    }
    this.adminService.eliminarTurno(turno.id).subscribe({
      next: () => {
        this.toast.showSuccess('Turno dado de baja.');
        this.cargar();
      },
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }

  reactivar(turno: AdminTurno): void {
    this.adminService.reactivarTurno(turno.id).subscribe({
      next: () => {
        this.toast.showSuccess('Turno reactivado.');
        this.cargar();
      },
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }

  campoInvalido(campo: 'horarioId' | 'fecha'): boolean {
    const c = this.form.get(campo);
    return !!c && c.invalid && (c.dirty || c.touched);
  }
}
