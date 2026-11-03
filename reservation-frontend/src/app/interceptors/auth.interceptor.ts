import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from '../services/auth.service';

const AUTH_URL = `${environment.apiUrl}/auth`;

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Login y register son públicos: si mandamos un token vencido, JwtFilter responde 401
  if (req.url.startsWith(AUTH_URL)) {
    return next(req);
  }

  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();

  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) => {
      // 401 en una ruta protegida = token vencido o inválido: cerramos sesión
      if (err.status === 401) {
        authService.logout();
        void router.navigate(['/login']);
      }
      // Re-lanzamos el error para que el componente también pueda manejarlo
      return throwError(() => err);
    }),
  );
};
