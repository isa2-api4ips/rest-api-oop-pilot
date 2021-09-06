import {Component, Input, TemplateRef} from '@angular/core';
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";


@Component({
  selector: 'details-list',
  templateUrl: './details-list.component.html',
  styleUrls: ['./details-list.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: DetailsListComponent,
      multi: true,
    },
  ],
})
export class DetailsListComponent implements ControlValueAccessor {

  @Input() dataTemplate: TemplateRef<any>;

  @Input() title: string;
  @Input() fieldStyle: string ="width:300px";
  @Input() disabled = false;

  @Input()  displayedColumns: string[] = [];
  // default string schema
  @Input() dataSchema: any = {"value": "text"};
  @Input() set dataSource(value: any[]) {
    console.log("set Datasource1 :" +  value);
    this._dataSource = value;
    // init internalData values
    this.initInternalData();
  }
  get dataSource(): any[] {
    return this._dataSource;
  }

  // data source can be is array of arrays.
  _dataSource: any[];
  selected:any=null;
  internalData: any[];

  // datasource type can be string, object or array (of strings)
  // value is determinate and parsing datasource
  private datasourceType = "string"

  onCreateItem = (): any => {
  };

  onChange = (dataSource: any[]) => {
  };
  onTouch = () => {
  };

  constructor(public dialog: MatDialog) {

  }

  ngOnInit() {
    if (!this.displayedColumns || this.displayedColumns.length < 1) {
      this.displayedColumns = this.disabled ? ['value'] : ['value', 'edit']
    }

    this.initInternalData();
  }

  private initInternalData(){
    if (this._dataSource && this._dataSource.length > 0) {
      this.selected = this._dataSource[0];
      this.datasourceType = null
      this.internalData = [...this._dataSource.map(val => {
        // row is simple string value
        if (typeof val === "string" || val instanceof String) {
          if (!this.datasourceType) {
            this.datasourceType="string";
          }
          this.datasourceType="string";
          let obj = {value: val};
          return obj;
        } else if (Array.isArray(val)) {
          if (!this.datasourceType) {
            this.datasourceType="array";
          }

          let rowObject = {};
          let propNames =  Object.entries(this.dataSchema).map(( [propertyName, v] ) =>propertyName  );
          for (let i = 0; i < val.length; ++i) {
            rowObject[propNames[i]] = val[i];
          }
          return rowObject;
        } else {
          if (!this.datasourceType) {
            this.datasourceType="object";
          }
          return val;
        }
      })]
    } else {
      this.datasourceType="string";
      this.internalData=[]
    }
    this.selected = this.internalData && this.internalData.length > 0 ? this.internalData[0] : null;
  }

  openListEditor(event, config?: MatDialogConfig) {
    event.stopPropagation();
  //  const editorDialog = this.dialog.open(EditableListDialogComponent, this.updateData(config));
  }


  public updateDatasource() {
    this._dataSource = this.internalData.map(val => {
      if (this.datasourceType ==='array') {
        let rowValue =  Object.entries(this.dataSchema).map(( [propertyName, v] ) => val[propertyName]  );
        return rowValue;
      } else if (this.datasourceType ==='string')  {
        return val.value;
      } else {
        return val;
      }
    })

    this.onChange(this._dataSource);
  }


  // Allow Angular to set the value on the component
  writeValue(value: any[]): void {
    this.onChange(value);
    this._dataSource = value;
    this.initInternalData();
  }

  // Save a reference to the change function passed to us by
  // the Angular form control
  registerOnCreate(fn: () => any): void {
    console.log("register onCreateItem" + fn)
    this.onCreateItem = fn;
  }

  // Save a reference to the change function passed to us by
  // the Angular form control
  registerOnChange(fn: (datasource: any[]) => void): void {
    this.onChange = fn;
  }

  // Save a reference to the touched function passed to us by
  // the Angular form control
  registerOnTouched(fn: () => void): void {
    this.onTouch = fn;
  }

  // Allow the Angular form control to disable this input
  setDisabledState(disabled: boolean): void {
    this.disabled = disabled;

    if (disabled) {
      this.displayedColumns.splice(this.displayedColumns.lastIndexOf('edit'), 1);
    } else if (this.displayedColumns.lastIndexOf('edit') < 0) {
      this.displayedColumns.push('edit');
    }
  }

  selectRow(row) {
    console.log("Set selection:"  + JSON.stringify(row))
    if (this.selected===row) {
      return;
    }
    if (this.selected !=null) {
      //deselect
    }
    this.selected = row;
    console.log("Set selection1:"  + row)

  }

  get removeButtonEnabled(): boolean {
    return this.selected!==null;
  }

  isRowSelected(row:any): boolean {
    return this.selected===row;
  }

  removeSelectedItem() {
    let index = this._dataSource.indexOf(this.selected);
    if (index > -1) {
      this._dataSource.splice(index, 1);
      this._dataSource = [...this._dataSource];
    }

  }
  addNewItem() {
    console.log("add new Item")
    let newItem = this.onCreateItem();
    console.log("add new Item2 " + JSON.stringify(newItem))
    this._dataSource.push(newItem);
    this._dataSource = [...this._dataSource];
    this.selectRow(newItem);
  }

}
