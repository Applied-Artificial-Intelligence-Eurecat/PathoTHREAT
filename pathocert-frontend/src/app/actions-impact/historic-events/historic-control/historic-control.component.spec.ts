import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricControlComponent } from './historic-control.component';

describe('HistoricControlComponent', () => {
  let component: HistoricControlComponent;
  let fixture: ComponentFixture<HistoricControlComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HistoricControlComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HistoricControlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
