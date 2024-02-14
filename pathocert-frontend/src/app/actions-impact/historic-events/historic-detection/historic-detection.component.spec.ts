import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricDetectionComponent } from './historic-detection.component';

describe('HistoricDetectionComponent', () => {
  let component: HistoricDetectionComponent;
  let fixture: ComponentFixture<HistoricDetectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HistoricDetectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HistoricDetectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
