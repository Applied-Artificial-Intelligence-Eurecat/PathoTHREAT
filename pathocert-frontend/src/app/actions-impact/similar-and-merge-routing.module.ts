import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SimilarAndMergeComponent } from './similar-and-merge.component';
import { EditMergeComponent } from './edit-merge/edit-merge.component';
import { MergeEventsComponent } from './merge-events/merge-events.component';
import { HistoricEventsComponent } from './historic-events/historic-events.component';

const routes: Routes = [
    {
        path: '',
        component: SimilarAndMergeComponent,
        children: [
            { path: '', redirectTo: 'historic', pathMatch: 'full' },
            { path: 'historic', component: HistoricEventsComponent },
            { path: 'adapt', children: [
                { path: '', component: MergeEventsComponent},
                { path: 'ask', component: EditMergeComponent}
            ] }
        ]
    },

];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SimilarAndMergeRoutingModule {

}
