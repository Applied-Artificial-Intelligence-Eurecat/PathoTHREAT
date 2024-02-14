import {Component, Input, OnInit} from '@angular/core';
import {EventSimilarity} from '../../../models/event';

@Component({
    selector: 'app-historic-detection',
    templateUrl: './historic-detection.component.html',
    styleUrls: ['./historic-detection.component.scss']
})
export class HistoricDetectionComponent implements OnInit {

    @Input() event: EventSimilarity;

    constructor() {
    }

    ngOnInit(): void {
    }

}
