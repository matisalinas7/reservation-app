import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminNavComponent } from '../admin-nav/admin-nav';
import { AdminHorario, AdminServicio } from '../../../model/admin.model';
import { AdminService } from '../../../services/admin.service';
import { ToastService } from '../../../services/toast.service';
import { formatHttpError } from '../../../utils/http-error.util';
import { DIAS, diaLabel, hhmm } from '../../../utils/horario.util';

@Component({
  selector: 'app-admin-horarios',
  imports: [ReactiveFormsModule, AdminNavComponent],
  templateUrl: './admin-horarios.html',
  styleUrl: '../admin-shared.css',
})
export class AdminHorariosComponent implements OnInit {
  readonly dias = DIAS;
  readonly diaLabel = diaLabel;
  readonly hhmm = hhmm;

  private readonly fb = inject(FormBuilder);
  private readonly adminService = inject(AdminService);
  private readonly toast = inject(ToastService);

  readonly horarios = signal<AdminHorario[]>([]);
  readonly servicios = signal<AdminServicio[]>([]);
  readonly serviciosActivos = computed(() => this.servicios().filter((s) => !s.fechaBaja));
  readonly loading = signal(false);
  /** Error al cargar la lista (se muestra en la página, con Reintentar). */
  readonly error = signal<string | null>(null);
  readonly submitting = signal(false);
  readonly editingId = signal<number | null>(null);

  readonly form = this.fb.nonNullable.group({
    diaSemana: ['', Validators.required],
    horaInicio: ['', Validators.required],
    horaFin: ['', Validators.required],
    servicioId: [0, [Validators.required, Validators.min(1)]],
  });

  ngOnInit(): void {
    this.cargarServicios();
    this.cargar();
  }

  cargarServicios(): void {
    this.adminService.getServicios().subscribe({
      next: (data) => this.servicios.set(data),
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }

  cargar(): void {
    this.loading.set(true);
    this.error.set(null);
    this.adminService.getHorarios().subscribe({
      next: (data) => {
        this.horarios.set(data);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(formatHttpError(err));
      },
    });
  }

  nombreServicio(id: number): string {
    return this.servicios().find((s) => s.id === id)?.nombre ?? `#${id}`;
  }

  // El backend (LocalTime) trabaja con segundos; el input los omite, así que los completamos
  private conSegundos(hora: string): string {
    return hora.length === 5 ? `${hora}:00` : hora;
  }

  editar(horario: AdminHorario): void {
    this.editingId.set(horario.id);
    this.form.setValue({
      diaSemana: horario.diaSemana,
      horaInicio: this.hhmm(horario.horaInicio),
      horaFin: this.hhmm(horario.horaFin),
      servicioId: horario.servicioId,
    });
  }

  cancelarEdicion(): void {
    this.editingId.set(null);
    this.form.reset();
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const v = this.form.getRawValue();
    // "HH:mm" se puede comparar como texto: "09:00" < "10:00"
    if (v.horaFin <= v.horaInicio) {
      this.toast.showError('La hora de fin debe ser posterior a la hora de inicio.');
      return;
    }

    const payload = {
      diaSemana: v.diaSemana,
      horaInicio: this.conSegundos(v.horaInicio),
      horaFin: this.conSegundos(v.horaFin),
      servicioId: v.servicioId,
    };
    const id = this.editingId();
    const request$ =
      id === null
        ? this.adminService.crearHorario(payload)
        : this.adminService.actualizarHorario(id, payload);

    this.submitting.set(true);
    request$.subscribe({
      next: () => {
        this.submitting.set(false);
        this.toast.showSuccess(id === null ? 'Horario creado.' : 'Horario actualizado.');
        this.cancelarEdicion();
        this.cargar();
      },
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        this.toast.showError(formatHttpError(err));
      },
    });
  }

  darDeBaja(horario: AdminHorario): void {
    const ok = confirm(
      'Dar de baja este horario cancelará las reservas futuras asociadas a sus turnos. ¿Continuar?',
    );
    if (!ok) {
      return;
    }
    this.adminService.eliminarHorario(horario.id).subscribe({
      next: () => {
        this.toast.showSuccess('Horario dado de baja.');
        this.cargar();
      },
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }

  reactivar(horario: AdminHorario): void {
    this.adminService.reactivarHorario(horario.id).subscribe({
      next: () => {
        this.toast.showSuccess('Horario reactivado.');
        this.cargar();
      },
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }

  campoInvalido(campo: 'diaSemana' | 'horaInicio' | 'horaFin' | 'servicioId'): boolean {
    const c = this.form.get(campo);
    return !!c && c.invalid && (c.dirty || c.touched);
  }
}
