import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MergeEventsComponent } from './merge-events.component';

describe('AdaptEventsComponent', () => {
  let component: MergeEventsComponent;
  let fixture: ComponentFixture<MergeEventsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MergeEventsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MergeEventsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
