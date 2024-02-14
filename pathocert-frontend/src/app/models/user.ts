import { Organization } from "src/app/models/organization";

export class User {
    id: number;
    username: string;
    password: string;
    userRole: UserRole;
    registrationDate: Date;
    organization?: Organization;
    _links?: any;
}

export enum UserRole {
    SUPER_ADMIN = 'SUPER_ADMIN',
    ADMIN = 'ADMIN',
    MANAGER = 'MANAGER',
    FIRST_RESPONDANT = 'FIRST_RESPONDANT'
}
