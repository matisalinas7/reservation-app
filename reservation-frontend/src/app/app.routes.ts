import { Routes } from '@angular/router';
import { AdminCategoriasComponent } from './component/admin/admin-categorias/admin-categorias';
import { AdminHorariosComponent } from './component/admin/admin-horarios/admin-horarios';
import { AdminServiciosComponent } from './component/admin/admin-servicios/admin-servicios';
import { AdminTurnosComponent } from './component/admin/admin-turnos/admin-turnos';
import { AdminUsuariosComponent } from './component/admin/admin-usuarios/admin-usuarios';
import { DashboardComponent } from './component/dashboard/dashboard';
import { LoginComponent } from './component/login/login';
import { RegisterComponent } from './component/register/register';
import { ReservationFormComponent } from './component/reservation-form/reservation-form';
import { ReservationListComponent } from './component/reservation-list';
import { authGuard } from './guards/auth.guard';
import { guestGuard } from './guards/guest.guard';
import { roleGuard } from './guards/role.guard';
import { NotFoundComponent } from './pages/not-found/not-found';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },

  { path: '', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'reservas', component: ReservationListComponent, canActivate: [authGuard] },
  {
    path: 'nueva',
    component: ReservationFormComponent,
    canActivate: [roleGuard('CLIENTE', 'EMPLEADO')], // ADMIN no reserva (igual que el backend)
  },

  // Ruta padre sin componente: el guard se escribe una vez para todo el panel
  {
    path: 'admin',
    canActivate: [roleGuard('ADMIN')],
    children: [
      { path: '', redirectTo: 'categorias', pathMatch: 'full' },
      { path: 'categorias', component: AdminCategoriasComponent },
      { path: 'servicios', component: AdminServiciosComponent },
      { path: 'horarios', component: AdminHorariosComponent },
      { path: 'turnos', component: AdminTurnosComponent },
      { path: 'usuarios', component: AdminUsuariosComponent },
    ],
  },

  { path: '**', component: NotFoundComponent },
];
