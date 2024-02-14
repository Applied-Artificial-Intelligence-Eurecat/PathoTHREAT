import {Component, Input, OnInit} from '@angular/core';
import {EventSimilarity} from '../../../models/event';

@Component({
    selector: 'app-historic-generic-investigations',
    templateUrl: './historic-generic-investigations.component.html',
    styleUrls: ['./historic-generic-investigations.component.scss']
})
export class HistoricGenericInvestigationsComponent implements OnInit {

    @Input() event: EventSimilarity;

    constructor() {
    }

    ngOnInit(): void {
    }

}
