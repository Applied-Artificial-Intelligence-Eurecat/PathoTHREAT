import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MenuItem, MessageService } from 'primeng/api';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';
import { EmergencyReportService } from 'src/app/services/emergency-report.service';
import { AuthService } from '../services/auth.service';
import { DropDownValuesService } from '../services/dropdown-values.service';
import { BaseComponent } from '../shared/base.component';

@Component({
  selector: 'app-emergency-report',
  templateUrl: './emergency-report.component.html',
  styleUrls: ['./emergency-report.component.scss']
})
export class EmergencyReportComponent extends BaseComponent implements OnInit {

    items: MenuItem[] = [];

    constructor(private breadcrumbService: AppBreadcrumbService,
        private emergencyReportService: EmergencyReportService,
        private rout: Router,
        messageService: MessageService,
        dropDownValuesService: DropDownValuesService,
        router: Router,
        authService: AuthService) {
            super(messageService, dropDownValuesService, router, authService);

        this.breadcrumbService.setItems([
            { label: 'Emergency report', routerLink: ['/report'] }
        ]);
    }

    ngOnInit(): void {
        this.items = [
            {
                label: 'Reporter info',
                routerLink: 'info'
            },
            {
                label: 'Event description',
                routerLink: 'event'
            },
            {
                label: 'Additional feedback',
                routerLink: 'additional'
            }
        ];

        super.runSubscription(this.emergencyReportService.reportComplete.subscribe((reportData) => {
            super.displaySuccessMessage('Emergency report submitted.');
            this.rout.navigate(['/report']);
        }, error => super.manageError('Error archiving emergency report', error)));
    }

}
