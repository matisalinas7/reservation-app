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

// ---- Formato de fechas para mostrar (todas reciben "yyyy-MM-dd") ----

const DIAS_CORTOS = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
const DIAS_LARGOS = ['domingo', 'lunes', 'martes', 'miércoles', 'jueves', 'viernes', 'sábado'];
const MESES = [
  'enero',
  'febrero',
  'marzo',
  'abril',
  'mayo',
  'junio',
  'julio',
  'agosto',
  'septiembre',
  'octubre',
  'noviembre',
  'diciembre',
];

// Date en hora local (mismo motivo que diaDeLaSemana: new Date("2026-08-10") sería UTC)
function fechaLocal(fecha: string): Date {
  const [anio, mes, dia] = fecha.split('-').map(Number);
  return new Date(anio, mes - 1, dia);
}

// "2026-11-12" -> "Jue"
export function diaCorto(fecha: string): string {
  return DIAS_CORTOS[fechaLocal(fecha).getDay()];
}

// "2026-11-12" -> 12
export function diaDelMes(fecha: string): number {
  return fechaLocal(fecha).getDate();
}

// "2026-11-12" -> "nov"
export function mesCorto(fecha: string): string {
  return MESES[fechaLocal(fecha).getMonth()].slice(0, 3);
}

// "2026-11-12" -> "jueves 12 de noviembre"
export function fechaLarga(fecha: string): string {
  const d = fechaLocal(fecha);
  return `${DIAS_LARGOS[d.getDay()]} ${d.getDate()} de ${MESES[d.getMonth()]}`;
}

// "2026-11-12" + 1 -> "2026-11-13" (en hora local)
export function sumarDias(fecha: string, dias: number): string {
  const d = fechaLocal(fecha);
  d.setDate(d.getDate() + dias);
  const mes = String(d.getMonth() + 1).padStart(2, '0');
  const dia = String(d.getDate()).padStart(2, '0');
  return `${d.getFullYear()}-${mes}-${dia}`;
}

// "2026-11-12" -> "Jue 12 nov" (+ " 2025" si no es del año actual)
export function fechaCorta(fecha: string): string {
  const texto = `${diaCorto(fecha)} ${diaDelMes(fecha)} ${mesCorto(fecha)}`;
  const anio = fecha.slice(0, 4);
  return anio === hoyISO().slice(0, 4) ? texto : `${texto} ${anio}`;
}

// "jueves 12" -> "Jueves 12"
export function capitalizar(texto: string): string {
  return texto.charAt(0).toUpperCase() + texto.slice(1);
}
