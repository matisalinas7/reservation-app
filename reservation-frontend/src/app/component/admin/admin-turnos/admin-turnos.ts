import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminNavComponent } from '../admin-nav/admin-nav';
import {
  AdminCategoria,
  AdminHorario,
  AdminServicio,
  AdminTurno,
} from '../../../model/admin.model';
import { AdminService } from '../../../services/admin.service';
import { ToastService } from '../../../services/toast.service';
import { formatHttpError } from '../../../utils/http-error.util';
import { diaDeLaSemana, diaLabel, fechaCorta, hhmm, hoyISO } from '../../../utils/horario.util';

@Component({
  selector: 'app-admin-turnos',
  imports: [ReactiveFormsModule, AdminNavComponent],
  templateUrl: './admin-turnos.html',
  styleUrl: '../admin-shared.css',
})
export class AdminTurnosComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly adminService = inject(AdminService);
  private readonly toast = inject(ToastService);

  readonly hhmm = hhmm;
  readonly fechaCorta = fechaCorta;
  readonly hoy = hoyISO();

  readonly turnos = signal<AdminTurno[]>([]);
  readonly horarios = signal<AdminHorario[]>([]);
  readonly servicios = signal<AdminServicio[]>([]);
  readonly categorias = signal<AdminCategoria[]>([]);
  readonly horariosActivos = computed(() => this.horarios().filter((h) => !h.fechaBaja));

  // ---- Filtros de la lista (0 / '' = sin filtro) ----
  readonly filtroFecha = signal('');
  readonly filtroCategoria = signal(0);
  readonly filtroServicio = signal(0);
  readonly hayFiltros = computed(
    () => !!this.filtroFecha() || this.filtroCategoria() > 0 || this.filtroServicio() > 0,
  );

  /** Servicios para el select del filtro: solo los de la categoría elegida (si hay una). */
  readonly serviciosDelFiltro = computed(() => {
    const categoriaId = this.filtroCategoria();
    return categoriaId
      ? this.servicios().filter((s) => s.categoriaId === categoriaId)
      : this.servicios();
  });

  // turno -> horario -> servicio -> categoría. Se arman mapas una vez por cambio de listas,
  // así el filtro no hace un find() por cada turno.
  private readonly servicioPorHorario = computed(
    () => new Map(this.horarios().map((h) => [h.id, h.servicioId])),
  );
  private readonly categoriaPorServicio = computed(
    () => new Map(this.servicios().map((s) => [s.id, s.categoriaId])),
  );

  // Se recalcula cuando cambia la lista o cualquiera de los filtros
  readonly turnosVisibles = computed(() => {
    const fecha = this.filtroFecha();
    const categoriaId = this.filtroCategoria();
    const servicioId = this.filtroServicio();
    const servicioPorHorario = this.servicioPorHorario();
    const categoriaPorServicio = this.categoriaPorServicio();

    const lista = this.turnos().filter((t) => {
      if (fecha && t.fecha !== fecha) return false;
      const sId = servicioPorHorario.get(t.horarioId);
      if (servicioId && sId !== servicioId) return false;
      if (categoriaId && (sId === undefined || categoriaPorServicio.get(sId) !== categoriaId)) {
        return false;
      }
      return true;
    });
    return lista.sort((a, b) => (a.fecha + a.horaInicio).localeCompare(b.fecha + b.horaInicio));
  });

  readonly loading = signal(false);
  /** Error al cargar la lista (se muestra en la página, con Reintentar). */
  readonly error = signal<string | null>(null);
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
    this.adminService.getCategorias().subscribe({
      next: (data) => this.categorias.set(data),
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }

  cargar(): void {
    this.loading.set(true);
    this.error.set(null);
    this.adminService.getTurnos().subscribe({
      next: (data) => {
        this.turnos.set(data);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(formatHttpError(err));
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

  onFiltroCategoria(event: Event): void {
    const categoriaId = Number((event.target as HTMLSelectElement).value);
    this.filtroCategoria.set(categoriaId);
    // Si el servicio elegido no es de la nueva categoría, se limpia
    const servicio = this.servicios().find((s) => s.id === this.filtroServicio());
    if (categoriaId && servicio && servicio.categoriaId !== categoriaId) {
      this.filtroServicio.set(0);
    }
  }

  onFiltroServicio(event: Event): void {
    this.filtroServicio.set(Number((event.target as HTMLSelectElement).value));
  }

  limpiarFiltros(): void {
    this.filtroFecha.set('');
    this.filtroCategoria.set(0);
    this.filtroServicio.set(0);
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
