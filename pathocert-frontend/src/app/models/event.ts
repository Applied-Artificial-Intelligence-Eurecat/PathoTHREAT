import {AreaType} from './area';

export class EventSimilarity {
    document: {
        id: number;
        name: string;
        date: Date;
        source: string;
        url: string;
        keywords: string;
        text: string
    };
    impact: {
        numberPeopleExposed: number;
        numberPeopleHospitalized: number;
        numberPeopleDead: number;
        pathogens: string[];
        symptoms: string[];
    };
    control: {
        monitoring: string[];
        restoration: string[];
        prevention: string[]
    };
    similarity: number;
    active: boolean;
    // Things that we should have, but don't
    initialMitigation: null;
    eventInvestigation: null;
    date: null;
    firstDetectionEvent: null;
    timeToFirstDetection: null;
    timeToResponse: null;

    constructor(init?: Partial<Event>) {
        Object.assign(this, init);
        this.active = false;
    }
}

export class EventMerged {
    documentNames: string;
    location: { //
        city: string; //
        region: string; //
        country: string; //
    };
    cause: string[]; //
    source: string[];
    detection: string[]; //
    impact: { //
        peopleIll: string; //
        peopleHospitalized: string; //
        peopleDead: string; //
    };
    contaminants: string[]; //
    symptoms: string[]; //
    mitigation: string[]; //
    monitoring: string[]; //
    restoration: string[]; //
    prevention: string[]; //
}

export class Event {
    id?: number;
    caseType: CaseType;
    active: boolean;
    similarity: number;
    eventTypes: Array<EventType>; // identification/detection
    date: Date;
    area: AreaType;
    emergencyDescription: string;
    contaminationSource: string;
    firstDetectionEvent: string;
    risk?: string;
    action?: string;
    /*exposed: number; // impact
    hospitalized: number;
    dead: number;
    timeFirstDetection: any;
    timeResponse: any;
    associatedPathogens: Array<any>;
    eventInvestigation: string; // control
    initialMitigation: string;
    monitoring: string;
    restoration: string;
    prevention: string;*/
}

export enum EventType {
    EARTHQUAKE = 'EARTHQUAKE',
    WASTE_WATER = 'WASTE_WATER',
    DRINKING_WATER = 'DRINKING_WATER',
    LEAKING = 'LEAKING',
    FLOOD_BLOCKAGE = 'FLOOD_BLOCKAGE'
}


export enum CaseType {
    GREECE_WATER_CONTAMINATION = 'Greece water contamination',
    DENMARK_WATER_CONTAMINATION = 'DENMARK_WATER_CONTAMINATION',
    FLOOD_PARIS = 'FLOOD_PARIS',
    LOREM_IPSUM_DOLOR = 'LOREM_IPSUM_DOLOR',
    CONSECUTIVE_PRODUO_AMANIS_SMELLS = 'CONSECUTIVE_PRODUO_AMANIS_SMELLS',
    WATER_SMELLS_BAD_IN_POBLA = 'WATER_SMELLS_BAD_IN_POBLA',
    ALMATRET_FLOOD_BLOCKAGE = 'ALMATRET_FLOOD_BLOCKAGE'
}
