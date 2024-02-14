import {RouterModule} from '@angular/router';
import {NgModule} from '@angular/core';
import {AppMainComponent} from './app.main.component';

import {DashboardComponent} from './dashboard/dashboard.component';
import {EmergencyDetailComponent} from './emergency-detail/emergency-detail.component';
import {EmergenciesCompleteComponent} from './emergencies-complete/emergencies-complete.component';
import {EmergenciesPendentComponent} from './emergencies-pendent/emergencies-pendent.component';
import { GraphSearchComponent } from './graph-search/graph-search.component';

@NgModule({
    imports: [
        RouterModule.forRoot([
            {
                path: '', component: AppMainComponent,
                // canActivate: [AuthGuard],
                children: [
                    {
                        path: '', component: DashboardComponent,
                        // canActivate: [AuthGuard]
                    },
                    {
                        path: 'report',
                        loadChildren: () => import('./emergency-report/emergency-report.module').then(m => m.EmergencyReportModule),
                        // canActivate: [AuthGuard]
                    },
                    {
                        path: 'xpert', component: GraphSearchComponent,
                        // canActivate: [AuthGuard]
                    },
                    {
                        path: 'past', component: EmergenciesCompleteComponent,
                        // canActivate: [AuthGuard]
                    },
                    {
                        path: 'pendent', component: EmergenciesPendentComponent,
                        // canActivate: [AuthGuard]
                    },
                    {
                        path: 'detail/:id', component: EmergencyDetailComponent,
                        // canActivate: [AuthGuard]
                    },
                    {
                        path: 'detail/:id/actions',
                        loadChildren: () => import('./actions-impact/similar-and-merge.module').then(m => m.SimilarAndMergeModule),
                        // canActivate: [AuthGuard]
                    }
                ]
            },
            // {path: 'login', component: LoginComponent},
        ], {scrollPositionRestoration: 'enabled'})
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
