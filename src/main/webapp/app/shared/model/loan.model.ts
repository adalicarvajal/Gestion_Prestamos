import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';

export interface ILoan {
  id?: number;
  requestedAmount?: number;
  interestRate?: number;
  paymentTermMonths?: number;
  applicationDate?: dayjs.Dayjs | null;
  status?: number;
  user?: IUser | null;
}

export const defaultValue: Readonly<ILoan> = {};
