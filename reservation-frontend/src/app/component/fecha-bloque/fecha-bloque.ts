import { Component, computed, input } from '@angular/core';
import { diaCorto, diaDelMes, fechaLarga, mesCorto } from '../../utils/horario.util';

/** Bloque de fecha estilo agenda: día abreviado, número grande y mes. */
@Component({
  selector: 'app-fecha-bloque',
  host: { '[attr.data-apagado]': 'apagado()' },
  template: `
    <time class="fecha-bloque" [attr.datetime]="fecha()" [attr.aria-label]="larga()">
      <span class="fecha-bloque__dia" aria-hidden="true">{{ dia() }}</span>
      <span class="fecha-bloque__num" aria-hidden="true">{{ numero() }}</span>
      <span class="fecha-bloque__mes" aria-hidden="true">{{ mes() }}</span>
    </time>
  `,
  styleUrl: './fecha-bloque.css',
})
export class FechaBloqueComponent {
  /** "yyyy-MM-dd" */
  readonly fecha = input.required<string>();
  /** Atenuado (por ejemplo, reservas del historial). */
  readonly apagado = input(false);

  readonly dia = computed(() => diaCorto(this.fecha()));
  readonly numero = computed(() => diaDelMes(this.fecha()));
  readonly mes = computed(() => mesCorto(this.fecha()));
  readonly larga = computed(() => fechaLarga(this.fecha()));
}
