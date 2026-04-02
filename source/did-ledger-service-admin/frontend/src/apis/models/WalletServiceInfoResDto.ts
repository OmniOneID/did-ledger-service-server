import { WalletServiceStatus } from '../constants/WalletServiceStatus';

export interface WalletServiceInfoResDto {
  id: number;
  did: string;
  name: string;
  status: WalletServiceStatus;
  serverUrl: string;
  certificateUrl: string;
  certificateVc: string;
  didDocument?: any;
  createdAt: string;
  updatedAt: string;
}
