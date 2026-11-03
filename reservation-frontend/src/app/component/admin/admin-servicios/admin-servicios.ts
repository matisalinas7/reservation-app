import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminCategoria, AdminServicio } from '../../../model/admin.model';
import { AdminService } from '../../../services/admin.service';
import { ToastService } from '../../../services/toast.service';
import { formatHttpError } from '../../../utils/http-error.util';

@Component({
  selector: 'app-admin-servicios',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './admin-servicios.html',
  styleUrl: '../admin-shared.css',
})
export class AdminServiciosComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly adminService = inject(AdminService);
  private readonly toast = inject(ToastService);

  readonly servicios = signal<AdminServicio[]>([]);
  readonly categorias = signal<AdminCategoria[]>([]);
  readonly categoriasActivas = computed(() => this.categorias().filter((c) => !c.fechaBaja));
  readonly loading = signal(false);
  readonly submitting = signal(false);
  readonly editingId = signal<number | null>(null);

  readonly form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    descripcion: ['', [Validators.required, Validators.maxLength(255)]],
    duracion: [30, [Validators.required, Validators.min(1)]],
    categoriaId: [0, [Validators.required, Validators.min(1)]],
  });

  ngOnInit(): void {
    this.cargarCategorias();
    this.cargar();
  }

  cargarCategorias(): void {
    this.adminService.getCategorias().subscribe({
      next: (data) => this.categorias.set(data),
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }

  cargar(): void {
    this.loading.set(true);
    this.adminService.getServicios().subscribe({
      next: (data) => {
        this.servicios.set(data);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.toast.showError(formatHttpError(err));
      },
    });
  }

  nombreCategoria(id: number): string {
    return this.categorias().find((c) => c.id === id)?.nombre ?? `#${id}`;
  }

  editar(servicio: AdminServicio): void {
    this.editingId.set(servicio.id);
    this.form.setValue({
      nombre: servicio.nombre,
      descripcion: servicio.descripcion,
      duracion: servicio.duracion,
      categoriaId: servicio.categoriaId,
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

    const payload = this.form.getRawValue();
    const id = this.editingId();
    const request$ =
      id === null
        ? this.adminService.crearServicio(payload)
        : this.adminService.actualizarServicio(id, payload);

    this.submitting.set(true);
    request$.subscribe({
      next: () => {
        this.submitting.set(false);
        this.toast.showSuccess(id === null ? 'Servicio creado.' : 'Servicio actualizado.');
        this.cancelarEdicion();
        this.cargar();
      },
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        this.toast.showError(formatHttpError(err));
      },
    });
  }

  darDeBaja(servicio: AdminServicio): void {
    if (!confirm(`¿Dar de baja el servicio "${servicio.nombre}"?`)) {
      return;
    }
    this.adminService.eliminarServicio(servicio.id).subscribe({
      next: () => {
        this.toast.showSuccess('Servicio dado de baja.');
        this.cargar();
      },
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }

  reactivar(servicio: AdminServicio): void {
    this.adminService.reactivarServicio(servicio.id).subscribe({
      next: () => {
        this.toast.showSuccess('Servicio reactivado.');
        this.cargar();
      },
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }

  campoInvalido(campo: 'nombre' | 'descripcion' | 'duracion' | 'categoriaId'): boolean {
    const c = this.form.get(campo);
    return !!c && c.invalid && (c.dirty || c.touched);
  }
}
