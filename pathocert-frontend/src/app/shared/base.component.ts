import {Directive, OnDestroy} from '@angular/core';
import {Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {MenuItem, MessageService, SelectItem} from 'primeng/api';
import {Severity} from 'src/app/models/emergency';
import {DropDownValuesService} from 'src/app/services/dropdown-values.service';
import {AuthService} from 'src/app/services/auth.service';
import { ApiService } from '../services/api.service';
import { KeycloakService } from 'keycloak-angular';

@Directive()
export abstract class BaseComponent implements OnDestroy {

    private subscriptions: Subscription[] = [];
    private breadcrumbItems: MenuItem[] = [];

    constructor(
        protected messageService: MessageService,
        protected dropDownValuesService: DropDownValuesService,
        protected router: Router,
        protected authService: AuthService) {
    }

    get breadcrumbs(): MenuItem[] {
        return this.breadcrumbItems;
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(subscription => subscription.unsubscribe());
    }

    runSubscription(subscription: Subscription) {
        this.subscriptions.push(subscription);
    }

    clearBreadcrumbs() {
        while (this.breadcrumbItems.length > 0) {
            this.breadcrumbItems.pop();
        }
    }

    addBreadcrumbItem(menuItem: MenuItem) {
        this.breadcrumbItems.push(menuItem);
    }

    manageError(summary: string, error: any) {
        let errorMessage: string;
        if (error.message) {
            errorMessage = error.message;
        } else {
            errorMessage = JSON.stringify(error, null, 2);
        }

        if (error.status === 500 && error.error.message === 'Account expired') {
            this.messageService.add({
                severity: 'error',
                summary: 'account-expired',
                detail: 'report-administrator',
                sticky: true,
                life: 4000
            });
        } else if (error.status === 500 && error.error.message === 'Invalid username/password supplied') {
            this.messageService.add({
                severity: 'error',
                summary: 'Login Error',
                detail: 'Invalid credentials',
                sticky: true,
                life: 4000
            });
        } else if (error.status === 400) {
            this.messageService.add({
                severity: 'error',
                summary: summary,
                detail: error.error.message,
                sticky: false,
                life: 4000
            });
        } else {
            this.displayUserMessage('error', summary, errorMessage);
        }
    }

    displaySuccessMessage(detail: string = 'Operation successful') {
        this.displayUserMessage('success', 'Success', detail);
    }

    displayInfoMessage(detail: string) {
        this.displayUserMessage('info', 'Info', detail);
    }

    displayWarningMessage(detail: string) {
        this.displayUserMessage('warn', 'Warning', detail);
    }

    getSeverityStyle(severity: number) {
        let style = 'secondary';
        switch (severity) {
            case 1:
                style = 'success';
                break;
            case 2:
                style = 'warning';
                break;
            case 3:
                style = 'danger';
                break;
            default:
                break;
        }
        return style;
    }

    getSeverity(severity: number) {
        let level = Severity.LOW;
        switch (severity) {
            case 1:
                level = Severity.LOW;
                break;
            case 2:
                level = Severity.MEDIUM;
                break;
            case 3:
                level = Severity.HIGH;
                break;
            default:
                break;
        }
        return level;
    }

    private displayUserMessage(severity: string, summary: string, detail: string) {
        this.messageService.add({
            severity,
            summary,
            detail,
            sticky: false,
            life: 4000
        });
    }
}
