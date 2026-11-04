import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

/**
 * Fábrica de guards: roleGuard('ADMIN'), roleGuard('CLIENTE', 'EMPLEADO').
 * Es experiencia de usuario, no seguridad: el backend valida igual con @PreAuthorize.
 */
export const roleGuard = (...roles: string[]): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const toast = inject(ToastService);

    // Chequea la sesión él mismo: no depende de que authGuard esté antes en el array
    if (!authService.isAuthenticated()) {
      return router.createUrlTree(['/login']);
    }

    if (authService.hasAnyRole(...roles)) {
      return true;
    }

    toast.showError('No tenés acceso a esa sección.');
    return router.createUrlTree(['/']);
  };
};
