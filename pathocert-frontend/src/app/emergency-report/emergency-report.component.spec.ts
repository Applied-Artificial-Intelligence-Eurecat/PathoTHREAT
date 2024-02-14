import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmergencyReportComponent } from './emergency-report.component';

describe('EmergencyReportComponent', () => {
  let component: EmergencyReportComponent;
  let fixture: ComponentFixture<EmergencyReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmergencyReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmergencyReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
