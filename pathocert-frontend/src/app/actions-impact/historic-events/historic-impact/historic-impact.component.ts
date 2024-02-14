import {Component, Input, OnInit} from '@angular/core';
import {EventSimilarity} from '../../../models/event';

@Component({
    selector: 'app-historic-impact',
    templateUrl: './historic-impact.component.html',
    styleUrls: ['./historic-impact.component.scss']
})
export class HistoricImpactComponent implements OnInit {

    @Input() event: EventSimilarity;

    constructor() {
    }

    ngOnInit(): void {
    }

}
