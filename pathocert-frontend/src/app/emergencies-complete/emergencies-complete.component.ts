import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService, SelectItem } from 'primeng/api';
import { Subscription } from 'rxjs';

import { environment } from 'src/environments/environment';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { Emergency } from 'src/app/models/emergency';
import { Page } from 'src/app/models/page';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { DropDownValuesService } from 'src/app/services/dropdown-values.service';
import { EmergencyService } from 'src/app/services/emergency.service';
import { BaseComponent } from 'src/app/shared/base.component';

@Component({
  selector: 'app-emergencies-complete',
  templateUrl: './emergencies-complete.component.html',
  styleUrls: ['./emergencies-complete.component.scss']
})
export class EmergenciesCompleteComponent extends BaseComponent implements OnInit {

    subscription: Subscription;
    environment = environment;
    pastEmergencyList: Array<Emergency> = [];
    selectedEmergency: Emergency;
    reportingUserList: Array<User> = [];

    constructor(private breadcrumbService: AppBreadcrumbService,
        private emergencyService: EmergencyService,
        private rout: Router,
        messageService: MessageService,
        dropDownValuesService: DropDownValuesService,
        router: Router,
        authService: AuthService) {
            super(messageService, dropDownValuesService, router, authService);
            this.breadcrumbService.setItems([
                { label: 'Past emergency', routerLink: ['/past'] }
            ]);
    }

    async ngOnInit(): Promise<void> {
        let sub = await this.emergencyService.findPageCompleteEmergencies();
        super.runSubscription(sub.subscribe(
            response => {
                this.pastEmergencyList = response;
            }, error => super.manageError('login error', error)));
    }

    ngOnDestroy() {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    onRowSelect(event) {
        //this.messageService.add({severity:'info', summary:'Row Selected', detail: event.data.name});
        this.rout.navigate([`detail/${event.data.id}`]);
    }

}
