import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {environment} from 'src/environments/environment';
import {Observable, throwError} from 'rxjs';
import {map, tap} from 'rxjs/operators';
import {LoginResult} from 'src/app/models/loginResult';
import {Emergency} from 'src/app/models/emergency';
import {EventMerged, EventSimilarity} from 'src/app/models/event';
import {User} from 'src/app/models/user';
import {Organization} from 'src/app/models/organization';
import {MultiselectValues} from '../models/multiselectValues';
import {GraphItem} from '../models/graphitem';
import {KeycloakService} from 'keycloak-angular';

@Injectable()
export class ApiService {
    token: string;

    constructor(
        public httpClient: HttpClient,
        private keycloak: KeycloakService
    ) {
        this.keycloak.getToken().then(v => {
            this.token = v;
        });
    }

    async updateToken(): Promise<void> {
        await this.keycloak.getToken().then(v => {
            this.token = v;
        });
    }

    login(username: string, password: string): Observable<LoginResult> {
        const body = JSON.stringify({ username, password });
        const headers = new HttpHeaders().append(
            'Content-Type',
            'application/json'
        );
        return this.httpClient
            .post(this.url('/api/authenticate'), body, { headers })
            .pipe(map((response: any) => response));
    }
    async getEmergencyListFilterByArchived(params: any): Promise<Observable<any>> {
        return this.httpClient.get<any>(this.url('/api/emergencies/my'), {
            headers: await this.retrieveHttpOptions(),
            params,
        });
    }

    async getEmergencyById(emergencyId: number): Promise<Observable<Emergency>> {
        return this.httpClient.get<Emergency>(
            this.url('/api/emergencies/' + emergencyId),
            {headers: await this.retrieveHttpOptions()}
        );
    }

    async createEmergency(emergency: Emergency) {
        return this.httpClient.post<Emergency>(
            this.url('/api/emergencies'),
            emergency,
            {headers: await this.retrieveHttpOptions()}
        );
    }

    async updateEmergency(emergency: Emergency) {
        const url = this.url('/api/emergencies/' + emergency.id);
        return this.httpClient.put<Emergency>(
            url,
            emergency,
            {headers: await this.retrieveHttpOptions()}
        );
    }

    async retrieveMergeEvent(events: Array<string>): Promise<Observable<EventMerged>> {
        return this.httpClient.post<EventMerged>(this.url('/api/assessment/merge-documents'), events, {
            headers: await this.retrieveHttpOptions(),
        }).pipe(
            tap((o) => {
            })
        );
    }

    async searchByTerms(term1: string, term2: string): Promise<Observable<string>> {
        const params = new HttpParams({
            fromObject: {
                subject: term1,
                desiredOutput: term2
            }
        });
        const uri = `/api/expert/search-terms`;
        return this.httpClient.get<string>(
            this.url(
                uri
            ),
            {params, headers: await this.retrieveHttpOptions()}
        );
    }

    async detailValues(): Promise<Observable<string[]>> {
        const uri = `/api/expert/values`;

        interface Values {
            _embedded: {
                strings: string[]
            };
        }

        return this.httpClient.get<Values>(
            this.url(
                uri
            ),
            {headers: await this.retrieveHttpOptions()}
        ).pipe(
            map(r => r._embedded.strings)
        );
    }

    async detailLabels(): Promise<Observable<string[]>> {
        const uri = `/api/expert/labels`;

        interface Labels {
            _embedded: {
                strings: string[]
            };
        }

        return this.httpClient.get<Labels>(
            this.url(
                uri
            ),
            {headers: await this.retrieveHttpOptions()}
        ).pipe(
            map(
                r => r._embedded.strings
            )
        );
    }

    async detailItems(): Promise<Observable<GraphItem[]>> {
        const uri = `/api/expert/graph-data`;

        return this.httpClient.get<GraphItem[]>(
            this.url(
                uri
            ),
            {headers: await this.retrieveHttpOptions()}
        );
    }

    async detailItemsSearch(term1, term2): Promise<Observable<GraphItem[]>> {
        const uri = `/api/expert/graph-data-search`;

        const params = new HttpParams({
            fromObject: {
                subject: term1,
                desiredOutput: term2
            }
        });

        return this.httpClient.get<GraphItem[]>(
            this.url(
                uri
            ),
            {params, headers: await this.retrieveHttpOptions()}
        );
    }

    async specifyDropdownValues(label: string): Promise<Observable<string[]>> {
        const uri = `/api/expert/values/dropdown/${label}`;

        interface Labels {
            _embedded: {
                strings: string[]
            };
        }

        return this.httpClient.get<Labels>(
            this.url(
                uri
            ),
            {headers: await this.retrieveHttpOptions()}
        ).pipe(
            map(
                r => r._embedded.strings
            ),
            tap(r => {
            })
        );
        ;
    }

    async specifyValues(label: string): Promise<Observable<string[]>> {
        const uri = `/api/expert/values/${label}`;

        interface Labels {
            _embedded: {
                strings: string[]
            };
        }


        return this.httpClient.get<Labels>(
            this.url(
                uri
            ),
            {headers: await this.retrieveHttpOptions()}
        ).pipe(
            map(
                r => r._embedded.strings
            ),
            tap(r => {
            })
        );
    }

    async specifyLabels(value: string): Promise<Observable<string[]>> {
        const uri = `/api/expert/labels/${value}`;

        interface Labels {
            _embedded: {
                strings: string[]
            };
        }


        return this.httpClient.get<Labels>(
            this.url(
                uri
            ),
            {headers: await this.retrieveHttpOptions()}
        ).pipe(
            map(
                r => r._embedded.strings
            ),
            tap(r => {
            })
        );
    }

    async retrieveDocuments(emergencyId: number): Promise<Observable<EventSimilarity[]>> {
        return this.httpClient.get(
            this.url(
                `/api/assessment/${emergencyId}/close-assessments`
            ),
            {headers: await this.retrieveHttpOptions()},
        ).pipe(
            tap(n => {
            }),
            map((n: { _embedded: { documentSimilarities: EventSimilarity[] } }, _) => {
                    return n._embedded.documentSimilarities;
                }
            ),
        );
    }

    async multiselectValues(): Promise<Observable<MultiselectValues>> {
        const uri = `/api/emergencies/selectable-values`;

        return this.httpClient.get<MultiselectValues>(
            this.url(
                uri
            ),
            {headers: await this.retrieveHttpOptions()}
        );

    }

    private url(suffix: string) {
        return environment.apiUrl + suffix;
    }

    private retrieveHttpOptions() {
        return this._retrieveHttpOptions(this.token);

    }

    private async _retrieveHttpOptions(token: string) {
        await this.updateToken();
        return new HttpHeaders({timeout: '1200000'})
            .append('Content-Type', 'application/json;charset=UTF-8')
            .append('Authorization', 'Bearer ' + token);
    }

    sendToPathoWARE(emergencyJSON): Observable<string> {
        //const uri = "/datacon/dataconnector/api/v1/pathothreat/";
        const uri = this.url(
            "/api/emergencies/send-to-ware"
        )
        var myheaders = new HttpHeaders()
            .append('Authorization', 'Bearer ' + this.token)
            .append("x-token", this.token)
            .append("scenario", emergencyJSON['scenario'])
            .append("Content-Type", "application/json");
        return this.httpClient.post<string>(
            uri,
            emergencyJSON,
            {
                headers: myheaders
            }
        );
    }

    exportAssessment(emergencyObject: {}): Observable<Blob> {
        const uri = this.url(
            "/api/emergencies/export-to-file"
        )
        return this.httpClient.post(uri, emergencyObject, { responseType: 'blob' });
    }
}

