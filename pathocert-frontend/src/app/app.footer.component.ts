import {Component} from '@angular/core';
import {AppComponent} from './app.component';

@Component({
    selector: 'app-footer',
    template: `
        <div class="layout-footer">
            <a id="footerlogolink">
                <img id="footer-logo"
                     [src]="'assets/images/pathocert-logo.jpg'" width="80vw" alt="posedion-layout">
            </a>
            <div class="social-icons">
            </div>
        </div>
    `
})
export class AppFooterComponent {

    constructor(public app: AppComponent) {}


}
