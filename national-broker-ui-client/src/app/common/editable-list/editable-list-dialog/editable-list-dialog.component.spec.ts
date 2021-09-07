import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {EditableListDialogComponent} from "./editable-list-dialog.component";


describe('DatasetDetailsDialogComponent', () => {
  let component: EditableListDialogComponent;
  let fixture: ComponentFixture<EditableListDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditableListDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditableListDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
