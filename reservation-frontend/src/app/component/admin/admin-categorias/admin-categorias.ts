import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminNavComponent } from '../admin-nav/admin-nav';
import { AdminCategoria } from '../../../model/admin.model';
import { AdminService } from '../../../services/admin.service';
import { ToastService } from '../../../services/toast.service';
import { formatHttpError } from '../../../utils/http-error.util';

@Component({
  selector: 'app-admin-categorias',
  imports: [ReactiveFormsModule, AdminNavComponent],
  templateUrl: './admin-categorias.html',
  styleUrl: '../admin-shared.css',
})
export class AdminCategoriasComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly adminService = inject(AdminService);
  private readonly toast = inject(ToastService);

  readonly categorias = signal<AdminCategoria[]>([]);
  readonly loading = signal(false);
  /** Error al cargar la lista (se muestra en la página, con Reintentar). */
  readonly error = signal<string | null>(null);
  readonly submitting = signal(false);
  readonly editingId = signal<number | null>(null);

  readonly form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    descripcion: ['', [Validators.required, Validators.maxLength(255)]],
  });

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.error.set(null);
    this.adminService.getCategorias().subscribe({
      next: (data) => {
        this.categorias.set(data);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(formatHttpError(err));
      },
    });
  }

  editar(categoria: AdminCategoria): void {
    this.editingId.set(categoria.id);
    this.form.setValue({ nombre: categoria.nombre, descripcion: categoria.descripcion });
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
        ? this.adminService.crearCategoria(payload)
        : this.adminService.actualizarCategoria(id, payload);

    this.submitting.set(true);
    request$.subscribe({
      next: () => {
        this.submitting.set(false);
        this.toast.showSuccess(id === null ? 'Categoría creada.' : 'Categoría actualizada.');
        this.cancelarEdicion();
        this.cargar();
      },
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        this.toast.showError(formatHttpError(err));
      },
    });
  }

  darDeBaja(categoria: AdminCategoria): void {
    const ok = confirm(
      `¿Dar de baja "${categoria.nombre}"? Se cancelarán las reservas futuras de sus servicios.`,
    );
    if (!ok) {
      return;
    }
    this.adminService.eliminarCategoria(categoria.id).subscribe({
      next: () => {
        this.toast.showSuccess('Categoría dada de baja.');
        this.cargar();
      },
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }

  reactivar(categoria: AdminCategoria): void {
    this.adminService.reactivarCategoria(categoria.id).subscribe({
      next: () => {
        this.toast.showSuccess('Categoría reactivada.');
        this.cargar();
      },
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }

  campoInvalido(campo: 'nombre' | 'descripcion'): boolean {
    const c = this.form.get(campo);
    return !!c && c.invalid && (c.dirty || c.touched);
  }
}
