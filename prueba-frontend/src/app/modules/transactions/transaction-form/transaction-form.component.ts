import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { TransactionService } from '../../../core/services/transaction.service';
import { AccountService } from '../../../core/services/account.service';
import { ClientService } from '../../../core/services/client.service';
import { Account } from '../../../core/models/account.model';
import { Client } from '../../../core/models/client.model';
import { CreateTransactionRequest, TransactionType } from '../../../core/models/transaction.model';

@Component({
  selector: 'app-transaction-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './transaction-form.component.html',
  styleUrls: ['./transaction-form.component.css']
})
export class TransactionFormComponent implements OnInit {
  transactionForm!: FormGroup;
  accounts: Account[] = [];
  clientsMap: Map<number, Client> = new Map();
  
  isLoadingAccounts: boolean = false;
  isSubmitting: boolean = false;
  errorMessage: string | null = null;

  transactionTypes = [
    { code: 'DEPOSIT', label: 'Consignación (Depósito)' },
    { code: 'WITHDRAWAL', label: 'Retiro de Efectivo' },
    { code: 'TRANSFER', label: 'Transferencia entre Cuentas' }
  ];

  constructor(
    private fb: FormBuilder,
    private transactionService: TransactionService,
    private accountService: AccountService,
    private clientService: ClientService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadClientsAndAccounts();

    // Reaccionar al cambio de tipo de transacción
    this.transactionForm.get('transactionType')?.valueChanges.subscribe(type => {
      this.updateAccountValidators(type);
    });
  }

  private initForm(): void {
    this.transactionForm = this.fb.group({
      transactionType: ['DEPOSIT', [Validators.required]],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      description: [''],
      sourceAccountId: [null],
      targetAccountId: [null]
    }, { validators: [this.transferAccountsValidator] });

    this.updateAccountValidators('DEPOSIT');
  }

  private updateAccountValidators(type: TransactionType): void {
    const sourceCtrl = this.transactionForm.get('sourceAccountId');
    const targetCtrl = this.transactionForm.get('targetAccountId');

    sourceCtrl?.clearValidators();
    targetCtrl?.clearValidators();

    if (type === 'WITHDRAWAL' || type === 'TRANSFER') {
      sourceCtrl?.setValidators([Validators.required]);
    } else {
      sourceCtrl?.setValue(null);
    }

    if (type === 'DEPOSIT' || type === 'TRANSFER') {
      targetCtrl?.setValidators([Validators.required]);
    } else {
      targetCtrl?.setValue(null);
    }

    sourceCtrl?.updateValueAndValidity();
    targetCtrl?.updateValueAndValidity();
  }

  /**
   * Valida que en una transferencia las cuentas de origen y destino no sean la misma.
   */
  private transferAccountsValidator(control: AbstractControl): ValidationErrors | null {
    const type = control.get('transactionType')?.value;
    const source = control.get('sourceAccountId')?.value;
    const target = control.get('targetAccountId')?.value;

    if (type === 'TRANSFER' && source && target && String(source) === String(target)) {
      return { sameAccount: true };
    }
    return null;
  }

  private loadClientsAndAccounts(): void {
    this.isLoadingAccounts = true;
    this.clientService.getAllClients().subscribe({
      next: (clients) => {
        this.clientsMap.clear();
        (clients || []).forEach(c => {
          if (c.id) this.clientsMap.set(c.id, c);
        });

        this.accountService.getAllAccounts().subscribe({
          next: (data) => {
            // RN-T06: Solo cuentas activas
            this.accounts = (data || []).filter(a => a.status === 'ACTIVE');
            this.isLoadingAccounts = false;

            // Revisar si viene parametro por URL para pre-seleccionar cuenta
            const qAccount = this.route.snapshot.queryParamMap.get('account');
            const qType = this.route.snapshot.queryParamMap.get('type') as TransactionType;

            if (qType && ['DEPOSIT', 'WITHDRAWAL', 'TRANSFER'].includes(qType)) {
              this.transactionForm.patchValue({ transactionType: qType });
            }

            if (qAccount) {
              const accId = Number(qAccount);
              const currentType = this.transactionForm.get('transactionType')?.value;
              if (currentType === 'WITHDRAWAL' || currentType === 'TRANSFER') {
                this.transactionForm.patchValue({ sourceAccountId: accId });
              } else {
                this.transactionForm.patchValue({ targetAccountId: accId });
              }
            }
          },
          error: (err) => {
            this.isLoadingAccounts = false;
            this.errorMessage = err.message || 'Error al cargar las cuentas activas.';
          }
        });
      },
      error: (err) => {
        this.isLoadingAccounts = false;
        this.errorMessage = err.message || 'Error al cargar la información de clientes.';
      }
    });
  }

  getAccountLabel(acc: Account): string {
    const client = this.clientsMap.get(acc.clientId);
    const clientName = client ? `${client.firstName} ${client.lastName}` : `Cliente #${acc.clientId}`;
    const typeLabel = acc.accountType === 'SAVINGS' ? 'Ahorros' : 'Corriente';
    return `${acc.accountNumber} (${typeLabel}) — ${clientName} [Saldo: $${acc.balance.toFixed(2)}]`;
  }

  onSubmit(): void {
    if (this.transactionForm.invalid) {
      this.transactionForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = null;

    const val = this.transactionForm.value;
    const request: CreateTransactionRequest = {
      transactionType: val.transactionType,
      amount: Number(val.amount),
      description: val.description ? val.description.trim() : undefined,
      sourceAccountId: val.sourceAccountId ? Number(val.sourceAccountId) : undefined,
      targetAccountId: val.targetAccountId ? Number(val.targetAccountId) : undefined
    };

    this.transactionService.createTransaction(request).subscribe({
      next: (res) => {
        this.isSubmitting = false;
        const targetId = res.targetAccountId || res.sourceAccountId;
        if (targetId) {
          this.router.navigate(['/transactions/account', targetId]);
        } else {
          this.router.navigate(['/transactions']);
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err.message || 'Error al procesar la transacción financiera.';
      }
    });
  }

  get currentType(): TransactionType {
    return this.transactionForm.get('transactionType')?.value;
  }

  get f() {
    return this.transactionForm.controls;
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.transactionForm.get(fieldName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}
