export type IdentificationType = 'CC' | 'CE' | 'PASSPORT';

export interface Client {
  id?: number;
  identificationType: IdentificationType;
  identificationNumber: string | number;
  firstName: string;
  lastName: string;
  email: string;
  birthDate: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateClientRequest {
  identificationType: IdentificationType;
  identificationNumber: string | number;
  firstName: string;
  lastName: string;
  email: string;
  birthDate: string;
}

export interface UpdateClientRequest {
  identificationType: IdentificationType;
  identificationNumber: string | number;
  firstName: string;
  lastName: string;
  email: string;
  birthDate: string;
}

