import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {EventMerged} from 'src/app/models/event';
import {ActionsImpactService} from 'src/app/services/actions-impact.service';

type Status = 'Loading' | 'Result' | 'Error';

@Component({
    selector: 'app-merge-events',
    templateUrl: './merge-events.component.html',
    styleUrls: ['./merge-events.component.scss']
})
export class MergeEventsComponent implements OnInit, OnDestroy {

    event: EventMerged = new EventMerged();
    impactHtml = '';
    controlHtml = '';
    status: Status = 'Loading';

    eventSubs: Subscription;
    impactSubs: Subscription;
    controlSubs: Subscription;

    constructor(
        private actionsImpactService: ActionsImpactService,
        private router: Router) {
    }

    ngOnInit(): void {
        this.eventSubs = this.actionsImpactService.eventResultReceived.subscribe(e => {
            if (e === null) {
                this.status = 'Loading';
            } else {
                this.status = 'Result';
                this.event = e;
            }
        }, _ => {
            this.status = 'Error';
        });
        this.impactSubs = this.actionsImpactService.controlHtml.subscribe(s => {
            this.controlHtml = s;
        });
        this.controlSubs = this.actionsImpactService.impactHtml.subscribe(s => {
            this.impactHtml = s;
        });
    }

    ngOnDestroy(): void {
        this.eventSubs.unsubscribe();
        this.impactSubs.unsubscribe();
        this.controlSubs.unsubscribe();
    }

    prevPage() {
        this.router.navigate([`detail/${this.actionsImpactService.emergencyId}/actions/historic`]);
    }

    editImpact = () => {
        this.actionsImpactService.flagAskImpact = true;
        this.edit();
    }

    editControl = () => {
        this.actionsImpactService.flagAskControl = true;
        this.edit();
    }

    edit() {
        this.router.navigate([`detail/${this.actionsImpactService.emergencyId}/actions/adapt/ask`]);
    }

    async complete() {
        await this.actionsImpactService.eventImpactControlComplete();
    }

    cancel() {
        this.router.navigate([`detail/${this.actionsImpactService.emergencyId}`]);
    }
}
