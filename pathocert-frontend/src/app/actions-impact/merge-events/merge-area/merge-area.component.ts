import {Component, Input, OnInit} from '@angular/core';
import {EventMerged} from '../../../models/event';

@Component({
    selector: 'app-merge-area',
    templateUrl: './merge-area.component.html',
    styleUrls: ['./merge-area.component.scss']
})
export class MergeAreaComponent implements OnInit {

    @Input() event: EventMerged;

    constructor() {
    }

    ngOnInit(): void {
    }

}
