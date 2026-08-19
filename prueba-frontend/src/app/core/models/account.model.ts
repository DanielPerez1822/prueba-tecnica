export type AccountType = 'SAVINGS' | 'CHECKING';
export type AccountStatus = 'ACTIVE' | 'INACTIVE' | 'CANCELLED';

export interface Account {
  id?: number;
  accountType: AccountType;
  accountNumber: string;
  status: AccountStatus;
  balance: number;
  gmfExempt: boolean;
  createdAt?: string;
  updatedAt?: string;
  clientId: number;
}

export interface CreateAccountRequest {
  accountType: AccountType;
  clientId: number;
  gmfExempt: boolean;
}

export interface UpdateAccountStatusRequest {
  status: AccountStatus;
}
