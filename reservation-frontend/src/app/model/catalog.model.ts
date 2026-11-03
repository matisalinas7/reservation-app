export interface Categoria {
  id: number;
  nombre: string;
  descripcion: string;
}

export interface Servicio {
  id: number;
  nombre: string;
  descripcion: string;
  duracion: number;
  categoriaId: number;
}

export interface TurnoDisponible {
  id: number;
  fecha: string;
  horaInicio: string;
  horaFin: string;
  horarioId: number;
}
