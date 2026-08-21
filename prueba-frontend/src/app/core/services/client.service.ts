import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client, CreateClientRequest, UpdateClientRequest } from '../models/client.model';

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private readonly apiUrl = '/api/v1/clients';
  // http://localhost:9000
  constructor(private http: HttpClient) { }

  getAllClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.apiUrl);
  }

  getClientById(id: number): Observable<Client> {
    return this.http.get<Client>(`${this.apiUrl}/${id}`);
  }

  createClient(request: CreateClientRequest): Observable<Client> {
    return this.http.post<Client>(this.apiUrl, request);
  }

  updateClient(id: number, request: UpdateClientRequest): Observable<Client> {
    return this.http.put<Client>(`${this.apiUrl}/${id}`, request);
  }

  deleteClient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
