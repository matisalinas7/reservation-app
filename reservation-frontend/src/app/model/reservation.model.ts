export enum ReservationStatus {
  ACTIVE = 'ACTIVE',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED',
}

export type MotivoCancelacion = 'CAMBIO_DE_PLANES' | 'EMERGENCIA' | 'ERROR_AL_RESERVAR' | 'OTRO';

/** Espejo de ReservaResponseDTO. fecha: "2026-10-20", horas: "11:00:00" (texto). */
export interface Reservation {
  id: number;
  usuarioId: number;
  turnoId: number;
  servicioId: number;
  servicioNombre: string;
  fecha: string;
  horaInicio: string;
  horaFin: string;
  estado: ReservationStatus;
  motivoCancelacion: MotivoCancelacion | null;
  fechaBaja: string | null;
}

/** Espejo de ReservaRequestDTO. */
export interface CreateReservationRequest {
  usuarioId: number;
  turnoId: number;
  servicioId: number;
}

/** Valores del enum del backend + texto para mostrar. Fuente única para selects y etiquetas. */
export const MOTIVOS_CANCELACION: readonly { valor: MotivoCancelacion; etiqueta: string }[] = [
  { valor: 'CAMBIO_DE_PLANES', etiqueta: 'Cambio de planes' },
  { valor: 'EMERGENCIA', etiqueta: 'Emergencia' },
  { valor: 'ERROR_AL_RESERVAR', etiqueta: 'Error al reservar' },
  { valor: 'OTRO', etiqueta: 'Otro' },
];
