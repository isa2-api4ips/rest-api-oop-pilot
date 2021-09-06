import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {OrganizationDetailsDialogComponent} from './organization-details-dialog.component';

describe('ServiceGroupDetailsDialogComponent', () => {
  let component: OrganizationDetailsDialogComponent;
  let fixture: ComponentFixture<OrganizationDetailsDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OrganizationDetailsDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OrganizationDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
