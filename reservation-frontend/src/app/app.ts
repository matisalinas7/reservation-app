import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';
import { ToastComponent } from './component/toast/toast';

@Component({
  selector: 'app-root',
  imports: [CommonModule, RouterOutlet, RouterLink, ToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  isAdmin(): boolean {
    return this.authService.hasAnyRole('ADMIN');
  }

  currentUser() {
    return this.authService.currentUser();
  }

  logout(): void {
    this.authService.logout();
    void this.router.navigate(['/login']);
  }
}
