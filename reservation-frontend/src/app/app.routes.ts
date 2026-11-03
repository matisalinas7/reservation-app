import { Routes } from '@angular/router';
import { AdminCategoriasComponent } from './component/admin/admin-categorias/admin-categorias';
import { DashboardComponent } from './component/dashboard/dashboard';
import { LoginComponent } from './component/login/login';
import { RegisterComponent } from './component/register/register';
import { ReservationFormComponent } from './component/reservation-form/reservation-form';
import { ReservationListComponent } from './component/reservation-list';
import { adminGuard } from './guards/admin.guard';
import { authGuard } from './guards/auth.guard';
import { guestGuard } from './guards/guest.guard';
import { NotFoundComponent } from './pages/not-found/not-found';
import { AdminServiciosComponent } from './component/admin/admin-servicios/admin-servicios';
import { AdminHorariosComponent } from './component/admin/admin-horarios/admin-horarios';
import { AdminTurnosComponent } from './component/admin/admin-turnos/admin-turnos';
import { AdminUsuariosComponent } from './component/admin/admin-usuarios/admin-usuarios';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },

  { path: '', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'reservas', component: ReservationListComponent, canActivate: [authGuard] },
  { path: 'nueva', component: ReservationFormComponent, canActivate: [authGuard] },

  {
    path: 'admin/categorias',
    component: AdminCategoriasComponent,
    canActivate: [authGuard, adminGuard],
  },

  {
    path: 'admin/servicios',
    component: AdminServiciosComponent,
    canActivate: [authGuard, adminGuard],
  },

  {
    path: 'admin/horarios',
    component: AdminHorariosComponent,
    canActivate: [authGuard, adminGuard],
  },

  {
    path: 'admin/turnos',
    component: AdminTurnosComponent,
    canActivate: [authGuard, adminGuard],
  },

  {
    path: 'admin/usuarios',
    component: AdminUsuariosComponent,
    canActivate: [authGuard, adminGuard],
  },

  { path: '**', component: NotFoundComponent },
];
