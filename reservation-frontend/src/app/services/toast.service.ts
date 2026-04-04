import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  readonly message = signal<string | null>(null);
  readonly variant = signal<'error' | 'success'>('error');

  private hideTimer?: ReturnType<typeof setTimeout>;

  showError(text: string): void {
    this.clearTimer();
    this.variant.set('error');
    this.message.set(text);
    this.hideTimer = setTimeout(() => this.dismiss(), 7000);
  }

  showSuccess(text: string): void {
    this.clearTimer();
    this.variant.set('success');
    this.message.set(text);
    this.hideTimer = setTimeout(() => this.dismiss(), 4000);
  }

  dismiss(): void {
    this.message.set(null);
    this.clearTimer();
  }

  private clearTimer(): void {
    if (this.hideTimer !== undefined) {
      clearTimeout(this.hideTimer);
      this.hideTimer = undefined;
    }
  }
}
