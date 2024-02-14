import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceAskComponent } from './service-ask.component';

describe('ServiceAskComponent', () => {
  let component: ServiceAskComponent;
  let fixture: ComponentFixture<ServiceAskComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ServiceAskComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceAskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
