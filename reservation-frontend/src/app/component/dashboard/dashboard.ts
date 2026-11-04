import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { rolLabel } from '../../utils/rol.util';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class DashboardComponent {
  private readonly authService = inject(AuthService);

  readonly currentUser = this.authService.currentUser;

  readonly esAdmin = computed(() => this.authService.hasAnyRole('ADMIN'));
  readonly esEmpleado = computed(() => this.authService.hasAnyRole('EMPLEADO'));
  readonly esStaff = computed(() => this.authService.hasAnyRole('ADMIN', 'EMPLEADO'));
  readonly puedeReservar = computed(() => this.authService.hasAnyRole('CLIENTE', 'EMPLEADO'));
  readonly rolLabel = computed(() => rolLabel(this.currentUser()?.rol));

  readonly adminQuickLinks = [
    { label: 'Gestionar categorías', path: '/admin/categorias' },
    { label: 'Gestionar servicios', path: '/admin/servicios' },
    { label: 'Gestionar horarios', path: '/admin/horarios' },
    { label: 'Gestionar turnos', path: '/admin/turnos' },
    { label: 'Gestionar usuarios', path: '/admin/usuarios' },
  ];
}
