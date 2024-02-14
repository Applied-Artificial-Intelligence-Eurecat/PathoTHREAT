import { NgModule } from '@angular/core';

import { StepsModule } from 'primeng/steps';

import { SharedModule } from '../shared/shared.module';

import { CommonModule } from '@angular/common';
import { SimilarAndMergeComponent } from './similar-and-merge.component';
import { SimilarAndMergeRoutingModule } from './similar-and-merge-routing.module';
import { MergeEventsComponent } from './merge-events/merge-events.component';
import { HistoricEventsComponent } from './historic-events/historic-events.component';
import { ActionsImpactService } from '../services/actions-impact.service';
import { EditMergeComponent } from './edit-merge/edit-merge.component';
import { FieldInlineComponent } from './field-inline/field-inline.component';
import { FieldInnerHtmlComponent } from './field-inner-html/field-inner-html.component';
import { FieldListComponent } from './field-list/field-list.component';
import { HistoricControlComponent } from './historic-events/historic-control/historic-control.component';
import { HistoricDetectionComponent } from './historic-events/historic-detection/historic-detection.component';
import { HistoricImpactComponent } from './historic-events/historic-impact/historic-impact.component';
import { MergeAreaComponent } from './merge-events/merge-area/merge-area.component';
import { HistoricGenericInvestigationsComponent } from './historic-events/historic-generic-investigations/historic-generic-investigations.component';
import { ExpertSearchModule } from "../expert-search/expert-search.module";
import { PaginatorModule } from 'primeng/paginator';



@NgModule({
    declarations: [
        HistoricEventsComponent,
        SimilarAndMergeComponent,
        MergeEventsComponent,
        EditMergeComponent,
        FieldInlineComponent,
        FieldInnerHtmlComponent,
        FieldListComponent,
        HistoricControlComponent,
        HistoricDetectionComponent,
        HistoricImpactComponent,
        MergeAreaComponent,
        HistoricGenericInvestigationsComponent,
    ],
    providers: [
        ActionsImpactService
    ],
    imports: [
        SharedModule,
        CommonModule,
        SimilarAndMergeRoutingModule,
        StepsModule,
        ExpertSearchModule,
        PaginatorModule
    ]
})
export class SimilarAndMergeModule { }
