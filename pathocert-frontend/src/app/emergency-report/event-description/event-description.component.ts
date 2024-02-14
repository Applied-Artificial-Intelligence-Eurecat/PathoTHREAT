import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MenuItem, SelectItem, MessageService } from 'primeng/api';
import { Emergency, Severity } from 'src/app/models/emergency';
import { EmergencyReportService } from 'src/app/services/emergency-report.service';
import { DropDownValuesService } from 'src/app/services/dropdown-values.service';
import { EmergencyService } from 'src/app/services/emergency.service';
import { BaseComponent } from 'src/app/shared/base.component';
import { AuthService } from 'src/app/services/auth.service';
import { ApiService } from 'src/app/services/api.service';
import LocationPicker from 'location-picker';

@Component({
    selector: 'app-event-description',
    templateUrl: './event-description.component.html',
    styleUrls: ['./event-description.component.scss']
})
export class EventDescriptionComponent extends BaseComponent implements OnInit {

    dataEvent: Emergency;
    submitted: boolean = false;

    eventTypeList: {}[] = [];
    areaTypeList: Array<SelectItem> = [];
    infrastructureList: {}[] = [];

    lp: LocationPicker;

    constructor(private emergencyReportService: EmergencyReportService,
        private rout: Router,
        messageService: MessageService,
        dropDownValuesService: DropDownValuesService,
        router: Router,
        authService: AuthService,
        private apiService: ApiService) {
        super(messageService, dropDownValuesService, router, authService);
    }

    async ngOnInit(): Promise<void> {
        this.dataEvent = this.emergencyReportService.emergencyReportData;

        this.eventTypeList.push({'optionName': 'Other'});
        let sub = await this.apiService.specifyDropdownValues("TypeOfEventClass");
        sub.subscribe(
            (st: string[]) => {
                st.sort((a, b) => a.localeCompare(b))
                st.forEach(arr_elem => this.eventTypeList.push({'optionName': arr_elem}));
            }
        );

        this.infrastructureList.push({'optionName': 'Other'})
        sub = await this.apiService.specifyDropdownValues("Infrastructure");
        sub .subscribe(
            (st: string[]) => {
                st.sort((a, b) => a.localeCompare(b))
                st.forEach(arr_elem => this.infrastructureList.push({'optionName': arr_elem}));
            }
        );
        this.areaTypeList = this.dropDownValuesService.areaTypeAsDropdown();

        this.lp = new LocationPicker('map', {
            setCurrentPosition: false
        }, {
            zoom: 11,
            streetViewControl: false,
            fullscreenControl: false,
            disableDoubleClickZoom: true,
            scrollwheel: false
        });
        this.lp.setLocation(this.dataEvent.affectedAreaLat, this.dataEvent.affectedAreaLon);
    }

    nextPage() {
        if (this.dataEvent.emergencyTypeClass !== null && this.dataEvent.infrastructureConcerns !== null
            && this.dataEvent.emergencyDescription !== null) {
            var coords = this.lp.getMarkerPosition();
            this.dataEvent.affectedAreaLat = coords.lat;
            this.dataEvent.affectedAreaLon = coords.lng;
            this.emergencyReportService.emergencyReportData = this.dataEvent;
            this.rout.navigate(['report/additional']);
            return;
        }
        this.submitted = true;
    }

    prevPage() {
        var coords = this.lp.getMarkerPosition();
        this.dataEvent.affectedAreaLat = coords.lat;
        this.dataEvent.affectedAreaLon = coords.lng;
        this.rout.navigate(['report/info']);
    }

    cancel() {
        this.emergencyReportService.clearReportData.next();
        this.rout.navigate(['']);
    }

}
