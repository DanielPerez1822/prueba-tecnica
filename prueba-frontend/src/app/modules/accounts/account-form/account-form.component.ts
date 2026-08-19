import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { AccountService } from '../../../core/services/account.service';
import { ClientService } from '../../../core/services/client.service';
import { CreateAccountRequest } from '../../../core/models/account.model';
import { Client } from '../../../core/models/client.model';

@Component({
  selector: 'app-account-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './account-form.component.html',
  styleUrls: ['./account-form.component.css']
})
export class AccountFormComponent implements OnInit {
  accountForm!: FormGroup;
  clients: Client[] = [];
  isLoadingClients: boolean = false;
  isSubmitting: boolean = false;
  errorMessage: string | null = null;

  accountTypes = [
    { code: 'SAVINGS', label: 'Cuenta de Ahorros (Prefijo 53)' },
    { code: 'CHECKING', label: 'Cuenta Corriente (Prefijo 33)' }
  ];

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private clientService: ClientService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadClients();
  }

  private initForm(): void {
    this.accountForm = this.fb.group({
      clientId: ['', [Validators.required]],
      accountType: ['SAVINGS', [Validators.required]],
      gmfExempt: [false]
    });
  }

  private loadClients(): void {
    this.isLoadingClients = true;
    this.clientService.getAllClients().subscribe({
      next: (data) => {
        this.clients = data || [];
        this.isLoadingClients = false;

        // Verificar si viene clientId por query params o route params
        const qClientId = this.route.snapshot.queryParamMap.get('clientId');
        if (qClientId) {
          this.accountForm.patchValue({ clientId: Number(qClientId) });
        }
      },
      error: (err) => {
        this.isLoadingClients = false;
        this.errorMessage = err.message || 'Error al cargar la lista de clientes.';
      }
    });
  }

  onSubmit(): void {
    if (this.accountForm.invalid) {
      this.accountForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = null;

    const formVal = this.accountForm.value;
    const request: CreateAccountRequest = {
      clientId: Number(formVal.clientId),
      accountType: formVal.accountType,
      gmfExempt: Boolean(formVal.gmfExempt)
    };

    this.accountService.createAccount(request).subscribe({
      next: (created) => {
        this.isSubmitting = false;
        this.router.navigate(['/accounts']);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err.message || 'Error al aperturar la cuenta financiera.';
      }
    });
  }

  get f() {
    return this.accountForm.controls;
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.accountForm.get(fieldName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}
