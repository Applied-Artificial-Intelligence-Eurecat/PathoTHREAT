import {Injectable} from '@angular/core';

import {Observable, Subject} from 'rxjs';
import {MessageService} from 'primeng/api';

import {Emergency} from 'src/app/models/emergency';
import {ApiService} from 'src/app/services/api.service';
import {EmergencyService} from 'src/app/services/emergency.service';
import {Router} from '@angular/router';
import {MultiselectValues} from '../models/multiselectValues';

@Injectable()
export class EmergencyReportService {

    emergencyReportData: Emergency = new Emergency();

    reportComplete = new Subject<Emergency>();
    clearReportData = new Subject<any>();

    constructor(private apiService: ApiService,
                private emergencyService: EmergencyService,
                private messageService: MessageService,
                private router: Router) {
        this.initData();
        this.clearReportData.subscribe(res => this.initData());
    }

    initData() {
        this.emergencyReportData.nameReporter = '';
        this.emergencyReportData.ocupationDescription = '';

        this.emergencyReportData.emergencyTypeClass = null;
        this.emergencyReportData.infrastructureConcerns = null;
        this.emergencyReportData.affectedAreaLat = null;
        this.emergencyReportData.affectedAreaLon = null;
        this.emergencyReportData.emergencyDescription = null;

        this.emergencyReportData.additionalObservations = '';
        this.emergencyReportData.archived = false;
        this.emergencyReportData.impact = '';
        this.emergencyReportData.control = '';

        this.emergencyReportData.contaminants = [];
        this.emergencyReportData.infrastructures = [];
        this.emergencyReportData.symptoms = [];

    }

    async multiselectValues(): Promise<Observable<MultiselectValues>> {
        return await this.apiService.multiselectValues();
    }

    async emergencyReportComplete() {
        let sub = await this.emergencyService.createEmergency(this.emergencyReportData);
        sub.subscribe(response => {
            this.reportComplete.next(this.emergencyReportData);
            this.clearReportData.next();
            this.router.navigate(['']);
        }, error => this.messageService.add({
            severity: 'error',
            summary: 'error on create emergency',
            detail: error.error.message ? error.error.message : error.message,
            sticky: true,
            life: 4000
        }));
    }

}
