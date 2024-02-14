import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MergeAreaComponent } from './merge-area.component';

describe('MergeAreaComponent', () => {
  let component: MergeAreaComponent;
  let fixture: ComponentFixture<MergeAreaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MergeAreaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MergeAreaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
