import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-graph-info',
  templateUrl: './graph-info.component.html',
  styleUrls: ['./graph-info.component.scss']
})
export class GraphInfoComponent implements OnInit, OnChanges {

  @Input()
  public selectedThingType: string;
  @Input()
  public selectedThing: object;

  public calculatedGradient: string;

  constructor() { }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.selectedThingType === "Edge") {
      this.calculatedGradient = 'linear-gradient(to right,' + this.labelToColor[this.selectedThing['source']['label']] + ' 0%,' + this.labelToColor[this.selectedThing['target']['label']] + ' 100%)'
    }
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
    "EffectWater": "#e27655"
  }
}
