import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { SERVICIOS_DISPONIBLES } from '../../data/servicios-disponibles';
import { CreateReservationRequest } from '../../model/reservation.model';
import { ReservationService } from '../../services/reservation.service';
import { ToastService } from '../../services/toast.service';
import { formatHttpError } from '../../utils/http-error.util';

@Component({
  selector: 'app-reservation-form',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './reservation-form.html',
  styleUrl: './reservation-form.css',
})
export class ReservationFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly reservationService = inject(ReservationService);
  private readonly toast = inject(ToastService);
  private readonly router = inject(Router);

  readonly servicios = SERVICIOS_DISPONIBLES;
  readonly submitting = signal(false);

  readonly form = this.fb.nonNullable.group({
    nombreCliente: ['', [Validators.required, Validators.maxLength(100)]],
    fecha: ['', Validators.required],
    hora: ['', Validators.required],
    servicio: ['', Validators.required],
  });

  enviar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const v = this.form.getRawValue();
    const body: CreateReservationRequest = {
      customerName: v.nombreCliente.trim(),
      date: v.fecha,
      time: this.normalizeTime(v.hora),
      service: v.servicio,
    };

    this.submitting.set(true);
    this.reservationService.crear(body).subscribe({
      next: () => {
        this.submitting.set(false);
        this.toast.showSuccess('Reserva creada correctamente.');
        void this.router.navigate(['/']);
      },
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        this.toast.showError(formatHttpError(err));
      },
    });
  }

  campoInvalido(campo: 'nombreCliente' | 'fecha' | 'hora' | 'servicio'): boolean {
    const c = this.form.get(campo);
    return !!c && c.invalid && (c.dirty || c.touched);
  }

  private normalizeTime(time: string): string {
    return time.length === 5 ? `${time}:00` : time;
  }
}
