import {Component, Input, OnInit} from '@angular/core';

@Component({
    selector: 'app-field-list',
    templateUrl: './field-list.component.html',
    styleUrls: ['./field-list.component.scss']
})
export class FieldListComponent implements OnInit {

    @Input() title: string;
    @Input() ls: string[];

    constructor() {
    }

    ngOnInit(): void {
    }


    isShown(): boolean {
        return JSON.parse(JSON.stringify(this.ls)).toString() !== '' && this.ls !== undefined;
    }
}
