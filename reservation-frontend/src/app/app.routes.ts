import { Routes } from '@angular/router';
import { ReservationFormComponent } from './component/reservation-form/reservation-form';
import { ReservationListComponent } from './component/reservation-list';
import { NotFoundComponent } from './pages/not-found/not-found';

export const routes: Routes = [
  { path: '', component: ReservationListComponent },
  { path: 'nueva', component: ReservationFormComponent },
  { path: '**', component: NotFoundComponent },
];
