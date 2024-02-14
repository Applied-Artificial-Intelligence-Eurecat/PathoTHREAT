import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { LoginResult } from 'src/app/models/loginResult';
import { BaseComponent } from 'src/app/shared/base.component';
import { DropDownValuesService } from 'src/app/services/dropdown-values.service';
import { AuthService } from 'src/app/services/auth.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent extends BaseComponent implements OnInit {

    loginForm: FormGroup;
    loginError = false;

    constructor(
        private fb: FormBuilder,
        private authenticationService: AuthService,
        private rout: Router,
        messageService: MessageService,
        dropDownValuesService: DropDownValuesService,
        router: Router,
        authService: AuthService) {
        super(messageService, dropDownValuesService, router, authService);
    }

    username() { return this.loginForm.get('username'); }
    password() { return this.loginForm.get('password'); }

    ngOnInit(): void {
        this.loginForm = this.fb.group({
            username: new FormControl('', Validators.required),
            password: new FormControl('', Validators.required)
        });
    }

    login() {
        super.runSubscription(this.authenticationService.login(this.username().value, this.password().value).subscribe(
            async (response: LoginResult) => {
                this.rout.navigateByUrl('');
            },
            error => {
                super.manageError("Login error", {status: 500, error: {message: 'Invalid username/password supplied'}})
            }
        ));
    }

}
