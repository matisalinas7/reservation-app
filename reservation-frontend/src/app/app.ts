import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ToastComponent } from './component/toast/toast';
import { AuthService } from './services/auth.service';
import { rolLabel } from './utils/rol.util';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  // Se exponen los signals directamente: en el template se leen igual, con ()
  readonly isAuthenticated = this.authService.isAuthenticated;
  readonly currentUser = this.authService.currentUser;

  readonly esCliente = computed(() => this.authService.hasAnyRole('CLIENTE'));
  readonly esAdmin = computed(() => this.authService.hasAnyRole('ADMIN'));
  readonly puedeReservar = computed(() => this.authService.hasAnyRole('CLIENTE', 'EMPLEADO'));
  readonly rolLabel = computed(() => rolLabel(this.currentUser()?.rol));

  /** Estado puramente visual: menú desplegado en el celular. */
  readonly menuAbierto = signal(false);

  toggleMenu(): void {
    this.menuAbierto.update((abierto) => !abierto);
  }

  cerrarMenu(): void {
    this.menuAbierto.set(false);
  }

  logout(): void {
    this.cerrarMenu();
    this.authService.logout();
    void this.router.navigate(['/login']);
  }
}
