import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../../services/admin.service';
import {
  AdminCategoria,
  AdminHorario,
  AdminServicio,
  AdminTurno,
  AdminUser,
} from '../../../model/admin.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <section class="admin-shell">
      <div class="admin-header">
        <div>
          <p class="eyebrow">Administración</p>
          <h1>Panel del administrador</h1>
        </div>
      </div>

      <div class="admin-grid">
        <a class="admin-card" routerLink="/admin/usuarios">
          <span class="admin-card__label">Usuarios</span>
          <strong>{{ usuarios.length }}</strong>
          <small>Ver y gestionar roles</small>
        </a>
        <a class="admin-card" routerLink="/admin/categorias">
          <span class="admin-card__label">Categorías</span>
          <strong>{{ categorias.length }}</strong>
          <small>Administrar categorías</small>
        </a>
        <a class="admin-card" routerLink="/admin/servicios">
          <span class="admin-card__label">Servicios</span>
          <strong>{{ servicios.length }}</strong>
          <small>Crear y editar servicios</small>
        </a>
        <a class="admin-card" routerLink="/admin/horarios">
          <span class="admin-card__label">Horarios</span>
          <strong>{{ horarios.length }}</strong>
          <small>Configurar disponibilidad</small>
        </a>
        <a class="admin-card" routerLink="/admin/turnos">
          <span class="admin-card__label">Turnos</span>
          <strong>{{ turnos.length }}</strong>
          <small>Generar y limpiar turnos</small>
        </a>
      </div>
    </section>
  `,
  styles: [
    `
      .admin-shell {
        max-width: 1200px;
        margin: 0 auto;
        padding: 1.5rem 1rem 3rem;
      }
      .admin-header {
        margin-bottom: 1.5rem;
      }
      .eyebrow {
        margin: 0 0 0.3rem;
        text-transform: uppercase;
        letter-spacing: 0.08em;
        font-size: 0.7rem;
        font-weight: 700;
        color: #4f46e5;
      }
      h1 {
        margin: 0;
        font-size: clamp(2rem, 4vw, 2.8rem);
      }
      .admin-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
        gap: 1rem;
      }
      .admin-card {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
        padding: 1.25rem;
        border-radius: 18px;
        text-decoration: none;
        background: linear-gradient(135deg, #ffffff, #eef2ff);
        border: 1px solid #dfe3f5;
        color: #111827;
        box-shadow: 0 10px 20px rgba(15, 23, 42, 0.04);
      }
      .admin-card__label {
        font-size: 0.72rem;
        text-transform: uppercase;
        letter-spacing: 0.06em;
        color: #4b5563;
      }
      .admin-card strong {
        font-size: 2rem;
      }
      .admin-card small {
        color: #4b5563;
      }
    `,
  ],
})
export class AdminDashboardComponent implements OnInit {
  private readonly adminService = inject(AdminService);

  usuarios: AdminUser[] = [];
  categorias: AdminCategoria[] = [];
  servicios: AdminServicio[] = [];
  horarios: AdminHorario[] = [];
  turnos: AdminTurno[] = [];

  ngOnInit(): void {
    this.cargarResumen();
  }

  private cargarResumen(): void {
    this.adminService.getUsuarios().subscribe({ next: (data) => (this.usuarios = data) });
    this.adminService.getCategorias().subscribe({ next: (data) => (this.categorias = data) });
    this.adminService.getServicios().subscribe({ next: (data) => (this.servicios = data) });
    this.adminService.getHorarios().subscribe({ next: (data) => (this.horarios = data) });
    this.adminService.getTurnos().subscribe({ next: (data) => (this.turnos = data) });
  }
}
