import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'clients', pathMatch: 'full' },
  {
    path: 'clients',
    loadChildren: () => import('./modules/clients/clients.routes').then(m => m.CLIENTS_ROUTES)
  },
  {
    path: 'accounts',
    loadChildren: () => import('./modules/accounts/accounts.routes').then(m => m.ACCOUNTS_ROUTES)
  },
  {
    path: 'transactions',
    loadChildren: () => import('./modules/transactions/transactions.routes').then(m => m.TRANSACTIONS_ROUTES)
  },
  { path: '**', redirectTo: 'clients' }
];
