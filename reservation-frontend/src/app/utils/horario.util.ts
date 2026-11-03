// El backend usa el enum DayOfWeek de Java (en inglés); al usuario le mostramos español
export const DIAS = [
  { value: 'MONDAY', label: 'Lunes' },
  { value: 'TUESDAY', label: 'Martes' },
  { value: 'WEDNESDAY', label: 'Miércoles' },
  { value: 'THURSDAY', label: 'Jueves' },
  { value: 'FRIDAY', label: 'Viernes' },
  { value: 'SATURDAY', label: 'Sábado' },
  { value: 'SUNDAY', label: 'Domingo' },
] as const;

export function diaLabel(valor: string): string {
  return DIAS.find((d) => d.value === valor)?.label ?? valor;
}

// "09:00:00" -> "09:00"
export function hhmm(hora: string): string {
  return hora.slice(0, 5);
}

// Date.getDay() devuelve 0 para domingo
const DIAS_JS = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];

// "2026-08-10" -> "MONDAY". Se arma con (año, mes, día) para evitar corrimientos de zona horaria
export function diaDeLaSemana(fecha: string): string {
  const [anio, mes, dia] = fecha.split('-').map(Number);
  return DIAS_JS[new Date(anio, mes - 1, dia).getDay()];
}

// Fecha de hoy "yyyy-MM-dd" en hora local (toISOString usaría UTC y podría dar mañana a la noche)
export function hoyISO(): string {
  const ahora = new Date();
  const mes = String(ahora.getMonth() + 1).padStart(2, '0');
  const dia = String(ahora.getDate()).padStart(2, '0');
  return `${ahora.getFullYear()}-${mes}-${dia}`;
}

// Hora actual local "HH:mm"
export function horaActual(): string {
  const ahora = new Date();
  const hh = String(ahora.getHours()).padStart(2, '0');
  const mm = String(ahora.getMinutes()).padStart(2, '0');
  return `${hh}:${mm}`;
}
