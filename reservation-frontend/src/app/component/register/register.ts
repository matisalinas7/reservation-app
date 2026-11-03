import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { formatHttpError } from '../../utils/http-error.util';
import { RegisterRequest } from '../../model/auth.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly toast = inject(ToastService);
  private readonly router = inject(Router);

  readonly submitting = signal(false);

  readonly form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.minLength(2)]],
    apellido: ['', [Validators.required, Validators.minLength(2)]],
    mail: ['', [Validators.required, Validators.email]],
    contrasenia: ['', [Validators.required, Validators.minLength(8)]],
    telefono: ['', [Validators.pattern(/^[+]?[0-9]{8,15}$/)]],
  });

  register(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const payload: RegisterRequest = {
      nombre: raw.nombre.trim(),
      apellido: raw.apellido.trim(),
      mail: raw.mail.trim(),
      contrasenia: raw.contrasenia,
      telefono: raw.telefono?.trim() ? raw.telefono.trim() : undefined,
    };

    this.submitting.set(true);
    this.authService.register(payload).subscribe({
      next: (response) => {
        this.submitting.set(false);
        this.toast.showSuccess('Usuario registrado correctamente.');
        void this.router.navigate(['/']);
      },
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        this.toast.showError(formatHttpError(err));
      },
    });
  }

  campoInvalido(campo: 'nombre' | 'apellido' | 'mail' | 'contrasenia' | 'telefono'): boolean {
    const c = this.form.get(campo);
    return !!c && c.invalid && (c.dirty || c.touched);
  }
}
