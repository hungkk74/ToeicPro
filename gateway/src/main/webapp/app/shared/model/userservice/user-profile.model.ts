export interface IUserProfile {
  id?: number;
  userId?: string;
  name?: string;
  email?: string;
  phone?: string | null;
  address?: string | null;
}

export const defaultValue: Readonly<IUserProfile> = {};
