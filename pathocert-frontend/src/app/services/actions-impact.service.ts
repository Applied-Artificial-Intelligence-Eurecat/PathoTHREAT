import { Injectable } from '@angular/core';

import { BehaviorSubject, interval, Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { EventMerged, EventSimilarity } from 'src/app/models/event';

// import { Page } from 'src/app/shared/page';
import { ApiService } from 'src/app/services/api.service';
import { EmergencyService } from './emergency.service';
import { Router } from '@angular/router';
import { GraphItem } from '../models/graphitem';

@Injectable()
export class ActionsImpactService {

    emergencyId: number;
    pastEventsData: BehaviorSubject<EventSimilarity[]> = new BehaviorSubject([]);
    adaptEventData: EventMerged = new EventMerged();
    flagAskImpact = false;
    flagAskControl = false;
    impactHtml: BehaviorSubject<string> = new BehaviorSubject('');
    controlHtml: BehaviorSubject<string> = new BehaviorSubject('');

    eventResultReceived: BehaviorSubject<EventMerged> = new BehaviorSubject(
        null
    );

    constructor(private apiService: ApiService,
        private emergencyService: EmergencyService,
        private rout: Router) {
        this.eventResultReceived.subscribe(
            (event) => {
                this.adaptEventData = event;
                this.impactHtml.next(this.renderImpactFromEventMerged(event));
                this.controlHtml.next(this.renderControlFromEventMerged(event));
            }
        );
    }

    editedImpactHtml(impactHtml: string): void {
        this.impactHtml.next(impactHtml.replace(/<p><br><\/p>/g, ''));
    }

    editedControlHtml(controlHtml: string): void {
        this.controlHtml.next(controlHtml.replace(/<p><br><\/p>/g, ''));
    }

    async initializeData() {
        this.pastEventsData.next([]);
        this.eventResultReceived.next(null);
        let subscr: Subscription;
        let obs = await this.retrieveHistoricEvents();
        subscr = obs.subscribe(
            (res) => {
                this.pastEventsData.next(res);
            }
        );
        const timer$ = interval(20000).subscribe(
            async (result) => {
                if (subscr !== undefined) {
                    subscr.unsubscribe();
                }
                obs = await this.retrieveHistoricEvents();
                subscr = obs.subscribe(
                    (res) => {
                        this.pastEventsData.next(res);
                        timer$.unsubscribe();
                    }
                );
            }
        );
    }

    async retrieveHistoricEvents(): Promise<Observable<Array<EventSimilarity>>> {
        return await this.apiService.retrieveDocuments(this.emergencyId)
            .then(
                map(response => {
                    return response ? response : [];
                }));
    }

    async retrieveMergedEvents(ls: string[]) {
        this.eventResultReceived.next(null);
        let sub = await this.mergeEvents(ls);
        sub.subscribe((res) => {
            this.eventResultReceived.next(res);
            this.impactHtml.next(this.renderImpactFromEventMerged(res));
            this.controlHtml.next(this.renderControlFromEventMerged(res));
        });
    }

    async mergeEvents(ls: string[]): Promise<Observable<EventMerged>> {
        return await this.apiService.retrieveMergeEvent(ls)
            .then(
                map(response => response ? response : new EventMerged())
            );
        // return of(ls[0]);
    }

    async searchByTerms(term1: string, term2: string): Promise<Observable<string>> {
        return await this.apiService.searchByTerms(term1, term2).then(
            map(response => response ? response : ''));
    }

    async detailValues(): Promise<Observable<string[]>> {
        return await this.apiService.detailValues().then(
            map(r => r ? r : ['']),
        );
    }

    async detailLabels(): Promise<Observable<string[]>> {
        return await this.apiService.detailLabels().then(
            map(r => r ? r : ['']),
        );
    }

    async detailItems(): Promise<Observable<GraphItem[]>> {
        return await this.apiService.detailItems().then(
            map(r => r),
        );
    }

    async detailItemsSearch(term1, term2): Promise<Observable<GraphItem[]>> {
        return await this.apiService.detailItemsSearch(term1, term2).then(
            map(r => r),
        );
    }

    async specificValues(value: string) {
        return await this.apiService.specifyValues(value).then(
            map(r => r ? r : [''])
        );
    }

    async specificLabels(value: string) {
        return await this.apiService.specifyLabels(value).then(
            map(r => r ? r : [''])
        );
    }

    async eventImpactControlComplete() {
        const eme = this.emergencyService.detailEmergency.value;
        eme.impact = this.impactHtml.value;
        eme.control = this.controlHtml.value;
        let sub = await this.emergencyService.updateEmergency(eme);
        sub.subscribe(emergency => {
        });
        this.rout.navigate([`detail/${this.emergencyId}`]);
        // TODO @sergi.simon
    }

    renderControlFromEventMerged(event: EventMerged): string {
        return `
<div class="p-fluid p-col-12 p-md-12">
    ${this.renderPropertiesIfExists('Potential Event Detection: ', event.detection)}
    ${this.renderPropertiesIfExists('Mitigation Actions: ', event.mitigation)}
    ${this.renderPropertiesIfExists('Monitoring Actions: ', event.monitoring)}
    ${this.renderPropertiesIfExists('Restoration: ', event.restoration)}
    ${this.renderPropertiesIfExists('Prevention: ', event.prevention)}
</div>`;
    }

    renderImpactFromEventMerged(event: EventMerged): string {
        return `
        ${this.renderPropertyIfExist('Number of people exposed', event.impact?.peopleIll)}
        ${this.renderPropertyIfExist('Number of people hospitalized', event.impact?.peopleHospitalized)}
        ${this.renderPropertyIfExist('Number of people dead', event.impact?.peopleDead)}
        ${this.renderPropertyIfExist('Time of contamination to first detection', undefined)}
        ${this.renderPropertyIfExist('Time of contamination to response', undefined)}
        ${this.renderPropertiesIfExists('Expected Associated Pathogens: ', event.contaminants)}
        ${this.renderPropertiesIfExists('Expected Associated Symptoms: ', event.symptoms)}
`;
        // Definir els pathogens enlloc de fer-ho després al damunt al html
    }

    private renderPropertyIfExist(name: string, value: string): string {
        const b = value !== null && value !== '' && value !== undefined;
        return b ? `
    <p>${name}: ${value}</p>` : '';
    }

    private renderPropertiesIfExists(name: string, ls: string[]): string {
        const text = JSON.stringify(ls);
        const b = (ls !== null) && (ls !== undefined) && (JSON.parse(text).toString() !== '');
        return b ? `
        <p>${name}</p>
        ${this.renderListOfProperties(ls)}` : '';

    }

    private renderListOfProperties(ls: string[]): string {
        return `<ul>${ls.map(e => `<li>${e}</li>`).join('')}</ul>`;
    }
}
