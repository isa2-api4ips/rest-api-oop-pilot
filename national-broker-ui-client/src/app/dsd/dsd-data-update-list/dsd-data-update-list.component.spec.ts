import {ComponentFixture, TestBed} from '@angular/core/testing';
import {DsdDataUpdateListComponent} from "./dsd-data-update-list.component";


describe('OrganizationUpdateListComponent', () => {
  let component: DsdDataUpdateListComponent;
  let fixture: ComponentFixture<DsdDataUpdateListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DsdDataUpdateListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DsdDataUpdateListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
