import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Message } from 'primeng/api';

import { EventSimilarity } from 'src/app/models/event';
import { ActionsImpactService } from 'src/app/services/actions-impact.service';

type State = 'Loading' | 'ResultButError' | 'Result';

@Component({
    selector: 'app-historic-events',
    templateUrl: './historic-events.component.html',
    styleUrls: ['./historic-events.component.scss']
})
export class HistoricEventsComponent implements OnInit {

    historicEvents: Array<EventSimilarity> = [];
    historicEventsPage: Array<EventSimilarity> = [];
    casesSelected = 0;
    messageValidation: Message[] = [];
    switch: State = 'Loading';
    minElapsed: number = 0;
    secElapsed: number = 0;
    startTime: Date;


    constructor(private actionsImpactService: ActionsImpactService,
        private router: Router) {
    }

    ngOnInit(): void {
        this.startTime = new Date();
        setInterval(() => {
            let msElapsed = new Date().valueOf() - this.startTime.valueOf();
            this.minElapsed = Math.floor((msElapsed % (1000 * 60 * 60)) / (1000 * 60));
            this.secElapsed = Math.floor((msElapsed % (1000 * 60)) / 1000);
        }, 1000);
        // this.actionsImpactService.initializeData();
        this.getHistoricEvents();
    }

    getHistoricEvents() {
        this.switch = 'Loading';
        this.actionsImpactService.pastEventsData
            .subscribe(r => {
                if (r.length === 0) {
                    this.switch = 'Loading'; // behaviour return [] when it is still loading
                } else {
                    this.historicEvents = r;
                    this.historicEventsPage = this.historicEvents.slice(0, 5);
                    this.historicEvents.forEach(v => { v.active = false; });
                    this.historicEvents.forEach(e => e.active ? this.casesSelected++ : null);
                    this.switch = 'Result';
                }
            }, e => {
                this.switch = 'ResultButError';
            });
    }

    onChangeSelectCase(event: any) {

        const ls = this.historicEvents.map(e => [e.active, e.document.name]);
        if (event.checked) {
            this.casesSelected++;
            this.messageValidation = [];
        } else {
            this.casesSelected = this.casesSelected - 1;
        }
    }

    jsIsWeird() {
        return (e) => this.onChangeSelectCase(e);
    }

    prepareQA() {
        this.router.navigate([`detail/${this.actionsImpactService.emergencyId}/actions/adapt/ask`]);
    }

    addMessageNoCasesSelected() {
        this.messageValidation = [
            { severity: 'warn', summary: 'Warning', detail: 'Please, select at least one case to continue.' }
        ];
    }

    async nextPage() {
        if (this.casesSelected > 0) {
            // enviar eventos seleccionados y recibir el resultado combinado
            await this.actionsImpactService.retrieveMergedEvents(this.historicEvents.filter(e => e.active)
                .map(e => e.document.name));
            this.actionsImpactService.eventResultReceived.subscribe(res => {
                this.router.navigate([`detail/${this.actionsImpactService.emergencyId}/actions/adapt`]);
            });
            return;
        } else {
            this.addMessageNoCasesSelected();
        }
    }

    cancel() {
        this.router.navigate([`detail/${this.actionsImpactService.emergencyId}`]);
    }

    onPageChange(event) {
        this.historicEventsPage = this.historicEvents.slice(event.page * 5, event.page * 5 + 5);
    }
}
