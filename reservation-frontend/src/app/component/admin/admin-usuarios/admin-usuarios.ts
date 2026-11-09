import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { AdminNavComponent } from '../admin-nav/admin-nav';
import { AdminUser } from '../../../model/admin.model';
import { AdminService } from '../../../services/admin.service';
import { AuthService } from '../../../services/auth.service';
import { ToastService } from '../../../services/toast.service';
import { formatHttpError } from '../../../utils/http-error.util';
import { rolLabel } from '../../../utils/rol.util';

@Component({
  selector: 'app-admin-usuarios',
  imports: [AdminNavComponent],
  templateUrl: './admin-usuarios.html',
  styleUrl: '../admin-shared.css',
})
export class AdminUsuariosComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly authService = inject(AuthService);
  private readonly toast = inject(ToastService);

  readonly roles = ['CLIENTE', 'EMPLEADO', 'ADMIN'] as const;
  readonly rolLabel = rolLabel;
  readonly usuarios = signal<AdminUser[]>([]);
  readonly loading = signal(false);
  /** Error al cargar la lista (se muestra en la página, con Reintentar). */
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.error.set(null);
    this.adminService.getUsuarios().subscribe({
      next: (data) => {
        this.usuarios.set(data);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(formatHttpError(err));
      },
    });
  }

  // El admin no debería poder quitarse el rol ni darse de baja a sí mismo
  esUsuarioActual(usuario: AdminUser): boolean {
    return usuario.mail === this.authService.currentUser()?.mail;
  }

  cambiarRol(usuario: AdminUser, event: Event): void {
    const rol = (event.target as HTMLSelectElement).value as AdminUser['rol'];
    if (rol === usuario.rol) {
      return;
    }
    this.adminService.actualizarRol(usuario.id, rol).subscribe({
      next: () => {
        this.toast.showSuccess(`Rol de ${usuario.mail} actualizado a ${rol}.`);
        this.cargar();
      },
      error: (err: HttpErrorResponse) => {
        this.toast.showError(formatHttpError(err));
        // Recargamos para que el select vuelva al valor real y no quede mostrando uno falso
        this.cargar();
      },
    });
  }

  darDeBaja(usuario: AdminUser): void {
    const ok = confirm(
      `¿Dar de baja a ${usuario.mail}? No podrá iniciar sesión y se cancelarán sus reservas futuras.`,
    );
    if (!ok) {
      return;
    }
    this.adminService.eliminarUsuario(usuario.id).subscribe({
      next: () => {
        this.toast.showSuccess('Usuario dado de baja.');
        this.cargar();
      },
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }

  reactivar(usuario: AdminUser): void {
    this.adminService.reactivarUsuario(usuario.id).subscribe({
      next: () => {
        this.toast.showSuccess('Usuario reactivado.');
        this.cargar();
      },
      error: (err: HttpErrorResponse) => this.toast.showError(formatHttpError(err)),
    });
  }
}
