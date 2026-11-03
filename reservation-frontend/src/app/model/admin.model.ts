export interface AdminUser {
  id: number;
  nombre: string;
  apellido: string;
  mail: string;
  telefono?: string | null;
  rol: 'CLIENTE' | 'EMPLEADO' | 'ADMIN';
  fechaBaja?: string | null;
}

export interface AdminCategoria {
  id: number;
  nombre: string;
  descripcion: string;
  fechaBaja?: string | null;
}

export interface AdminServicio {
  id: number;
  nombre: string;
  descripcion: string;
  duracion: number;
  categoriaId: number;
  fechaBaja?: string | null;
}

export interface AdminHorario {
  id: number;
  diaSemana: 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';
  horaInicio: string;
  horaFin: string;
  servicioId: number;
  fechaBaja?: string | null;
}

export interface AdminTurno {
  id: number;
  fecha: string;
  horaInicio: string;
  horaFin: string;
  horarioId: number;
  fechaBaja?: string | null;
}
