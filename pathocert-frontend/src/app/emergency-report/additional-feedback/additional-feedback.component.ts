import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Emergency } from 'src/app/models/emergency';
import { EmergencyReportService } from 'src/app/services/emergency-report.service';
import { map } from 'rxjs/operators';
import { MultiselectValues } from '../../models/multiselectValues';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ExpertSearchComponent } from 'src/app/expert-search/expert-search.component';

interface MultiselectPossibleValues {
    symptoms: { name: string, value: string }[];
    infrastructures: { name: string, value: string }[];
    contaminants: { name: string, value: string }[];
}

@Component({
    selector: 'app-additional-feedback',
    templateUrl: './additional-feedback.component.html',
    styleUrls: ['./additional-feedback.component.scss']
})
export class AdditionalFeedbackComponent implements OnInit {

    divSize: number = 150;

    ref: DynamicDialogRef;

    dataAdditional: Emergency;
    multiselectValues: MultiselectPossibleValues = {
        symptoms: Object.entries({
            'water-bad': 'Water smells bad',
            'water-color': 'The colour of water is strange',
            'water-taste': 'The taste of water is strange'
        }
        ).map(([k, v]) => ({ name: v, value: k })),
        infrastructures: Object.entries({
            'distribution-broken': 'Drinking water distribution network broken',
            'pathogenssewage-failure': 'Sewage network failure',
            // flooding: 'Flooding'
        })
            .map(([k, v]) => ({ name: v, value: k })),
        contaminants: Object.entries({
            ecoli: 'Escherichia Coli',
            norovirus: 'Norovirus',
            rotavirus: 'Rotavirus'
        }
        ).map(([k, v]) => ({ name: v, value: k }))
    };

    constructor(private emergencyReportService: EmergencyReportService,
        private router: Router,
        private dialogService: DialogService) {
    }

    async ngOnInit(): Promise<void> {
        this.dataAdditional = this.emergencyReportService.emergencyReportData;
        let sub = await this.emergencyReportService.multiselectValues();
        sub.pipe(
            map((r: MultiselectValues) => ({
                    contaminants: Object.entries(r.contaminants).map(([k, v]) => ({name: v, value: k})),
                    infrastructures: Object.entries(r.infrastructures).map(([k, v]) => ({name: v, value: k})),
                    symptoms: Object.entries(r.symptoms).map(([k, v]) => ({name: v, value: k})),
                })
            )
        )
            .subscribe(s => {
                s.contaminants.sort((a, b) => a['name'].localeCompare(b['name']));
                s.infrastructures.sort((a, b) => a['name'].localeCompare(b['name']));
                s.symptoms.sort((a, b) => a['name'].localeCompare(b['name']));
                s.contaminants.unshift({'name': 'Other', 'value': 'Other'})
                s.infrastructures.unshift({'name': 'Other', 'value': 'Other'})
                s.symptoms.unshift({'name': 'Other', 'value': 'Other'})
                this.multiselectValues = s;
            });
    }

    prevPage() {
        this.router.navigate(['report/event']);
    }

    async complete() {
        await this.emergencyReportService.emergencyReportComplete();
    }

    cancel() {
        this.emergencyReportService.clearReportData.next();
        this.router.navigate(['']);
    }

    displayExpert() {
        this.ref = this.dialogService.open(ExpertSearchComponent, {
            header: 'Search the Expert Database',
            style: {
                'width': '70%'
            },
            contentStyle: {
                'overflow': 'visible'
            }
        });
    }

    tapValues(event: any) {
    }
}
