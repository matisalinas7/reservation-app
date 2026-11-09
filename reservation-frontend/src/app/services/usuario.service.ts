import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ClienteResumen } from '../model/usuario.model';

@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/usuarios`;

  // ADMIN / EMPLEADO: solo clientes activos, con los datos mínimos
  getClientes(): Observable<ClienteResumen[]> {
    return this.http.get<ClienteResumen[]>(`${this.baseUrl}/clientes`);
  }
}
