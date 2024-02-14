import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FieldInnerHtmlComponent } from './field-inner-html.component';

describe('HistoricFieldInnerHtmlComponent', () => {
  let component: FieldInnerHtmlComponent;
  let fixture: ComponentFixture<FieldInnerHtmlComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FieldInnerHtmlComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FieldInnerHtmlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
