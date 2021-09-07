import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {SignatureDetailsDialogComponent} from "./signature-details-dialog.component";


describe('ServiceGroupDetailsDialogComponent', () => {
  let component: SignatureDetailsDialogComponent;
  let fixture: ComponentFixture<SignatureDetailsDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SignatureDetailsDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SignatureDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
