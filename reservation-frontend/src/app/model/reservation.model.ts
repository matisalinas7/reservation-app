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
