import {AfterViewInit, Component, Input, OnInit, OnChanges, SimpleChanges, ChangeDetectorRef} from '@angular/core';

import cytoscape from 'cytoscape';
import cola from 'cytoscape-cola';
import euler from 'cytoscape-euler';

@Component({
    selector: 'app-interactive-graph',
    templateUrl: './interactive-graph.component.html',
    styleUrls: ['./interactive-graph.component.scss']
})
export class InteractiveGraphComponent implements OnInit, AfterViewInit, OnChanges {
    @Input()
    public nodes: {}[];
    @Input()
    public relations: {}[];

    @Input()
    public pause: boolean;
    labelsXs: {} = {
        "Event": 0,
        "CascadingWNEvent": 1000,
        "EffectWater": 2000,
        "ProducedEvent": 3000,
        "Contaminant": 4000,
        "EffectHealth": 5000,
        "Symptom": 7000,
        "ContaminantTreatment": 8000,
        "ContaminantFamily": 9000,
        "ContaminantType": 10000,
        "EffectWaterTaste": 11000,
        "EffectWaterOdor": 12000,
        "ContaminantMitigation": 13000
    };
    totalHeight: number;// = 150*40;
    labelsCounts: { string?: number } = {};
    labelsHeights: {} = {};

    public selectedThingType: string = "None";
    public selectedThing: object = {
        'text': "Nothing selected yet"
    };
    diagram;

    hasData: boolean = false;
    selection: boolean = false;
    selectedSuccessors: string[] = [];

    constructor() {
    }

    ngOnInit(): void {
        cytoscape.use(euler);
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.nodes.length > 0) {
            this.hasData = true;
        } else {
            this.hasData = false;
        }
        this.diagram.remove('node');

        this.nodes.forEach(v => {
            this.labelsCounts[v['label']] = this.labelsCounts[v['label']] ? this.labelsCounts[v['label']] + 1 : 1;
        })
        var vals = Object.values(this.labelsCounts);
        this.totalHeight = (Math.max(...vals) + 2) * 150;
        this.labelsHeights = {};

        this.nodes.forEach(v => {
            if (!(v['label'] in this.labelsHeights)) {
                this.labelsHeights[v['label']] = 0;
            }
            this.diagram.add({
                group: 'nodes',
                data: {
                    'id': v['name'],
                    name: v['name'],
                    label: v['label'],
                    color: v['color'],
                    clearColor: v['clearColor']
                },
                position: {
                    x: this.labelsXs[v['label']], y: this.labelsHeights[v['label']]
                }
            });
            this.labelsHeights[v['label']] += this.totalHeight / this.labelsCounts[v['label']];
        });
        this.relations.forEach(r => {
            this.diagram.add({
                group: 'edges',
                data: {
                    'id': r['source'] + '-' + r['target'],
                    source: r['source'],
                    target: r['target'],
                    label: r['label']
                },
                classes: "curved"
            });
        });
        this.fitDiagram();
    }

    ngAfterViewInit(): void {
        this.initDiagram();
    }

    visible: boolean = false;

    showHelp() {
        this.visible = true;
    }

    initDiagram() {
        this.diagram = cytoscape({
            elements: [],
            layout: {
                name: 'euler'
            },
            style: [{
                "selector": "node",
                "style": {
                    "text-valign": "center",
                    "text-halign": "center",
                    'label': 'data(name)',
                    'background-color': 'data(color)',
                    'width': '100px',
                    'height': '100px',
                    'shape': "ellipse"
                }
            }, {
                "selector": "edge",
                "style": {
                    'width': 5,
                    'label': 'data(label)',
                    'text-valign': 'center',
                    'text-outline-width': 1,
                    'text-outline-color': '#000',
                    'color': '#FFF',
                    "target-arrow-shape": "triangle",
                    "target-arrow-color": "#999",
                    'line-color': '#999',
                    "edge-text-rotation": "autorotate"
                }
            }, {
                "selector": "edge.curved",
                "style": {
                    "curve-style": "unbundled-bezier",
                    "control-point-distances": 150,
                    "control-point-weights": 0.2
                }
            }],
            container: document.getElementById("myDiagramDiv")
        });

        this.diagram.on('mousemove', 'node', (event) => {
            var node = event.target;
            if (!this.selection || this.selectedSuccessors.includes(node.id())) {
                this.selectedThing = {
                    'name': node.data('name'),
                    'label': node.data('label')
                };
                this.selectedThingType = "Node";
            }
        });
        this.diagram.on('mousemove', 'edge', (event) => {
            var node = event.target;
            if (!this.selection || this.selectedSuccessors.includes(node.id())) {
                let sourceNode = this.diagram.filter(function (element, i) {
                    return element.isNode() && element.id() === node.data('source');
                });
                let targetNode = this.diagram.filter(function (element, i) {
                    return element.isNode() && element.id() === node.data('target');
                });
                this.selectedThing = {
                    'source': {
                        'name': sourceNode.data('name'),
                        'label': sourceNode.data('label')
                    },
                    'label': node.data('label'),
                    'target': {
                        'name': targetNode.data('name'),
                        'label': targetNode.data('label')
                    }
                }
                this.selectedThingType = "Edge";
                node.style({
                    "target-arrow-color": "#1B74C5",
                    "line-color": '#1B74C5',
                    "width": 15
                });
            }
        });
        this.diagram.on('mouseout', 'edge', (event) => {
            var node = event.target;
            this.selectedThingType = "AirEdge";
            if (!this.selection) {
                node.style({
                    "target-arrow-color": "#999",
                    "line-color": '#999',
                    "width": 5
                });
            } else if (this.selectedSuccessors.includes(node.id())) {
                node.style({
                    "width": 5
                });
            }
        });

        this.diagram.on('mouseout', 'node', (event) => {
            this.selectedThingType = "AirNode";
        });

        this.diagram.on('click', 'node', (event) => {
            this.selection = true;
            this.selectedSuccessors = [];
            var node = event.target;
            node.successors().forEach(s => {
                this.selectedSuccessors.push(s.id());
            })
            this.selectedSuccessors.push(node.id());
            this.selectedThing = {
                'name': node.data('name'),
                'label': node.data('label')
            };
            this.selectedThingType = "Node";
            this.resetDiagramDefaults();
            node.successors('edge').style({
                'line-color': '#1B74C5',
                "target-arrow-color": "#1B74C5"
            });
            node.style({
                'background-color': node.data('color'),
                'shape': 'diamond'
            });
            node.successors('node').forEach(s => {
                s.style({
                    'background-color': s.data('color'),
                    'shape': 'diamond'
                });
            });
            if (node.successors().length > 0) {
                this.diagram.fit(node.successors().union(node), 5);
            }
        });

        this.diagram.on('click', (event) => {
            var target = event.target;
            if (target === this.diagram) {
                this.selection = false;
                this.selectedSuccessors = [];
                this.resetDiagramDefaults();
            }
            ;
        });

        this.fitDiagram();
    }

    resetDiagramDefaults() {
        this.diagram.edges().style({
            "target-arrow-color": "#999",
            'line-color': '#999'
        });
        this.diagram.nodes().forEach(s => {
            s.style({
                'background-color': s.data('color'),
                'shape': 'ellipse'
            });
        });
    }

    fitDiagram() {
        this.diagram.fit(5);
    }

    exportDiagram() {
        const response = {
            nodes: this.nodes,
            relations: this.relations
        };
        console.log(response);
        const blob = new Blob([JSON.stringify(response)], { type: 'application/json' });
        const downloadURL = URL.createObjectURL(blob);
        console.log(downloadURL);
        const link = document.createElement('a');
        link.href = downloadURL;
        link.download = "graph.json";
        link.click();
    }
}
