import {ChangeDetectorRef, Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {HttpClient} from "@angular/common/http";
import {AlertService} from "../../../alert/alert.service";
import {FormGroup} from "@angular/forms";
import {SecurityService} from "../../../security/security.service";
import {MatChipInputEvent} from "@angular/material/chips";

@Component({
  selector: 'editable-list-dialog',
  templateUrl: './editable-list-dialog.component.html',
  styleUrls: ['./editable-list-dialog.component.scss']
})
export class EditableListDialogComponent implements OnInit {


  displayedColumns: string[];
  //displayedColumns: string[] = ['name', 'occupation', 'age', 'edit'];
  dataSource:any;
  dataSchema:any;

  selected:any=null;
  selectedRowIndex:number;

  title: String = "List editor";

  constructor(public securityService: SecurityService,
              public dialog: MatDialog,
              protected http: HttpClient,
              public dialogRef: MatDialogRef<EditableListDialogComponent>,
              private alertService: AlertService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private changeDetector: ChangeDetectorRef) {


    let ipos:number=0;
    this.dataSource =[...data.dataSource]
    this.title = data.title?data.title: "List editor";
    this.displayedColumns = data.displayedColumns;
    this.dataSchema = data.dataSchema;
  }


  ngOnInit() {


    // detect changes for updated values in mat-selection-list (check change detection operations)
    // else the following error is thrown :xpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:
    // 'aria-selected: false'. Current value: 'aria-selected: true'
    //
    this.changeDetector.detectChanges()
  }

  public getDataSource(){
    return this.dataSource;
  }

  selectRow(row) {
    this.selectedRowIndex=row.position;
    if (this.selected===row) {
      return;
    }
    if (this.selected !=null) {
      //deselect

    }

    this.selected = row;
    this.selectedRowIndex = row.position;

  }

  get removeButtonEnabled(): boolean {
    return this.selected!==null;
  }

  isRowSelected(row:any): boolean {
    return this.selected===row;
  }

  removeSelectedItem() {
    let index = this.dataSource.indexOf(this.selected);
    if (index > -1) {
      this.dataSource.splice(index, 1);
      this.dataSource = [...this.dataSource];
    }
  }

  addNewItem() {
    var newItem = {};

    for (const [key, value] of Object.entries(this.dataSchema)) {
      if (key === 'edit') {
        return;
      }
      switch (value) {
        case 'number':
          newItem[key] = "1";
          break;
        case 'checkbox':
          newItem[key] = "true";
          break;
        case 'text':
          newItem[key] = "New '"+key+"'";
          break;
        case 'url':
          newItem[key] = "http://example/test";
          break;
        case 'time':
          newItem[key] = "12:00";
          break;
        case 'date':
          newItem[key] = "1/1/2021";
          break;
        default:
          newItem[key] = "";

      }

    }
    this.dataSource.push(newItem);
    this.dataSource = [...this.dataSource];
  }

  submitForm() {
    // validate values
    this.dialogRef.close(true);
  }

  checkValidity(g: FormGroup) {
    Object.keys(g.controls).forEach(key => {
      if (g.contains(key) && g.get(key) !== null) {
        g.get(key).markAsDirty();
      }

    });
    Object.keys(g.controls).forEach(key => {
      if (g.contains(key) && g.get(key) !== null) {
        g.get(key).markAsTouched();
      }
    });
    //!!! updateValueAndValidity - else some filed did no update current / on blur never happened
    Object.keys(g.controls).forEach(key => {
      if (g.contains(key) && g.get(key) !== null) {
        g.get(key).updateValueAndValidity();
      }
    });
  }


  compareTableItemById(item1, item2): boolean {
    return item1.id === item2.id;
  }


  isEqual(val1, val2): boolean {
    return (this.isEmpty(val1) && this.isEmpty(val2)
      || val1 === val2);
  }

  isEmpty(str): boolean {
    return (!str || 0 === str.length);
  }


  addListItem(list: string[], event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    // Add our fruit
    if (value) {
      list.push(value);
    }

    // Clear the input value
    event.chipInput!.clear();
  }

  removeListItem(list: string[], item: string): void {
    const index = list.indexOf(item);

    if (index >= 0) {
      list.splice(index, 1);
    }
  }
}
