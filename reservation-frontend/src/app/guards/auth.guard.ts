import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Devolver un UrlTree redirige en un solo paso (en vez de navigate + false)
  return authService.isAuthenticated() ? true : router.createUrlTree(['/login']);
};
