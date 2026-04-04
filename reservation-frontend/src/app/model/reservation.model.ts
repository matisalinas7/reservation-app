export enum ReservationStatus {
  ACTIVE = 'ACTIVE',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED',
}

/** Fecha ISO (yyyy-MM-dd) y hora (HH:mm:ss) según la API del backend. */
export interface Reservation {
  id: number;
  customerName: string;
  date: string;
  time: string;
  service: string;
  status: ReservationStatus;
}

export interface CreateReservationRequest {
  customerName: string;
  date: string;
  time: string;
  service: string;
}
