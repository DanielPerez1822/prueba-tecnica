import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { ClientService } from '../../../core/services/client.service';
import { CreateClientRequest, UpdateClientRequest } from '../../../core/models/client.model';

@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './client-form.component.html',
  styleUrls: ['./client-form.component.css']
})
export class ClientFormComponent implements OnInit {
  clientForm!: FormGroup;
  isEditMode: boolean = false;
  clientId?: number;
  isLoading: boolean = false;
  isSubmitting: boolean = false;
  errorMessage: string | null = null;

  identificationTypes = [
    { code: 'CC', label: 'Cédula de Ciudadanía (CC)' },
    { code: 'CE', label: 'Cédula de Extranjería (CE)' },
    { code: 'PASSPORT', label: 'Pasaporte' }
  ];

  constructor(
    private fb: FormBuilder,
    private clientService: ClientService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.checkEditMode();
  }

  private initForm(): void {
    this.clientForm = this.fb.group({
      identificationType: ['CC', [Validators.required]],
      identificationNumber: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      birthDate: ['', [Validators.required, this.legalAgeValidator]]
    });
  }

  /**
   * RN-01: Validador de mayoría de edad (>= 18 años).
   */
  legalAgeValidator(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    const birthDate = new Date(control.value);
    if (isNaN(birthDate.getTime())) return null;

    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();

    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }

    return age >= 18 ? null : { underage: true };
  }

  private checkEditMode(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.clientId = Number(idParam);
      this.loadClientData(this.clientId);
    }
  }

  private loadClientData(id: number): void {
    this.isLoading = true;
    this.clientService.getClientById(id).subscribe({
      next: (client) => {
        this.isLoading = false;
        if (client) {
          // Formatear birthDate si viene como YYYY-MM-DD
          const formattedBirthDate = client.birthDate ? client.birthDate.split('T')[0] : '';
          this.clientForm.patchValue({
            identificationType: client.identificationType,
            identificationNumber: client.identificationNumber,
            firstName: client.firstName,
            lastName: client.lastName,
            email: client.email,
            birthDate: formattedBirthDate
          });
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.message || 'Error al cargar la información del cliente.';
      }
    });
  }

  onSubmit(): void {
    if (this.clientForm.invalid) {
      this.clientForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = null;

    const formValue = this.clientForm.value;

    if (this.isEditMode && this.clientId) {
      const updateReq: UpdateClientRequest = {
        identificationType: formValue.identificationType,
        identificationNumber: formValue.identificationNumber,
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        email: formValue.email,
        birthDate: formValue.birthDate
      };

      this.clientService.updateClient(this.clientId, updateReq).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.router.navigate(['/clients']);
        },
        error: (err) => {
          this.isSubmitting = false;
          this.errorMessage = err.message || 'Error al actualizar el cliente.';
        }
      });
    } else {
      const createReq: CreateClientRequest = {
        identificationType: formValue.identificationType,
        identificationNumber: formValue.identificationNumber,
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        email: formValue.email,
        birthDate: formValue.birthDate
      };

      this.clientService.createClient(createReq).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.router.navigate(['/clients']);
        },
        error: (err) => {
          this.isSubmitting = false;
          this.errorMessage = err.message || 'Error al registrar el cliente.';
        }
      });
    }
  }

  // Helpers para acceder fácil a los controles desde el HTML
  get f() {
    return this.clientForm.controls;
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.clientForm.get(fieldName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}
