import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { Emergency } from 'src/app/models/emergency';
import { EmergencyReportService } from 'src/app/services/emergency-report.service';

@Component({
    selector: 'app-report-information',
    templateUrl: './report-information.component.html',
    styleUrls: ['./report-information.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ReportInformationComponent implements OnInit {

    dataInfo: Emergency;
    submitted: boolean = false;

    scenariosList: {}[] = [
        { 'optionName': 'Amsterdam', 'optionValue': 'amsterdam' },
        { 'optionName': 'Granada', 'optionValue': 'granada' },
        { 'optionName': 'Limassol', 'optionValue': 'limassol' },
        { 'optionName': 'Seoul', 'optionValue': 'seoul' },
        { 'optionName': 'Sofia', 'optionValue': 'sofia' },
        { 'optionName': 'Thessaloniki', 'optionValue': 'thessaloniki' },
    ];

    constructor(private emergencyReportService: EmergencyReportService,
        private router: Router) { }

    ngOnInit(): void {
        this.dataInfo = this.emergencyReportService.emergencyReportData;
    }

    nextPage() {
        if (this.dataInfo.nameReporter && this.dataInfo.ocupationDescription && this.dataInfo.emergencyName && this.dataInfo.scenario) {
            if (this.dataInfo.affectedAreaLat == null) {
                this.setSelectedCoordinates(this.dataInfo.scenario);
            }
            this.emergencyReportService.emergencyReportData = this.dataInfo;
            this.router.navigate(['report/event']);
            return;
        }
        this.submitted = true;
    }

    cancel() {
        this.emergencyReportService.clearReportData.next();
        this.router.navigate(['']);
    }

    setSelectedCoordinates(scenario: string) {
        switch (scenario) {
            case 'amsterdam':
                this.dataInfo.affectedAreaLat = 52.370176;
                this.dataInfo.affectedAreaLon = 4.909025;
                break;
            case 'granada':
                this.dataInfo.affectedAreaLat = 37.204671;
                this.dataInfo.affectedAreaLon = -3.617582;
                break;
            case 'limassol':
                this.dataInfo.affectedAreaLat = 34.681248;
                this.dataInfo.affectedAreaLon = 33.028023;
                break;
            case 'seoul':
                this.dataInfo.affectedAreaLat = 37.548859;
                this.dataInfo.affectedAreaLon = 126.977784;
                break;
            case 'sofia':
                this.dataInfo.affectedAreaLat = 42.700505;
                this.dataInfo.affectedAreaLon = 23.329128;
                break;
            case 'thessaloniki':
                this.dataInfo.affectedAreaLat = 40.649074;
                this.dataInfo.affectedAreaLon = 22.919062;
                break;
            default:
                this.dataInfo.affectedAreaLat = 52.018654708123215;
                this.dataInfo.affectedAreaLon = 5.108985335711112;
        }
    }
}


