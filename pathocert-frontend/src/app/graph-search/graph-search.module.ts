import { NgModule } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { HttpClientModule } from '@angular/common/http';
import { EmergencyReportService } from '../services/emergency-report.service';
import { MessageService } from 'primeng/api';
import { SharedModule } from '../shared/shared.module';
import { ActionsImpactService } from '../services/actions-impact.service';
import { GraphSearchComponent } from './graph-search.component';
import { InteractiveGraphComponent } from './interactive-graph/interactive-graph.component';
import { GraphInfoComponent } from './interactive-graph/graph-info/graph-info.component';

@NgModule({
  declarations: [
    GraphSearchComponent,
    InteractiveGraphComponent,
    GraphInfoComponent
  ],
  imports: [
    SharedModule,
    ButtonModule,
    DropdownModule,
    HttpClientModule
  ],
  exports: [
    GraphSearchComponent,
    InteractiveGraphComponent
  ],
  providers: [
    EmergencyReportService,
    ActionsImpactService,
    MessageService
  ]
})
export class GraphSearchModule { }
