import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EditMergeComponent } from './edit-merge.component';


describe('AdaptAskComponent', () => {
  let component: EditMergeComponent;
  let fixture: ComponentFixture<EditMergeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditMergeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditMergeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
