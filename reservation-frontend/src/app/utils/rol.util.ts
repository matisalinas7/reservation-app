const ROL_LABELS: Record<string, string> = {
  ADMIN: 'Administrador',
  EMPLEADO: 'Empleado',
  CLIENTE: 'Cliente',
};

/** Texto en español para mostrar el rol. Si llega un rol desconocido, lo muestra tal cual. */
export function rolLabel(rol: string | undefined): string {
  return rol ? (ROL_LABELS[rol] ?? rol) : '';
}
