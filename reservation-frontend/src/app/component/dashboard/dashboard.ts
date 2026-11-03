import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class DashboardComponent {
  private readonly authService = inject(AuthService);

  readonly currentUser = this.authService.currentUser;
  readonly isAdmin = computed(() => this.authService.hasAnyRole('ADMIN'));

  readonly quickLinks = [
    { label: 'Ver reservas', path: '/reservas' },
    { label: 'Nueva reserva', path: '/nueva' },
  ];

  readonly adminQuickLinks = [
    { label: 'Gestionar categorías', path: '/admin/categorias' },
    { label: 'Gestionar servicios', path: '/admin/servicios' },
    { label: 'Gestionar horarios', path: '/admin/horarios' },
    { label: 'Gestionar turnos', path: '/admin/turnos' },
    { label: 'Gestionar usuarios', path: '/admin/usuarios' },
  ];
}
