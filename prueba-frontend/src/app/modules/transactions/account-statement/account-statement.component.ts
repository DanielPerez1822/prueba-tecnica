import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TransactionService } from '../../../core/services/transaction.service';
import { AccountService } from '../../../core/services/account.service';
import { ClientService } from '../../../core/services/client.service';
import { Account } from '../../../core/models/account.model';
import { Client } from '../../../core/models/client.model';
import { Transaction } from '../../../core/models/transaction.model';

@Component({
  selector: 'app-account-statement',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './account-statement.component.html',
  styleUrls: ['./account-statement.component.css']
})
export class AccountStatementComponent implements OnInit {
  accounts: Account[] = [];
  accountsMap: Map<number, Account> = new Map();
  clientsMap: Map<number, Client> = new Map();

  selectedAccountId?: number;
  selectedAccount?: Account;
  selectedClient?: Client;
  transactions: Transaction[] = [];

  isLoading: boolean = false;
  errorMessage: string | null = null;
  searchTerm: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private transactionService: TransactionService,
    private accountService: AccountService,
    private clientService: ClientService
  ) {}

  ngOnInit(): void {
    this.loadInitialData();
  }

  private loadInitialData(): void {
    this.isLoading = true;

    // Cargar Clientes
    this.clientService.getAllClients().subscribe({
      next: (clients) => {
        this.clientsMap.clear();
        (clients || []).forEach(c => {
          if (c.id) this.clientsMap.set(c.id, c);
        });

        // Cargar Cuentas
        this.accountService.getAllAccounts().subscribe({
          next: (accs) => {
            this.accounts = accs || [];
            this.accountsMap.clear();
            this.accounts.forEach(a => {
              if (a.id) this.accountsMap.set(a.id, a);
            });

            // Suscribirse a cambios en los parámetros de la ruta
            this.route.params.subscribe(params => {
              const accountIdParam = params['accountId'];
              if (accountIdParam) {
                this.selectedAccountId = Number(accountIdParam);
                this.loadStatement(this.selectedAccountId);
              } else if (this.accounts.length > 0 && this.accounts[0].id) {
                // Si no hay parámetro, seleccionar la primera cuenta disponible por defecto
                this.selectedAccountId = this.accounts[0].id;
                this.loadStatement(this.selectedAccountId);
              } else {
                this.isLoading = false;
              }
            });
          },
          error: (err) => {
            this.isLoading = false;
            this.errorMessage = err.message || 'Error al cargar las cuentas financieras.';
          }
        });
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.message || 'Error al cargar los clientes.';
      }
    });
  }

  loadStatement(accountId: number): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.selectedAccount = this.accountsMap.get(accountId);

    if (this.selectedAccount) {
      this.selectedClient = this.clientsMap.get(this.selectedAccount.clientId);
    }

    // Cargar extracto de transacciones
    this.transactionService.getAccountStatement(accountId).subscribe({
      next: (txs) => {
        // Ordenar por fecha descendente
        this.transactions = (txs || []).sort((a, b) => {
          const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
          const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
          return dateB - dateA;
        });
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.message || 'Error al cargar el extracto de movimientos.';
      }
    });
  }

  onAccountSelectChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    if (value) {
      this.router.navigate(['/transactions/account', value]);
    }
  }

  get filteredTransactions(): Transaction[] {
    if (!this.searchTerm.trim()) {
      return this.transactions;
    }
    const term = this.searchTerm.toLowerCase().trim();
    return this.transactions.filter(tx => {
      const typeStr = tx.transactionType.toLowerCase();
      const descStr = (tx.description || '').toLowerCase();
      const amountStr = String(tx.amount);
      const labelStr = this.getMovementLabel(tx).toLowerCase();

      return typeStr.includes(term) || descStr.includes(term) || amountStr.includes(term) || labelStr.includes(term);
    });
  }

  isCredit(tx: Transaction): boolean {
    if (!this.selectedAccountId) return false;
    if (tx.transactionType === 'DEPOSIT') return true;
    if (tx.transactionType === 'TRANSFER' && tx.targetAccountId === this.selectedAccountId) return true;
    return false;
  }

  getMovementLabel(tx: Transaction): string {
    if (tx.transactionType === 'DEPOSIT') {
      return 'Consignación (Crédito)';
    }
    if (tx.transactionType === 'WITHDRAWAL') {
      return 'Retiro de Efectivo (Débito)';
    }
    if (tx.transactionType === 'TRANSFER') {
      if (tx.targetAccountId === this.selectedAccountId) {
        const sourceAcc = tx.sourceAccountId ? this.accountsMap.get(tx.sourceAccountId) : null;
        const sourceNum = sourceAcc ? sourceAcc.accountNumber : `#${tx.sourceAccountId}`;
        return `Transferencia recibida de cta. ${sourceNum}`;
      } else {
        const targetAcc = tx.targetAccountId ? this.accountsMap.get(tx.targetAccountId) : null;
        const targetNum = targetAcc ? targetAcc.accountNumber : `#${tx.targetAccountId}`;
        return `Transferencia enviada a cta. ${targetNum}`;
      }
    }
    return tx.transactionType;
  }

  get totalDeposits(): number {
    return this.transactions
      .filter(tx => this.isCredit(tx))
      .reduce((sum, tx) => sum + tx.amount, 0);
  }

  get totalWithdrawals(): number {
    return this.transactions
      .filter(tx => !this.isCredit(tx))
      .reduce((sum, tx) => sum + tx.amount, 0);
  }
}
