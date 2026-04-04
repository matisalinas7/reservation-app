import { HttpErrorResponse } from '@angular/common/http';

export function formatHttpError(err: HttpErrorResponse): string {
  if (typeof err.error === 'string' && err.error.length > 0) {
    return err.error;
  }
  if (err.error && typeof err.error === 'object' && 'message' in err.error) {
    return String((err.error as { message: unknown }).message);
  }
  return err.message || 'Error de red o del servidor';
}