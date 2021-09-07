import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DatasetDetailsDialogComponent} from './dataset-details-dialog.component';

describe('DatasetDetailsDialogComponent', () => {
  let component: DatasetDetailsDialogComponent;
  let fixture: ComponentFixture<DatasetDetailsDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DatasetDetailsDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DatasetDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
