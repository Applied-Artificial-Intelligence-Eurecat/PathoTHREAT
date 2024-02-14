import { NgModule } from '@angular/core';

import {StepsModule} from 'primeng/steps';
import { MessageService } from 'primeng/api';

import { SharedModule } from 'src/app/shared/shared.module';
import { EmergencyReportService } from 'src/app/services/emergency-report.service';
import { EmergencyReportRoutingModule } from './emergency-report-routing.module';
import { ReportInformationComponent } from './report-information/report-information.component';
import { EventDescriptionComponent } from './event-description/event-description.component';
import { AdditionalFeedbackComponent } from './additional-feedback/additional-feedback.component';
import { EmergencyReportComponent } from './emergency-report.component';
import {MultiSelectModule} from 'primeng/multiselect';
import { ExpertSearchModule } from "../expert-search/expert-search.module";
import { ActionsImpactService } from '../services/actions-impact.service';
import { DialogService } from 'primeng/dynamicdialog';

@NgModule({
    declarations: [
        EmergencyReportComponent,
        ReportInformationComponent,
        EventDescriptionComponent,
        AdditionalFeedbackComponent
    ],
    providers: [
        EmergencyReportService,
        MessageService,
        ActionsImpactService,
        DialogService
    ],
    imports: [
        SharedModule,
        EmergencyReportRoutingModule,
        StepsModule,
        MultiSelectModule,
        ExpertSearchModule
    ]
})
export class EmergencyReportModule {

}
