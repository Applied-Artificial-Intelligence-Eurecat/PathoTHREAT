import { Component, OnInit } from '@angular/core';
import { ActionsImpactService } from '../services/actions-impact.service';

type SearchStatus = 'Result' | 'None' | 'NotYet';

@Component({
  selector: 'app-expert-search',
  templateUrl: './expert-search.component.html',
  styleUrls: ['./expert-search.component.scss']
})
export class ExpertSearchComponent implements OnInit {
  term1 = '';
  term2 = '';
  searchedResult = '';
  searchStatus: SearchStatus = 'NotYet';
  labels: { name: string }[] = [];
  values: { name: string }[] = [];

  subjectDisabled: boolean = false;
  outputDisabled: boolean = false;

  constructor(private actionsImpactService: ActionsImpactService) { }

  async ngOnInit(): Promise<void> {
      await this.updateValuesAndLabels();
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
      let sub = await this.actionsImpactService.specificLabels(this.term1);
      sub.subscribe(
          ls => {
              this.labels = ls.map(l => ({name: l}));
              this.outputDisabled = false;
          }
      );
  }

  private async updateSubjectValues() {
      this.subjectDisabled = true;
      let sub = await this.actionsImpactService.specificValues(this.term2);
      sub.subscribe(
          ls => {
              this.values = ls.map(l => ({name: l}));
              this.subjectDisabled = false;
          }
      );
  }

  async saveTermAndGetLabels(event: any) {
      this.term1 = event.value === null ? '' : event.value;
      if (this.term1 !== '') {
          if (this.term2 === '') {
              await this.updateOutputLabels();
          }
      } else {
          if (this.term2 === '') {
              await this.updateValuesAndLabels();
          } else {
              await this.updateSubjectValues();
          }
      }
  }

  async saveTermAndGetValues(event: any) {
      this.term2 = event.value === null ? '' : event.value;
      if (this.term2 !== '') {
          if (this.term1 === '') {
              await this.updateSubjectValues();
          }
      } else {
          if (this.term1 === '') {
              await this.updateValuesAndLabels();
          } else {
              await this.updateOutputLabels();
          }
      }
  }

  async search() {
      this.searchStatus = 'NotYet';
      let sub = await this.actionsImpactService.searchByTerms(this.term1, this.term2);
      sub.subscribe(
          response => {
              if (response === '') {
                  this.searchStatus = 'None';
              } else {
                  this.searchStatus = 'Result';
                  this.searchedResult = response;
              }
          },
          _ => {
              this.searchStatus = 'None';
          });
  }
}
