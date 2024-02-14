import {AfterViewInit, Component} from '@angular/core';
import {ActionsImpactService} from '../services/actions-impact.service';
import {addWarning} from "@angular-devkit/build-angular/src/utils/webpack-diagnostics";

@Component({
    selector: 'app-graph-search',
    templateUrl: './graph-search.component.html',
    styleUrls: ['./graph-search.component.scss']
})
export class GraphSearchComponent implements AfterViewInit {

    nodes = [];
    relations = [];

    pause = false;

    constructor(private actionsImpactService: ActionsImpactService) {
    }

    async ngAfterViewInit(): Promise<void> {
        await this.updateItems();
    }

    private async updateItems() {
        let sub = await this.actionsImpactService.detailItems();
        sub.subscribe(ls => {
            var graphItems = ls['_embedded']['graphItems'];
            this.nodes = [];
            this.relations = [];
            graphItems.map(ll => {
                if (ll['group'] === "nodes") {
                    this.nodes = [...this.nodes, {
                        group: "nodes",
                        name: ll['value'],
                        label: ll['label'],
                        color: this.labelToColor[ll['label']],
                        clearColor: this.clearerColors[this.labelToColor[ll['label']]]
                    }];
                } else {
                    this.relations = [...this.relations, {
                        group: "edges",
                        source: ll['source'],
                        target: ll['target'],
                        label: ll['label']
                    }];
                }
                this.pause = false;
            });
        });
    }

    labelToColor = {
        "Event": "#E3782F",
        "CascadingWNEvent": "#972029",
        "ProducedEvent": "#DDC332",
        "Contaminant": "#86B0A5",
        "EffectHealth": "#DBB065",
        "Symptom": "#EA7E82",
        "ContaminantTreatment": "#2d6049",
        "ContaminantFamily": "#d32f27",
        "ContaminantType": "#433d67",
        "EffectWaterTaste": "#547b86",
        "EffectWaterOdor": "#a5c4cf",
        "EffectWater": "#e27655",
        "ContaminantMitigation": "#358cb4",
    }

    clearerColors = {
        "#E3782F": "#d4976e",
        "#972029": "#8c5458",
        "#DDC332": "#d4c885",
        "#86B0A5": "#c5ded7",
        "#DBB065": "#cfbc9b",
        "#EA7E82": "#e6bcbe",
        "#2d6049": "#87a899",
        "#d32f27": "#f2cdcb",
        "#433d67": "#918da8",
        "#547b86": "#bcccd1",
        "#a5c4cf": "#edf3f5",
        "#e27655": "#91776e",
        "#358cb4": "#98c8de"
    }

    async confirmGraph() {
        this.pause = true;
        let sub = await this.actionsImpactService.detailItemsSearch(this.term1user, this.term2user);
        sub.subscribe(ls => {
            var graphItems = ls['_embedded']['graphItems'];
            this.nodes = [];
            this.relations = [];
            graphItems.map(ll => {
                if (ll['group'] === "nodes") {
                    this.nodes = [...this.nodes, {
                        group: "nodes",
                        name: ll['value'],
                        label: ll['label'],
                        color: this.labelToColor[ll['label']],
                        clearColor: this.clearerColors[this.labelToColor[ll['label']]]
                    }];
                } else {
                    this.relations = [...this.relations, {
                        group: "edges",
                        source: ll['source'],
                        target: ll['target'],
                        label: ll['label']
                    }];
                }
                this.pause = false;
            });
        });
    }

    async resetGraph() {
        this.pause = true;
        await this.updateItems();
    }

    term1user = '';
    term2user = '';
    searchedResult = '';
    labels: { name: string }[] = [];
    values: { name: string }[] = [];

    subjectDisabled: boolean = false;
    outputDisabled: boolean = false;
    searchDisabled: boolean = true;

    ngOnInit(): void {
        this.updateValuesAndLabels();
    }

    private async updateValuesAndLabels() {
        this.outputDisabled = true;
        this.subjectDisabled = true;

        let sub = await this.actionsImpactService.detailLabels();
        sub.subscribe(ls => {
            this.labels = ls.map(ll => ({name: ll}));
            this.outputDisabled = false;
        });

        sub = await this.actionsImpactService.detailValues();
        sub.subscribe(vs => {
            this.values = vs.map(v => ({name: v}));
            this.subjectDisabled = false;
        });
    }

    private async updateOutputLabels() {
        this.outputDisabled = true;
        let sub = await this.actionsImpactService.specificLabels(this.term1user);
        sub.subscribe(
            ls => {
                this.labels = ls.map(l => ({name: l}));
                this.outputDisabled = false;
            }
        );
    }

    private async updateSubjectValues() {
        this.subjectDisabled = true;
        let sub = await this.actionsImpactService.specificValues(this.term2user);
        sub.subscribe(
            ls => {
                this.values = ls.map(l => ({name: l}));
                this.subjectDisabled = false;
            }
        );
    }

    async saveTermAndGetLabels(event: any) {
        this.term1user = event.value === null ? '' : event.value;
        if (this.term1user !== '') {
            if (this.term2user === '') {
                await this.updateOutputLabels();
            } else {
                this.searchDisabled = false;
            }
        } else {
            this.searchDisabled = true;
            if (this.term2user === '') {
                await this.updateValuesAndLabels();
            } else {
                await this.updateSubjectValues();
            }
        }
    }

    async saveTermAndGetValues(event: any) {
        this.term2user = event.value === null ? '' : event.value;
        if (this.term2user !== '') {
            if (this.term1user === '') {
                await this.updateSubjectValues();
            } else {
                this.searchDisabled = false;
            }
        } else {
            this.searchDisabled = true;
            if (this.term1user === '') {
                await this.updateValuesAndLabels();
            } else {
                await this.updateOutputLabels();
            }
        }
    }
}
