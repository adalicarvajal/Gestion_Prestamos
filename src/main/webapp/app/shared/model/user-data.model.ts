import { IUser } from 'app/shared/model/user.model';

export interface IUserData {
  id?: number;
  salary?: number;
  familyLoad?: number;
  workplace?: string;
  housingType?: string | null;
  rentCost?: number | null;
  yearsOfEmployment?: number;
  employmentStatus?: number;
  user?: IUser | null;
}

export const defaultValue: Readonly<IUserData> = {};
