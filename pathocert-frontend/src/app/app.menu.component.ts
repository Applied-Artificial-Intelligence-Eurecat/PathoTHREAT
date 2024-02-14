import {Component, OnInit} from '@angular/core';
import {AppComponent} from './app.component';
import { MenuItem, MessageService } from 'primeng/api';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';
import { DropDownValuesService } from './services/dropdown-values.service';
import { BaseComponent } from './shared/base.component';
import { KeycloakService } from 'keycloak-angular';

@Component({
    selector: 'app-menu',
    template: `
        <ul class="layout-menu">
            <li app-menuitem *ngFor="let item of model; let i = index;" [item]="item" [index]="i" [root]="true"></li>
        </ul>
    `
})
export class AppMenuComponent extends BaseComponent implements OnInit {

    model: any[];

    constructor(public app: AppComponent,
        messageService: MessageService,
        dropDownValuesService: DropDownValuesService,
        router: Router,
        authService: AuthService,
        private keycloak: KeycloakService) {
            super(messageService, dropDownValuesService, router, authService);
    }

    ngOnInit() {
        this.model = [
            {
                label: '', icon: 'pi pi-fw pi-home',
                items: [
                    {label: 'Home', icon: 'pi pi-fw pi-home', routerLink: ['/dashboard']},
                    {label: 'Report emergency', icon: 'pi pi-fw pi-exclamation-circle', routerLink: ['/report']},
                    {label: 'Ongoing emergencies', icon: 'pi pi-fw pi-clock', routerLink: ['/pendent']},
                    {label: 'Past emergencies', icon: 'pi pi-fw pi-calendar', routerLink: ['/past']},
                ]
            },
            {
                label: 'Administration', icon: 'pi pi-fw pi-home',
                items: [
                    {label: 'Organizations', icon: 'pi pi-fw pi-briefcase'}
                ]
            },
            {
                label: '',
                items: [
                    {label: 'Report a bug', icon: 'pi pi-fw pi-exclamation-triangle'},
                    {label: 'Logout', icon: 'pi pi-fw pi-sign-out', command: (event?: any) => this.logout()}
                ]
            }
        ];
    }

    logout() {
        this.keycloak.logout();
    }
}
