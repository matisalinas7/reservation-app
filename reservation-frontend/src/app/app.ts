import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { ToastComponent } from './component/toast/toast';
import { AuthService } from './services/auth.service';
import { rolLabel } from './utils/rol.util';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, ToastComponent],
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

  logout(): void {
    this.authService.logout();
    void this.router.navigate(['/login']);
  }
}
