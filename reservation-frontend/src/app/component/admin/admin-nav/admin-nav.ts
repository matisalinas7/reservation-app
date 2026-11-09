import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

/** Pestañas para moverse entre las secciones del panel sin volver al inicio. */
@Component({
  selector: 'app-admin-nav',
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="admin-nav" aria-label="Secciones de administración">
      @for (s of secciones; track s.path) {
        <a [routerLink]="s.path" routerLinkActive="is-active" ariaCurrentWhenActive="page">{{
          s.label
        }}</a>
      }
    </nav>
  `,
  styles: `
    .admin-nav {
      display: flex;
      gap: var(--space-1);
      overflow-x: auto;
      margin-bottom: var(--space-6);
      border-bottom: 1px solid var(--line);
      scrollbar-width: none;
    }

    a {
      display: inline-flex;
      align-items: center;
      min-height: var(--control-height);
      padding: 0 var(--space-3);
      margin-bottom: -1px;
      font-weight: 500;
      white-space: nowrap;
      color: var(--ink-muted);
      text-decoration: none;
      border-bottom: 2px solid transparent;
    }

    a:hover {
      color: var(--ink);
    }

    a.is-active {
      font-weight: 600;
      color: var(--ink);
      border-bottom-color: var(--brand);
    }
  `,
})
export class AdminNavComponent {
  readonly secciones = [
    { label: 'Categorías', path: '/admin/categorias' },
    { label: 'Servicios', path: '/admin/servicios' },
    { label: 'Horarios', path: '/admin/horarios' },
    { label: 'Turnos', path: '/admin/turnos' },
    { label: 'Usuarios', path: '/admin/usuarios' },
  ];
}
