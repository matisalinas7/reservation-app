import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, AuthUser, LoginRequest, RegisterRequest } from '../model/auth.model';

const TOKEN_KEY = 'access_token';
const USER_ID_KEY = 'user_id';
const USER_MAIL_KEY = 'user_mail';
const USER_ROL_KEY = 'user_rol';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  private readonly currentUserSignal = signal<AuthUser | null>(this.readUserFromStorage());

  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly isAuthenticated = computed(() => this.currentUserSignal() !== null);

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/login`, request)
      .pipe(tap((response) => this.saveSession(response)));
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/register`, request)
      .pipe(tap((response) => this.saveSession(response)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_ID_KEY);
    localStorage.removeItem(USER_MAIL_KEY);
    localStorage.removeItem(USER_ROL_KEY);
    this.currentUserSignal.set(null);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  hasAnyRole(...roles: string[]): boolean {
    const user = this.currentUserSignal();
    return !!user && roles.includes(user.rol);
  }

  private saveSession(response: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(USER_ID_KEY, String(response.id));
    localStorage.setItem(USER_MAIL_KEY, response.mail);
    localStorage.setItem(USER_ROL_KEY, response.rol);
    this.currentUserSignal.set({ id: response.id, mail: response.mail, rol: response.rol });
  }

  private readUserFromStorage(): AuthUser | null {
    const id = localStorage.getItem(USER_ID_KEY);
    const mail = localStorage.getItem(USER_MAIL_KEY);
    const rol = localStorage.getItem(USER_ROL_KEY);
    return id && mail && rol ? { id: Number(id), mail, rol } : null;
  }
}
