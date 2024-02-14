import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricEventsComponent } from './historic-events.component';

describe('HistoricEventsComponent', () => {
  let component: HistoricEventsComponent;
  let fixture: ComponentFixture<HistoricEventsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HistoricEventsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HistoricEventsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
