import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmergenciesCompleteComponent } from './emergencies-complete.component';

describe('EmergenciesCompleteComponent', () => {
  let component: EmergenciesCompleteComponent;
  let fixture: ComponentFixture<EmergenciesCompleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmergenciesCompleteComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmergenciesCompleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
