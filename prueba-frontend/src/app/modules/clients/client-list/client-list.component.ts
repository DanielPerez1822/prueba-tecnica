import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ClientService } from '../../../core/services/client.service';
import { Client } from '../../../core/models/client.model';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.css']
})
export class ClientListComponent implements OnInit {
  clients: Client[] = [];
  isLoading: boolean = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  searchTerm: string = '';
  
  // Confimación de eliminación
  clientToDelete: Client | null = null;
  isDeleting: boolean = false;

  constructor(private clientService: ClientService) {}

  ngOnInit(): void {
    this.loadClients();
  }

  loadClients(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.clientService.getAllClients().subscribe({
      next: (data) => {
        this.clients = data || [];
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.message || 'Error al cargar la lista de clientes.';
      }
    });
  }

  get filteredClients(): Client[] {
    if (!this.searchTerm.trim()) {
      return this.clients;
    }
    const term = this.searchTerm.toLowerCase().trim();
    return this.clients.filter(client =>
      client.firstName.toLowerCase().includes(term) ||
      client.lastName.toLowerCase().includes(term) ||
      client.email.toLowerCase().includes(term) ||
      String(client.identificationNumber).toLowerCase().includes(term) ||
      client.identificationType.toLowerCase().includes(term)
    );
  }

  openDeleteModal(client: Client): void {
    this.clientToDelete = client;
    this.errorMessage = null;
  }

  closeDeleteModal(): void {
    if (this.isDeleting) return;
    this.clientToDelete = null;
  }

  confirmDelete(): void {
    if (!this.clientToDelete || !this.clientToDelete.id) return;

    this.isDeleting = true;
    const clientId = this.clientToDelete.id;

    this.clientService.deleteClient(clientId).subscribe({
      next: () => {
        this.isDeleting = false;
        this.clientToDelete = null;
        this.showSuccess('Cliente eliminado exitosamente.');
        this.loadClients();
      },
      error: (err) => {
        this.isDeleting = false;
        this.closeDeleteModal();
        // RN-02: No se puede eliminar cliente con productos
        this.errorMessage = err.message || 'No se puede eliminar el cliente. Verifique que no tenga productos asociados.';
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
