import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

import { BehaviorSubject, Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { Emergency } from 'src/app/models/emergency';
import { ApiService } from 'src/app/services/api.service';
import { Credentials } from '../models/credentials';

@Injectable()
export class EmergencyService {
    detailEmergency: BehaviorSubject<Emergency> = new BehaviorSubject(new Emergency);
    credentials: Credentials
    shouldUpdate: BehaviorSubject<boolean> = new BehaviorSubject(true);

    constructor(
        private apiService: ApiService,
        private keycloak: KeycloakService) {
    }

    async findAllPendent(): Promise<Observable<Array<Emergency>>> {
        const params: any = {
            archived: false,
            username: this.keycloak.getUsername()
        };
        return this.apiService.getEmergencyListFilterByArchived(params).then(
            map(response => {
                if (response && response._embedded && response._embedded.emergencies) {
                    return response._embedded.emergencies.map((eme: Emergency) => {
                        const emeString = 'emergencies';
                        let id = null;
                        if (Array.isArray(eme._links.self)) {
                            const index = (eme._links.self[0].href).indexOf(emeString);
                            id = (eme._links.self[0].href).slice(index + emeString.length + 1, (eme._links.self[0].href).length);
                        } else {
                            const index = (eme._links.self.href).indexOf(emeString);
                            id = (eme._links.self.href).slice(index + emeString.length + 1, (eme._links.self.href).length);
                        }
                        id = (id).replace('/', '');
                        eme.id = Number(id);
                        return eme;
                    });
                } else {
                    return [];
                }
            }));
    }

    async findPageCompleteEmergencies(first: number = -1, pageSize: number = 5): Promise<Observable<Array<Emergency>>> {
        const params: any = {
            archived: true
        };
        return this.apiService.getEmergencyListFilterByArchived(params).then(
            map(response => {
                if (response && response._embedded && response._embedded.emergencies) {
                    return response._embedded.emergencies.map(eme => {
                        const emeString = 'emergencies';
                        let id = null;
                        if (Array.isArray(eme._links.self)) {
                            const index = (eme._links.self[0].href).indexOf(emeString);
                            id = (eme._links.self[0].href).slice(index + emeString.length + 1, (eme._links.self[0].href).length);
                        } else {
                            const index = (eme._links.self.href).indexOf(emeString);
                            id = (eme._links.self.href).slice(index + emeString.length + 1, (eme._links.self.href).length);
                        }
                        id = (id).replace('/', '');
                        eme.id = Number(id);
                        return eme;
                    });
                } else {
                    return [];
                };
            }));
    }

    async retrieveEmergencyById(id: number): Promise<Observable<Emergency>> {
        const obs = await this.apiService.getEmergencyById(id).then(
            map(eme => {
                if (eme) {
                    const emeString = 'emergencies';
                    let id = null;
                    if (Array.isArray(eme._links.self)) {
                        const index = (eme._links.self[0].href).indexOf(emeString);
                        id = (eme._links.self[0].href).slice(index + emeString.length + 1, (eme._links.self[0].href).length);
                    } else {
                        const index = (eme._links.self.href).indexOf(emeString);
                        id = (eme._links.self.href).slice(index + emeString.length + 1, (eme._links.self.href).length);
                    }
                    id = (id).replace('/', '');
                    eme.id = Number(id);
                    return eme;
                } else {
                    return new Emergency();
                }
            })
        );
        obs.subscribe(e => this.detailEmergency.next(e))
        return obs
    }

    async createEmergency(emergency: Emergency): Promise<Observable<Emergency>> {
        emergency.reportDate = (new Date()).getTime();
        emergency.reportingUserId = "";
        console.log(this.keycloak.getUsername());
        emergency.reportingUsername = this.keycloak.getUsername();
        emergency.reportingOrganization = "";

        return await this.apiService.createEmergency(emergency).then(
            map(response => response ? response : null));
    }

    async updateEmergency(emergency: Emergency): Promise<Observable<Emergency>> {
        this.detailEmergency.next(emergency);
        return await this.apiService.updateEmergency(emergency).then(
            map(response => response ? response : null));
    }

    uploadToPathoWARE(emergencyJSON): Observable<string> {
        return this.apiService.sendToPathoWARE(emergencyJSON).pipe(
            map(
                response => {
                    return (response ? response : null);
                }
            )
        );
    }

    exportAssessment(emergencyObject: {}, filename: string) {
        console.log("Filename: " + filename);
        return this.apiService.exportAssessment(emergencyObject).pipe(
            map(
                response => {
                    const downloadURL = URL.createObjectURL(response);
                    const link = document.createElement('a');
                    link.href = downloadURL;
                    link.download = filename;
                    link.click();
                }
            )
        );
    }
}
