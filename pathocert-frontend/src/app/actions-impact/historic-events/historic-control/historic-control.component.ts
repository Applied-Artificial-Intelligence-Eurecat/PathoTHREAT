import {Component, Input, OnInit} from '@angular/core';
import {EventSimilarity} from '../../../models/event';

@Component({
    selector: 'app-historic-control',
    templateUrl: './historic-control.component.html',
    styleUrls: ['./historic-control.component.scss']
})
export class HistoricControlComponent implements OnInit {

    @Input() event: EventSimilarity;

    constructor() {
    }

    ngOnInit(): void {
    }

}
