import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Account, AccountStatus, CreateAccountRequest } from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly apiUrl = '/api/v1/accounts';
  // http://localhost:9000
  constructor(private http: HttpClient) { }

  getAllAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.apiUrl);
  }

  getAccountById(id: number): Observable<Account> {
    return this.http.get<Account>(`${this.apiUrl}/${id}`);
  }

  getAccountsByClientId(clientId: number): Observable<Account[]> {
    return this.http.get<Account[]>(`${this.apiUrl}/client/${clientId}`);
  }

  createAccount(request: CreateAccountRequest): Observable<Account> {
    return this.http.post<Account>(this.apiUrl, request);
  }

  updateAccountStatus(id: number, status: AccountStatus): Observable<Account> {
    return this.http.put<Account>(`${this.apiUrl}/${id}/status`, { status });
  }

  cancelAccount(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
