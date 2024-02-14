import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { EmergencyReportComponent } from './emergency-report.component';
import { ReportInformationComponent } from './report-information/report-information.component';
import { EventDescriptionComponent } from './event-description/event-description.component';
import { AdditionalFeedbackComponent } from './additional-feedback/additional-feedback.component';

const routes: Routes = [
    {
        path: '',
        component: EmergencyReportComponent,
        children: [
            { path:'', redirectTo: 'info', pathMatch: 'full' },
            { path: 'info', component: ReportInformationComponent },
            { path: 'event', component: EventDescriptionComponent },
            { path: 'additional', component: AdditionalFeedbackComponent }
        ]
    },

];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class EmergencyReportRoutingModule {

}
