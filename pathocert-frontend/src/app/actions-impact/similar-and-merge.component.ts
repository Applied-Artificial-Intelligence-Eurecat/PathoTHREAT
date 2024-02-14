import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MenuItem, MessageService} from 'primeng/api';
import {Subscription} from 'rxjs';
import {AppBreadcrumbService} from 'src/app/app.breadcrumb.service';
import {ActionsImpactService} from 'src/app/services/actions-impact.service';
import {EmergencyService} from 'src/app/services/emergency.service';
import {Emergency} from 'src/app/models/emergency';
import {BaseComponent} from 'src/app/shared/base.component';
import {DropDownValuesService} from 'src/app/services/dropdown-values.service';
import {AuthService} from 'src/app/services/auth.service';

@Component({
    selector: 'app-actions-impact',
    templateUrl: './similar-and-merge.component.html',
    styleUrls: ['./similar-and-merge.component.scss']
})
export class SimilarAndMergeComponent extends BaseComponent implements OnInit, OnDestroy {

    items: MenuItem[] = [];
    subscription: Subscription;
    emergency: Emergency;

    constructor(private breadcrumbService: AppBreadcrumbService,
                private actionsImpactService: ActionsImpactService,
                private rout: Router,
                private activatedRoute: ActivatedRoute,
                private emergencyService: EmergencyService,
                messageService: MessageService,
                dropDownValuesService: DropDownValuesService,
                router: Router,
                authService: AuthService) {
        super(messageService, dropDownValuesService, router, authService);

        this.breadcrumbService.setItems([
            {label: 'Emergency detail'},
            {label: 'Provide control actions and impact assessment'}
        ]);
    }

    async ngOnInit(): Promise<void> {
        const id = this.activatedRoute.snapshot.paramMap.get('id');
        if (id !== null) {
            this.actionsImpactService.emergencyId = Number(id);
            await this.actionsImpactService.initializeData();
            await this.getEmergencyById(Number(id));
        }
        this.items = [
            {
                label: 'Select similar past events',
                routerLink: 'historic'
            },
            {
                label: 'Adapt',
                routerLink: 'adapt'
            }
        ];
    }

    ngOnDestroy() {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    async getEmergencyById(id: number) {
        let sub = await this.emergencyService.retrieveEmergencyById(id);
        super.runSubscription(sub.subscribe(response => {
            this.emergency = response;
        }, error => super.manageError('Error retrieving emergency', error)));
    }
}
