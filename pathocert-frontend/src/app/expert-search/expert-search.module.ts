import { NgModule } from '@angular/core';
import { ExpertSearchComponent } from './expert-search.component';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { HttpClientModule } from '@angular/common/http';
import { EmergencyReportService } from '../services/emergency-report.service';
import { MessageService } from 'primeng/api';
import { SharedModule } from '../shared/shared.module';
import { ActionsImpactService } from '../services/actions-impact.service';


@NgModule({
  declarations: [ExpertSearchComponent],
  imports: [
    SharedModule,
    ButtonModule,
    DropdownModule,
    HttpClientModule,
  ],
  exports: [ExpertSearchComponent],
  providers: [
    EmergencyReportService,
    ActionsImpactService,
    MessageService
  ]
})
export class ExpertSearchModule { }
