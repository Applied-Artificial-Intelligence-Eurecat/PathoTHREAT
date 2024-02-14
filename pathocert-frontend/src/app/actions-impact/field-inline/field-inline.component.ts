import {Component, Input, OnInit} from '@angular/core';

@Component({
    selector: 'app-field-inline',
    templateUrl: './field-inline.component.html',
    styleUrls: ['./field-inline.component.scss']
})
export class FieldInlineComponent implements OnInit {

    @Input() title: string;
    @Input() information: string;
    @Input() suffix = '';

    constructor() {
    }

    ngOnInit(): void {
    }

    isShown(): boolean {
        return this.information !== '' && this.information !== undefined && this.information !== null;
    }
}
