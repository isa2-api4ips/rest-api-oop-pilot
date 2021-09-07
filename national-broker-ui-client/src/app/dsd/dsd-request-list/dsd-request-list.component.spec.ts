import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DsdRequestListComponent} from './dsd-request-list.component';

describe('OrganizationUpdateListComponent', () => {
  let component: DsdRequestListComponent;
  let fixture: ComponentFixture<DsdRequestListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DsdRequestListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DsdRequestListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
