import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FieldInlineComponent } from './field-inline.component';

describe('HistoricFieldComponent', () => {
  let component: FieldInlineComponent;
  let fixture: ComponentFixture<FieldInlineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FieldInlineComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FieldInlineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
