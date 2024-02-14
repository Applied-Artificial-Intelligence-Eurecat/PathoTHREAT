import { UserRole } from "./user";

export class Credentials {
  token: string;
  username: string;
  role: UserRole;
  organization?: string;
  userHref?: string;
  userOrganizationHref?: string;
}
