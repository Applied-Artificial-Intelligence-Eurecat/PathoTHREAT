import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppBreadcrumbService } from 'src/app/app.breadcrumb.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

    constructor(private rout: Router,
        private breadcrumbService: AppBreadcrumbService) {
    }

    ngOnInit(): void {
        this.breadcrumbService.setItems([
            { label: '', routerLink: [''] }
        ]);
    }

    reportEmergency() {
        this.rout.navigate(['report']);
    }

    checkExpert() {
        this.rout.navigate(['xpert']);
    }
}
