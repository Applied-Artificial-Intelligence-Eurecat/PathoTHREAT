import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmergencyDetailComponent } from './emergency-detail.component';

describe('EmergencyDetailComponent', () => {
  let component: EmergencyDetailComponent;
  let fixture: ComponentFixture<EmergencyDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmergencyDetailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmergencyDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
