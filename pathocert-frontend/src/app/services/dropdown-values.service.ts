import {Injectable} from '@angular/core';
import {SelectItem} from 'primeng/api';
import {AreaType} from 'src/app/models/area';
import {EventType} from 'src/app/models/event';
import {ApiService} from './api.service';

@Injectable()
export class DropDownValuesService {

    constructor(private apiService: ApiService) {
    }

    areaTypeAsDropdown(): SelectItem[] {
        return Object.keys(AreaType)
            .filter(item => isNaN(Number(item)))
            .map(key => ({label: key, value: AreaType[key]}));
    }
}
