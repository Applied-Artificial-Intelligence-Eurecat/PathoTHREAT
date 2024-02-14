import {Emergency} from 'src/app/models/emergency';
import {CaseType, Event, EventSimilarity, EventType} from 'src/app/models/event';
import {AreaType} from 'src/app/models/area';

export function mockDataPendent(): Emergency[] {
    return [
        {
            id: 1,
            reportDate: 9273969,
            reportingOrganization: null, // user organization
            reportingUserId: null, // user
            nameReporter: 'Andrés',
            ocupationDescription: 'Cámara',
            emergencyTypeClass: EventType.EARTHQUAKE,
            severityLevel: 1,
            affectedAreaType: AreaType.CITY,
            nameAreaAffected: 'Girona',
            emergencyDescription: '',
            waterStateDescription: '',
            riskAssessment: '',
            actionPlan: '',
            impact: '',
            control: ''
        },
        {
            id: 2,
            reportDate: 9273969,
            reportingOrganization: null, // user organization
            reportingUserId: null, // user
            nameReporter: 'Mario',
            ocupationDescription: 'Grifos',
            emergencyTypeClass: EventType.LEAKING,
            severityLevel: 3,
            affectedAreaType: AreaType.CITY,
            nameAreaAffected: 'Madrid',
            emergencyDescription: '',
            waterStateDescription: '',
            riskAssessment: '',
            actionPlan: '',
            impact: '',
            control: ''
        }
    ];
}

export function mockDataComplete(): Emergency[] {
    return [
        {
            id: 1,
            reportDate: 9273969,
            reportingOrganization: null, // user organization
            reportingUserId: null, // user
            nameReporter: 'Andrés',
            ocupationDescription: 'Cámara',
            emergencyTypeClass: EventType.WASTE_WATER,
            severityLevel: 2,
            affectedAreaType: AreaType.STREET,
            nameAreaAffected: 'Girona',
            emergencyDescription: '',
            waterStateDescription: '',
            riskAssessment: '',
            actionPlan: '',
            impact: '',
            control: ''
        },
        {
            id: 2,
            reportDate: -49273969,
            reportingOrganization: null, // user organization
            reportingUserId: null, // user
            nameReporter: 'Mario',
            ocupationDescription: 'Grifos',
            emergencyTypeClass: EventType.FLOOD_BLOCKAGE,
            severityLevel: 1,
            affectedAreaType: AreaType.CITY,
            nameAreaAffected: 'Madrid',
            emergencyDescription: '',
            waterStateDescription: '',
            riskAssessment: '',
            actionPlan: '',
            impact: '',
            control: ''
        }
    ];
}

export function mockDataHistoricEvents(): EventSimilarity[] {
    return [
        {
            document: {
                id: 1,
                text: 'This should contain a long list formatted.',
                name: 'Almaret Flood Blockage',
                date: new Date(),
                source: 'something.pdf',
                url: 'google.com',
                keywords: 'somekey,otherkey'
            },
            impact: {
                numberPeopleDead: 1,
                numberPeopleExposed: 2,
                numberPeopleHospitalized: 3,
                pathogens: ['a', 'b', 'c'],
            },
            control: {
                monitoring: ['Monistrong', 'yes'],
                prevention: ['Preventrong', 'no'],
                restoration: ['restrong', 'amb mel i llimona']

            },
            similarity: 50,
            active: false,
            initialMitigation: null,
            eventInvestigation: null,
            date: null,
            firstDetectionEvent: null,
            timeToFirstDetection: null,
            timeToResponse: null,
        },
        {
            document: {
                id: 2,
                text: 'This should contain a long list formatted.',
                name: 'Denmark water contamination',
                date: new Date(),
                source: 'something.pdf',
                url: 'google.com',
                keywords: 'somekey,otherkey'
            },
            impact: {
                numberPeopleDead: 1,
                numberPeopleExposed: 2,
                numberPeopleHospitalized: 3,
                pathogens: ['a', 'b', 'c'],
            },
            control: {
                monitoring: ['Monistrong', 'ok'],
                prevention: ['Preventrong', 'tk'],
                restoration: ['restrong', 'mx']
            },
            similarity: 20,
            active: false,
            initialMitigation: null,
            eventInvestigation: null,
            date: null,
            firstDetectionEvent: null,
            timeToFirstDetection: null,
            timeToResponse: null,
        }
    ];
}

export function mockDataHistoricEvents2(): Event[] {
    return [
        {
            id: 1,
            caseType: CaseType.ALMATRET_FLOOD_BLOCKAGE,
            active: false,
            similarity: 50,
            eventTypes: [EventType.DRINKING_WATER], // identification/detection
            date: new Date(),
            area: AreaType.CITY,
            emergencyDescription: 'after a strong',
            contaminationSource: 'drinking water',
            firstDetectionEvent: 'Customer complains',
            risk: ' Number of exposed: 400, Number of hospitalized: 100...',
            action: 'Event mitigation: , Initial investigation: Stop water,'
            /*exposed: 400, // impact
            hospitalized: 100,
            dead: 10,
            timeFirstDetection: '5 days',
            timeResponse: '8 days',
            associatedPathogens: ['Adenovirus'],
            eventInvestigation: '', // control
            initialMitigation: 'Stop water',
            monitoring: 'Analyses ...',
            restoration: 'Flushing ..',
            prevention: 'better...'*/
        },
        {
            id: 2,
            caseType: CaseType.DENMARK_WATER_CONTAMINATION,
            active: false,
            similarity: 79,
            eventTypes: [EventType.EARTHQUAKE, EventType.FLOOD_BLOCKAGE], // identification/detection
            date: new Date(),
            area: AreaType.STREET,
            emergencyDescription: 'after a strong',
            contaminationSource: 'drinking water',
            firstDetectionEvent: 'Customer complains',
            risk: ' Number of exposed: 140, Number of hospitalized: 20...',
            action: 'Event mitigation: A line-list, Initial investigation: The local...,'
            /*exposed: 140, // impact
            hospitalized: 10,
            dead: 0,
            timeFirstDetection: '-',
            timeResponse: '-',
            associatedPathogens: ['Campylobacter'],
            eventInvestigation: 'A line-list', // control
            initialMitigation: 'The local ..',
            monitoring: 'High concentrations of ...',
            restoration: 'Flushing  of the ...',
            prevention: '-'*/
        }
    ];
}
