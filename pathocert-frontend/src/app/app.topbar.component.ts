import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { AppComponent } from './app.component';
import { AppMainComponent } from './app.main.component';
import { AuthService } from './services/auth.service';
import { DropDownValuesService } from './services/dropdown-values.service';
import { BaseComponent } from './shared/base.component';
import { Observable } from 'rxjs';
import { KeycloakService } from 'keycloak-angular';

@Component({
    selector: 'app-topbar',
    templateUrl: './app.topbar.component.html'
})
export class AppTopBarComponent extends BaseComponent implements OnInit {

    tokenStream: Observable<boolean>;

    userIsLogged: boolean = false;
    username: string = "";

    constructor(
        public appMain: AppMainComponent,
        public app: AppComponent,
        messageService: MessageService,
        dropDownValuesService: DropDownValuesService,
        router: Router,
        authService: AuthService,
        private keycloak: KeycloakService) {
        super(messageService, dropDownValuesService, router, authService);
    }

    async ngOnInit(): Promise<void> {
        this.userIsLogged = await this.keycloak.isLoggedIn();
        if (this.userIsLogged) {
            this.username = this.keycloak.getUsername();
        }
    }

    onLogin(): void {
        this.keycloak.login();
    }

    onLogout(): void {
        this.keycloak.logout();
    }
}
