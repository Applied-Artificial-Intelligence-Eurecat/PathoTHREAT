import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportInformationComponent } from './report-information.component';

describe('ReportInformationComponent', () => {
  let component: ReportInformationComponent;
  let fixture: ComponentFixture<ReportInformationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReportInformationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReportInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
