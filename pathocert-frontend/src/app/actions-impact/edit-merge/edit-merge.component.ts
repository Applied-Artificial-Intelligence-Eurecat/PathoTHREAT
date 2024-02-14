import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ActionsImpactService} from 'src/app/services/actions-impact.service';


@Component({
    selector: 'app-edit-merge',
    templateUrl: './edit-merge.component.html',
    styleUrls: ['./edit-merge.component.scss']
})
export class EditMergeComponent implements OnInit {
    editionText = '';

    constructor(
        private router: Router,
        private actionsImpactService: ActionsImpactService) {
    }

    ngOnInit(): void {
        this.actionsImpactService.controlHtml.subscribe(
            s => {
                if (this.actionsImpactService.flagAskControl) {
                    this.editionText = s;
                }
            }
        );
        this.actionsImpactService.impactHtml.subscribe(
            s => {
                if (this.actionsImpactService.flagAskImpact) {
                    this.editionText = s;
                }
            });
    }

    getEitherImpactOrControl(): string {
        if (this.actionsImpactService.flagAskImpact) {
            return 'Impact';
        }
        if (this.actionsImpactService.flagAskControl) {
            return 'Control';
        }
        return '';
    }

    save() {
        if (this.actionsImpactService.flagAskControl) {
            // this.actionsImpactService.adaptEventData.action = this.editionText;
            this.actionsImpactService.editedControlHtml(this.editionText);
        }
        if (this.actionsImpactService.flagAskImpact) {
            this.actionsImpactService.editedImpactHtml(this.editionText);
            // this.actionsImpactService.adaptEventData.risk = this.editionText;
        }
        this.resetFlagsImpactControl();
        this.router.navigate([`detail/${this.actionsImpactService.emergencyId}/actions/adapt`]);
    }

    cancel() {
        this.resetFlagsImpactControl();
        this.router.navigate([`detail/${this.actionsImpactService.emergencyId}/actions/adapt`]);
    }

    resetFlagsImpactControl() {
        this.actionsImpactService.flagAskControl = false;
        this.actionsImpactService.flagAskImpact = false;
    }
}
