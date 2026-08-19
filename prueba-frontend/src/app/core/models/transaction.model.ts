export type TransactionType = 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER';

export interface Transaction {
  id?: number;
  transactionType: TransactionType;
  amount: number;
  description?: string;
  sourceAccountId?: number;
  targetAccountId?: number;
  createdAt?: string;
}

export interface CreateTransactionRequest {
  transactionType: TransactionType;
  amount: number;
  description?: string;
  sourceAccountId?: number;
  targetAccountId?: number;
}
