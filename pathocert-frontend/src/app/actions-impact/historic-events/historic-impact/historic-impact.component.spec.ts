import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricImpactComponent } from './historic-impact.component';

describe('HistoricImpactComponent', () => {
  let component: HistoricImpactComponent;
  let fixture: ComponentFixture<HistoricImpactComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HistoricImpactComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HistoricImpactComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
