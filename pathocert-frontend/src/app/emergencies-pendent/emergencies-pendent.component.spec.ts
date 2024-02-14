import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmergenciesPendentComponent } from './emergencies-pendent.component';

describe('EmergenciesPendentComponent', () => {
  let component: EmergenciesPendentComponent;
  let fixture: ComponentFixture<EmergenciesPendentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmergenciesPendentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmergenciesPendentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
