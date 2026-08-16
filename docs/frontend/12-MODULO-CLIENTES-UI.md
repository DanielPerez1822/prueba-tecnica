# 👤 Módulo de Clientes (UI) — Frontend Angular

## 1. Descripción Funcional

El módulo de clientes en la interfaz de usuario permite la gestión completa del ciclo de vida de los clientes: **creación**, **edición**, **consulta** y **eliminación** con validaciones reactivas en tiempo real antes del envío al backend.

---

## 2. Componentes e Interfaz de Usuario

### 2.1 Listado de Clientes (`ClientListComponent`)

**Ruta:** `/clients`

Muestra una tabla interactiva con todos los clientes registrados, incluyendo buscador por cédula/nombre y acciones de edición/eliminación.

```typescript
@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './client-list.component.html'
})
export class ClientListComponent implements OnInit {
  clients: Client[] = [];
  searchTerm: string = '';

  constructor(private clientService: ClientService) {}

  ngOnInit(): void {
    this.loadClients();
  }

  loadClients(): void {
    this.clientService.getAllClients().subscribe({
      next: (data) => this.clients = data,
      error: (err) => console.error(err)
    });
  }

  deleteClient(id: number): void {
    if (confirm('¿Está seguro de eliminar este cliente?')) {
      this.clientService.deleteClient(id).subscribe({
        next: () => this.loadClients(),
        error: (err) => alert(err.message) // RN-02: Error si tiene productos
      });
    }
  }
}
```

---

### 2.2 Formulario de Cliente (`ClientFormComponent`)

**Rutas:** `/clients/new` (crear) y `/clients/edit/:id` (editar)

Formulario reactivo con validaciones basadas en las Reglas de Negocio del sistema.

```typescript
@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './client-form.component.html'
})
export class ClientFormComponent implements OnInit {
  clientForm!: FormGroup;
  isEditMode: boolean = false;
  clientId?: number;

  identificationTypes = [
    { code: 'CC', label: 'Cédula de Ciudadanía' },
    { code: 'CE', label: 'Cédula de Extranjería' },
    { code: 'PASSPORT', label: 'Pasaporte' }
  ];

  constructor(
    private fb: FormBuilder,
    private clientService: ClientService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.checkEditMode();
  }

  initForm(): void {
    this.clientForm = this.fb.group({
      identificationType: ['CC', Validators.required],
      identificationNumber: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      birthDate: ['', [Validators.required, this.legalAgeValidator]]
    });
  }

  // RN-01: Validar mayoría de edad (≥ 18 años)
  legalAgeValidator(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    const birthDate = new Date(control.value);
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    return age >= 18 ? null : { underage: true };
  }

  save(): void {
    if (this.clientForm.invalid) return;

    const clientData: Client = this.clientForm.value;

    if (this.isEditMode && this.clientId) {
      this.clientService.updateClient(this.clientId, clientData).subscribe({
        next: () => this.router.navigate(['/clients']),
        error: (err) => alert(err.message)
      });
    } else {
      this.clientService.createClient(clientData).subscribe({
        next: () => this.router.navigate(['/clients']),
        error: (err) => alert(err.message)
      });
    }
  }
}
```

---

## 3. Servicio ClientService (`client.service.ts`)

```typescript
@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private apiUrl = `${environment.apiUrl}/clients`;

  constructor(private http: HttpClient) {}

  getAllClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.apiUrl);
  }

  getClientById(id: number): Observable<Client> {
    return this.http.get<Client>(`${this.apiUrl}/${id}`);
  }

  createClient(client: Client): Observable<Client> {
    return this.http.post<Client>(this.apiUrl, client);
  }

  updateClient(id: number, client: Client): Observable<Client> {
    return this.http.put<Client>(`${this.apiUrl}/${id}`, client);
  }

  deleteClient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
```

---

## 4. Validaciones de Negocio en la UI

| # | Regla Backend | Validación UI | Mensaje de Error UI |
|---|---------------|---------------|----------------------|
| RN-01 | Mayor de 18 años | `legalAgeValidator` en `birthDate` | "El cliente debe ser mayor de edad (18 años o más)" |
| RN-05 | Correo electrónico válido | `Validators.email` | "Ingrese un correo electrónico válido (xxxx@xxxxx.xxx)" |
| RN-06 | Nombre/Apellido ≥ 2 caracteres | `Validators.minLength(2)` | "Debe tener al menos 2 caracteres" |
| RN-02 | No eliminar cliente con productos | Captura de error HTTP 409 Conflict | "No se puede eliminar el cliente porque tiene productos vinculados" |
