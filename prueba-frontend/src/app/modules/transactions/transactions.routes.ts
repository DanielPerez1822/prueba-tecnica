import { Routes } from '@angular/router';
import { TransactionFormComponent } from './transaction-form/transaction-form.component';
import { AccountStatementComponent } from './account-statement/account-statement.component';

export const TRANSACTIONS_ROUTES: Routes = [
  { path: '', component: AccountStatementComponent },
  { path: 'new', component: TransactionFormComponent },
  { path: 'account/:accountId', component: AccountStatementComponent },
  { path: 'statement/:accountId', component: AccountStatementComponent }
];

