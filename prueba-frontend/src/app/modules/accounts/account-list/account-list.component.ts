import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../../core/services/account.service';
import { ClientService } from '../../../core/services/client.service';
import { Account, AccountStatus } from '../../../core/models/account.model';
import { Client } from '../../../core/models/client.model';

@Component({
  selector: 'app-account-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './account-list.component.html',
  styleUrls: ['./account-list.component.css']
})
export class AccountListComponent implements OnInit {
  accounts: Account[] = [];
  clients: Client[] = [];
  clientsMap: Map<number, Client> = new Map();
  
  selectedClientId?: number;
  searchTerm: string = '';
  isLoading: boolean = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  
  // Modal de Cancelación
  accountToCancel: Account | null = null;
  isCancelling: boolean = false;
  
  // Modificando estado
  statusChangingId: number | null = null;

  constructor(
    private accountService: AccountService,
    private clientService: ClientService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadClients();
    this.route.params.subscribe(params => {
      if (params['clientId']) {
        this.selectedClientId = Number(params['clientId']);
      } else {
        this.selectedClientId = undefined;
      }
      this.loadAccounts();
    });
  }

  loadClients(): void {
    this.clientService.getAllClients().subscribe({
      next: (data) => {
        this.clients = data || [];
        this.clientsMap.clear();
        this.clients.forEach(c => {
          if (c.id) this.clientsMap.set(c.id, c);
        });
      },
      error: (err) => console.error('Error al cargar clientes:', err)
    });
  }

  loadAccounts(): void {
    this.isLoading = true;
    this.errorMessage = null;

    const request = this.selectedClientId
      ? this.accountService.getAccountsByClientId(this.selectedClientId)
      : this.accountService.getAllAccounts();

    request.subscribe({
      next: (data) => {
        this.accounts = data || [];
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.message || 'Error al cargar las cuentas financieras.';
      }
    });
  }

  onClientFilterChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    if (value) {
      this.router.navigate(['/accounts/client', value]);
    } else {
      this.router.navigate(['/accounts']);
    }
  }

  get filteredAccounts(): Account[] {
    if (!this.searchTerm.trim()) {
      return this.accounts;
    }
    const term = this.searchTerm.toLowerCase().trim();
    return this.accounts.filter(acc => {
      const client = this.clientsMap.get(acc.clientId);
      const clientName = client ? `${client.firstName} ${client.lastName}`.toLowerCase() : '';
      const typeLabel = acc.accountType === 'SAVINGS' ? 'ahorros' : 'corriente';
      const statusLabel = acc.status.toLowerCase();

      return acc.accountNumber.toLowerCase().includes(term) ||
             clientName.includes(term) ||
             typeLabel.includes(term) ||
             statusLabel.includes(term);
    });
  }

  getClientName(clientId: number): string {
    const client = this.clientsMap.get(clientId);
    return client ? `${client.firstName} ${client.lastName}` : `Cliente #${clientId}`;
  }

  getClientDoc(clientId: number): string {
    const client = this.clientsMap.get(clientId);
    return client ? `${client.identificationType} ${client.identificationNumber}` : '';
  }

  changeStatus(account: Account, newStatus: AccountStatus): void {
    if (!account.id) return;

    this.statusChangingId = account.id;
    this.errorMessage = null;

    this.accountService.updateAccountStatus(account.id, newStatus).subscribe({
      next: () => {
        this.statusChangingId = null;
        const actionName = newStatus === 'ACTIVE' ? 'activada' : 'inactivada';
        this.showSuccess(`La cuenta ${account.accountNumber} ha sido ${actionName} con éxito.`);
        this.loadAccounts();
      },
      error: (err) => {
        this.statusChangingId = null;
        this.errorMessage = err.message || 'No se pudo cambiar el estado de la cuenta.';
      }
    });
  }

  openCancelModal(account: Account): void {
    this.accountToCancel = account;
    this.errorMessage = null;
  }

  closeCancelModal(): void {
    if (this.isCancelling) return;
    this.accountToCancel = null;
  }

  confirmCancel(): void {
    if (!this.accountToCancel || !this.accountToCancel.id) return;

    // RN-P08: Validar saldo en la UI antes de enviar si es mayor a 0
    if (this.accountToCancel.balance > 0) {
      this.errorMessage = `No se puede cancelar la cuenta ${this.accountToCancel.accountNumber} porque tiene un saldo positivo de $${this.accountToCancel.balance.toFixed(2)}. Solo se pueden cancelar cuentas con saldo igual a $0.`;
      this.closeCancelModal();
      return;
    }

    this.isCancelling = true;
    const accountId = this.accountToCancel.id;
    const accNum = this.accountToCancel.accountNumber;

    this.accountService.cancelAccount(accountId).subscribe({
      next: () => {
        this.isCancelling = false;
        this.accountToCancel = null;
        this.showSuccess(`La cuenta ${accNum} ha sido cancelada exitosamente.`);
        this.loadAccounts();
      },
      error: (err) => {
        this.isCancelling = false;
        this.closeCancelModal();
        this.errorMessage = err.message || 'Error al cancelar la cuenta.';
      }
    });
  }

  showSuccess(message: string): void {
    this.successMessage = message;
    setTimeout(() => {
      this.successMessage = null;
    }, 4000);
  }

  dismissAlert(): void {
    this.errorMessage = null;
    this.successMessage = null;
  }
}
