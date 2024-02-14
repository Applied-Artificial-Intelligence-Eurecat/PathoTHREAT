import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { MessageService } from 'primeng/api';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { Emergency } from 'src/app/models/emergency';
import { EmergencyService } from 'src/app/services/emergency.service';
import { BaseComponent } from 'src/app/shared/base.component';
import { DropDownValuesService } from 'src/app/services/dropdown-values.service';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-emergencies-pendent',
  templateUrl: './emergencies-pendent.component.html',
  styleUrls: ['./emergencies-pendent.component.scss']
})
export class EmergenciesPendentComponent extends BaseComponent implements OnInit {
    pendentEmergencies: Array<Emergency> = [];
    subscription: Subscription;
    cardIdClicked: number = null;

    constructor(private emergencyService: EmergencyService,
        private breadcrumbService: AppBreadcrumbService,
        private rout: Router,
        messageService: MessageService,
        dropDownValuesService: DropDownValuesService,
        router: Router,
        authService: AuthService) {
            super(messageService, dropDownValuesService, router, authService);
            this.breadcrumbService.setItems([
                { label: 'Ongoing emergencies', routerLink: ['/pendent'] }
            ]);

    }

    async ngOnInit(): Promise<void> {
        let subsc = await this.emergencyService.findAllPendent();
        super.runSubscription(subsc.subscribe(response => {
            this.pendentEmergencies = response;
        }, error =>
            super.manageError('Error retrieving pendent emergencies', error)
        ));
    }


    ngOnDestroy() {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    public onCardSelected(event, data) {
        //this.messageService.add({severity:'info', summary:'Row Selected', detail: event.data.name});
        this.rout.navigate([`detail/${data.id}`]);
    }

}
