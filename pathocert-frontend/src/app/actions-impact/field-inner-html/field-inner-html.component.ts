import {Component, Input, OnInit} from '@angular/core';
import {EventSimilarity} from '../../models/event';

@Component({
    selector: 'app-field-inner-html',
    templateUrl: './field-inner-html.component.html',
    styleUrls: ['./field-inner-html.component.scss']
})
export class FieldInnerHtmlComponent implements OnInit {

    @Input() event: EventSimilarity;

    constructor() {
    }

    ngOnInit(): void {
    }


    isShown(): boolean {
        return this.event.document.text !== '' && this.event.document.text !== undefined;
    }
}
