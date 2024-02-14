import {Organization} from 'src/app/models/organization';
import {User} from 'src/app/models/user';
import {Href, Links} from './links';

export class Emergency {
    id: number;
    reportDate: number;
    reportingOrganization: Organization | string; // user organization
    reportingUserId: User | string; // user
    reportingUsername: string;
    nameReporter: string;
    ocupationDescription: string;
    emergencyTypeClass: string;
    emergencyDescription: string;
    waterStateDescription?: string;
    infrastructureConcerns: string[];
    affectedAreaLat?: number;
    affectedAreaLon?: number;
    riskAssessment?: string;
    actionPlan?: string; // what does it do
    archived?: boolean;

    emergencyName?: string;
    scenario?: string;

    additionalObservations?: string;
    impact: string;
    control: string;
    // tslint:disable-next-line:variable-name
    _links?: LinksEmergency;
    contaminants: string[];
    infrastructures: string[];
    symptoms: string[];
}

export enum Severity {
    HIGH = 'HIGH',
    MEDIUM = 'MEDIUM',
    LOW = 'LOW'
}

export class LinksEmergency extends Links {
    emergency?: Href;
    reportingUserId?: Href;
    reportingOrganization?: Href;
}
