import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdditionalFeedbackComponent } from './additional-feedback.component';

describe('AdditionalFeedbackComponent', () => {
  let component: AdditionalFeedbackComponent;
  let fixture: ComponentFixture<AdditionalFeedbackComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdditionalFeedbackComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdditionalFeedbackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
