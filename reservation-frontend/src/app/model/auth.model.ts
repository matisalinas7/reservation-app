export interface LoginRequest {
  mail: string;
  contrasenia: string;
}

export interface RegisterRequest {
  nombre: string;
  apellido: string;
  mail: string;
  contrasenia: string;
  telefono?: string;
}

export interface AuthResponse {
  token: string;
  id: number;
  mail: string;
  rol: string;
}

export interface AuthUser {
  id: number;
  mail: string;
  rol: string;
}
