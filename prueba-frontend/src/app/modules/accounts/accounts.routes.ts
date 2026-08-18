import { Routes } from '@angular/router';
import { AccountListComponent } from './account-list/account-list.component';
import { AccountFormComponent } from './account-form/account-form.component';

export const ACCOUNTS_ROUTES: Routes = [
  { path: '', component: AccountListComponent },
  { path: 'client/:clientId', component: AccountListComponent },
  { path: 'new', component: AccountFormComponent }
];

