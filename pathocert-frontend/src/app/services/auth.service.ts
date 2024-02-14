import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { LoginResult } from 'src/app/models/loginResult';

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    constructor(private apiService: ApiService) {
    }


    login(username: string, password: string): Observable<LoginResult> {
        return this.apiService.login(username, password).pipe(
            map((result: LoginResult) => {
                console.log(result);
                return (result);
            }));
    }
}
