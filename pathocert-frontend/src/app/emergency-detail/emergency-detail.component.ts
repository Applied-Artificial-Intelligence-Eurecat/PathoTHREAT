import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MessageService} from 'primeng/api';
import {Emergency} from 'src/app/models/emergency';
import {EmergencyService} from 'src/app/services/emergency.service';
import {BaseComponent} from 'src/app/shared/base.component';
import {DropDownValuesService} from 'src/app/services/dropdown-values.service';
import {AuthService} from 'src/app/services/auth.service';
import {AppBreadcrumbService} from 'src/app/app.breadcrumb.service';
import {UserRole} from '../models/user';
import {JSONMap} from '../models/JSONMap';
import {combineLatest} from 'rxjs';

@Component({
    selector: 'app-emergency-detail',
    templateUrl: './emergency-detail.component.html',
    styleUrls: ['./emergency-detail.component.scss']
})
export class EmergencyDetailComponent extends BaseComponent implements OnInit {
    emergency: Emergency;
    userRole = UserRole;
    canArchive: boolean

    constructor(
        private activatedRoute: ActivatedRoute,
        private emergencyService: EmergencyService,
        private rout: Router,
        private breadcrumbService: AppBreadcrumbService,
        messageService: MessageService,
        dropDownValuesService: DropDownValuesService,
        router: Router,
        authService: AuthService) {
        super(messageService, dropDownValuesService, router, authService);
        this.emergencyService.detailEmergency.subscribe(s => {
        });
        this.emergencyService.detailEmergency.subscribe(s => {
            this.emergency = s;
            this.canArchive = s.archived === false;
        });
        this.breadcrumbService.setItems([
            {label: 'Emergency detail'}
        ]);
    }

    ngOnInit(): void {
        if (!this.emergencyService.shouldUpdate.getValue()) {
            this.emergencyService.shouldUpdate.next(true);
            return;
        }
        const id = this.activatedRoute.snapshot.paramMap.get('id');
        if (id) {
            this.findEmergencyById(Number(id));
        }
    }

    async findEmergencyById(id: number) {
        let sub = await this.emergencyService.retrieveEmergencyById(id);
        super.runSubscription(sub.subscribe(
            response => {
                this.emergency = response;
            }, error => super.manageError('login error', error)
        ));
    }

    provideControlImpact() {
        this.rout.navigate([`detail/${this.emergency.id}/actions`]);
    }

    async archiveEmergency() {
        this.emergency.archived = true;
        this.sendEmergencyToPathoWARE(this.emergency);
        let sub = await this.emergencyService.updateEmergency(this.emergency);
        super.runSubscription(sub.subscribe(eme => {
            super.displaySuccessMessage('Emergency archived');
            this.emergencyService.shouldUpdate.next(false);
            this.emergencyService.detailEmergency.next(eme);
            this.rout.navigate([`detail/${this.emergency.id}`]);
        }, error => super.manageError('Error archiving emergency', error)));
    }

    downloadEmergency() {
        super.runSubscription(this.emergencyService.exportAssessment(this.createWAREJson(this.emergency), this.emergency.emergencyName + ".json")
            .subscribe(
                res => {
                    super.displaySuccessMessage('Emergency downloaded successfully');
                },
                error => {
                    super.manageError('Error exporting emergency', error)
                }
            )
        );
    }

    close() {
        this.rout.navigate(['']);
    }

    sendEmergencyToPathoWARE(emergency: Emergency) {
        // Old JSON parse
        // const emergencyJSON: string = this.parseEmergency(emergency);
        const emergencyJSON = this.createWAREJson(emergency);
        super.runSubscription(this.emergencyService.uploadToPathoWARE(emergencyJSON)
            .subscribe(
                res => {
                    super.displaySuccessMessage('Emergency sent to PathoWARE successfully');
                },
                error => {
                    super.manageError('Error sending emergency to PathoWARE', error)
                }
            )
        );
    }

    createWAREJson(emergency: Emergency) {
        var dateAsDate = new Date(emergency.reportDate);
        var dateString = dateAsString(dateAsDate);

        var description = emergency.emergencyDescription.replace("\n", " ");
        while (description.includes("\n")) {
            description = description.replace("\n", " ");
        }

        var jsonObj = {};
        jsonObj['scenario'] = emergency.scenario;

        jsonObj['id'] = emergency.id.toString();
        jsonObj['name'] = emergency.emergencyName;
        jsonObj['alternateName'] = emergency.emergencyName;
        jsonObj['type'] = emergency.emergencyTypeClass;
        jsonObj['data'] = dateString;
        jsonObj['dateCreated'] = dateString;
        jsonObj['dateModified'] = dateString;
        jsonObj['category'] = 'naturalDisaster';
        jsonObj['subCategory'] = 'waterPollution';
        jsonObj['validTo'] = null;

        var assessmentDescription = (emergency.impact + " " + emergency.control).replace("\n", " ");
        while (assessmentDescription.includes("\n")) {
            assessmentDescription = assessmentDescription.replace("\n", " ");
        }

        jsonObj['description'] = assessmentDescription;
        jsonObj['locationLat'] = emergency.affectedAreaLat.toString();
        jsonObj['locationLon'] = emergency.affectedAreaLon.toString();
        jsonObj['dateIssued'] = dateString;
        jsonObj['validFrom'] = dateString;
        jsonObj['source'] = "Human reported emergency";
        jsonObj['alertSource'] = "PathoTHREAT";
        jsonObj['severity'] = "informational";
        jsonObj['owner'] = emergency.nameReporter + " " + emergency.ocupationDescription;
        return jsonObj;
    }

    parseEmergency(emergency: Emergency): string {
        var emJSON: JSONMap = new JSONMap(
            false,
            null,
            new Map<string, JSONMap[]>()
        );
        var generalDescription: JSONMap = new JSONMap(
            false,
            null,
            new Map<string, JSONMap[]>()
        );
        generalDescription.set("Date Of Report",
            [new JSONMap(
                true,
                emergency.reportDate.toString(),
                null
            )]
        );
        generalDescription.set("Type Of Event",
            [new JSONMap(
                true,
                emergency.emergencyTypeClass.toString(),
                null
            )]
        );
        generalDescription.set("Emergency Description",
            [new JSONMap(
                true,
                emergency.emergencyDescription,
                null
            )]
        );
        generalDescription.set("Infrastructure",
            [new JSONMap(
                true,
                "",
                null
            )]
        );
        emJSON.set("General Description",
            [generalDescription]
        );
        var areaInformation: JSONMap = new JSONMap(
            false,
            null,
            new Map<string, JSONMap[]>()
        );
        areaInformation.set("Type",
            [new JSONMap(
                true, null,
                //emergency.affectedAreaType,
                null
            )]
        );
        areaInformation.set("Name",
            [new JSONMap(
                true, null,
                //emergency.nameAreaAffected,
                null
            )]
        );
        emJSON.set("Area Information",
            [areaInformation]
        );
        var impactAssessment: JSONMap = new JSONMap(
            false,
            null,
            new Map<string, JSONMap[]>()
        );
        var impactClean: string = emergency.impact;
        if (impactClean.indexOf("<p>Number of people exposed: ") !== -1) {
            var exposed: string = impactClean.slice(
                impactClean.indexOf("<p>Number of people exposed: ") + "<p>Number of people exposed: ".length,
                impactClean.indexOf("</p>")
            )
            var impactClean: string = impactClean.slice(
                impactClean.indexOf("</p>") + "</p>".length
            )
            impactAssessment.set("Number of people exposed",
                [new JSONMap(
                    true,
                    exposed,
                    null
                )]
            );
        } else {
            impactAssessment.set("Number of people exposed",
                [new JSONMap(
                    true,
                    "",
                    null
                )]
            );
        }
        if (impactClean.indexOf("<p>Number of people hospitalized: ") !== -1) {
            var hospitalized: string = impactClean.slice(
                impactClean.indexOf("<p>Number of people hospitalized: ") + "<p>Number of people hospitalized: ".length,
                impactClean.indexOf("</p>")
            )
            var impactClean: string = impactClean.slice(
                impactClean.indexOf("</p>") + "</p>".length
            )
            impactAssessment.set("Number of people hospitalized",
                [new JSONMap(
                    true,
                    hospitalized,
                    null
                )]
            );
        } else {
            impactAssessment.set("Number of people hospitalized",
                [new JSONMap(
                    true,
                    "",
                    null
                )]
            );
        }
        if (impactClean.indexOf("<p>Number of people dead: ") !== -1) {
            var dead: string = impactClean.slice(
                impactClean.indexOf("<p>Number of people dead: ") + "<p>Number of people dead: ".length,
                impactClean.indexOf("</p>")
            )
            var impactClean: string = impactClean.slice(
                impactClean.indexOf("</p>") + "</p>".length
            )
            impactAssessment.set("Number of people deceased",
                [new JSONMap(
                    true,
                    dead,
                    null
                )]
            );
        } else {
            impactAssessment.set("Number of people deceased",
                [new JSONMap(
                    true,
                    "",
                    null
                )]
            );
        }
        var pathogens = this.parseList(impactClean, "<p>Expected Associated Pathogens: </p>");
        if (pathogens.length !== 0) {
            impactClean = this.cutCleanString(impactClean);
            var pathoMap = new JSONMap(
                true,
                pathogens,
                null
            );
            pathoMap.isList = true;
            impactAssessment.set("Associated Pathogens",
                [pathoMap]
            );
        } else {
            var pathoMap = new JSONMap(
                true,
                "",
                null
            );
            pathoMap.isList = true;
            impactAssessment.set("Associated Pathogens",
                [pathoMap]
            );
        }
        var symptoms = this.parseList(impactClean, "<p>Expected Associated Symptoms: </p>");
        if (symptoms.length !== 0) {
            impactClean = this.cutCleanString(impactClean);
            var symptoMap = new JSONMap(
                true,
                symptoms,
                null
            );
            symptoMap.isList = true;
            impactAssessment.set("Associated Symptoms",
                [symptoMap]
            );
        } else {
            var symptoMap = new JSONMap(
                true,
                "",
                null
            );
            symptoMap.isList = true;
            impactAssessment.set("Associated Symptoms",
                [symptoMap]
            );
        }
        emJSON.set("Impact Assessment",
            [impactAssessment]
        )
        var controlActions: JSONMap = new JSONMap(
            false,
            null,
            new Map<string, JSONMap[]>()
        );
        var controlClean: string = emergency.control;
        var objectives = [
            "<p>Potential Event Detection: </p>",
            "<p>Causes: </p>",
            "<p>Sources: </p>",
            "<p>Mitigation Actions: </p>",
            "<p>Monitoring Actions: </p>",
            "<p>Restoration: </p>",
            "<p>Prevention: </p>",
        ];
        var keys = [
            "Detection",
            "Cause",
            "Source",
            "Mitigation",
            "Monitoring",
            "Restoration",
            "Prevention"
        ];
        for (let i = 0; i < objectives.length; i++) {
            var results = this.parseList(controlClean, objectives[i]);
            if (results.length !== 0) {
                controlClean = this.cutCleanString(controlClean);
                var actionMap = new JSONMap(
                    true,
                    results,
                    null
                );
                actionMap.isList = true;
                controlActions.set(keys[i],
                    [actionMap]
                );
            } else {
                var actionMap = new JSONMap(
                    true,
                    "",
                    null
                );
                actionMap.isList = true;
                controlActions.set(keys[i],
                    [actionMap]
                );
            }
        }
        emJSON.set("Control Actions",
            [controlActions]
        )
        return emJSON.toJSONString();
    }

    parseList(cleanString: string, objective: string): string[] {
        if (cleanString.indexOf(objective) !== -1) {
            var pathogensClean = cleanString.slice(
                cleanString.indexOf(objective) + objective.length,
                cleanString.indexOf("</ul>")
            );
            pathogensClean = pathogensClean.slice(
                pathogensClean.indexOf("<ul>") + "<ul>".length
            );
            var pathogens: string[];
            pathogens = [];
            while (pathogensClean.indexOf("<li>") !== -1) {
                var pathogen = pathogensClean.slice(
                    pathogensClean.indexOf("<li>") + "<li>".length,
                    pathogensClean.indexOf("</li>")
                );
                pathogens.push(pathogen);
                pathogensClean = pathogensClean.slice(
                    pathogensClean.indexOf("</li>") + "</li>".length
                );
            }
            return pathogens;
        } else {
            return [];
        }
    }

    cutCleanString(cleanString: string) {
        return cleanString.slice(
            cleanString.indexOf("</ul>") + "</ul>".length
        )
    }
}

function dateAsString(dateAsDate) {
    var dateString = "";
    dateString += dateAsDate.getUTCFullYear()
    dateString += "-";
    dateString += (dateAsDate.getUTCMonth() + 1).toString().padStart(2, '0');
    dateString += "-";
    dateString += dateAsDate.getUTCDate().toString().padStart(2, '0');
    dateString += "T";
    dateString += dateAsDate.getUTCHours().toString().padStart(2, '0');
    dateString += ":";
    dateString += dateAsDate.getUTCMinutes().toString().padStart(2, '0');
    dateString += ":00Z";
    return dateString;
}

