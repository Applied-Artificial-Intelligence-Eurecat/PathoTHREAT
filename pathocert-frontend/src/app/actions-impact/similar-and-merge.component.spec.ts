import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SimilarAndMergeComponent } from './similar-and-merge.component';

describe('ActionsImpactComponent', () => {
  let component: SimilarAndMergeComponent;
  let fixture: ComponentFixture<SimilarAndMergeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SimilarAndMergeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SimilarAndMergeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
