import { Links } from "./links";

export class Organization {
    id?: number;
    name: string;
    description: string;
    actionArea: ActionArea;
    _links?: Links;
}

export enum ActionArea {
	AREA1,
    AREA2,
    AREA3
}
