import dayjs from 'dayjs';
import { ILoan } from 'app/shared/model/loan.model';

export interface IAmortization {
  id?: number;
  installmentNumber?: number;
  dueDate?: dayjs.Dayjs;
  remainingBalance?: number;
  principal?: number;
  paymentDate?: dayjs.Dayjs | null;
  paymentAmount?: number;
  penaltyInterest?: number | null;
  loan?: ILoan | null;
}

export const defaultValue: Readonly<IAmortization> = {};
