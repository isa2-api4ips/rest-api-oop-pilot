import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {DsdRequestDetailsDialogComponent} from "./dsd-request-details-dialog.component";


describe('ServiceGroupDetailsDialogComponent', () => {
  let component: DsdRequestDetailsDialogComponent;
  let fixture: ComponentFixture<DsdRequestDetailsDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DsdRequestDetailsDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DsdRequestDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
