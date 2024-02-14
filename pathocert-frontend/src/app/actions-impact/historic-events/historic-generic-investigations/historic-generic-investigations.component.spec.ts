import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricGenericInvestigationsComponent } from './historic-generic-investigations.component';

describe('HistoricGenericInvestigationsComponent', () => {
  let component: HistoricGenericInvestigationsComponent;
  let fixture: ComponentFixture<HistoricGenericInvestigationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HistoricGenericInvestigationsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HistoricGenericInvestigationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
