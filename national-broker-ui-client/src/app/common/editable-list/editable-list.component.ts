import {Component, Input, TemplateRef} from '@angular/core';
import {MatDialog, MatDialogConfig, MatDialogRef} from "@angular/material/dialog";
import {EditableListDialogComponent} from "./editable-list-dialog/editable-list-dialog.component";
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';

@Component({
  selector: 'editable-list',
  templateUrl: './editable-list.component.html',
  styleUrls: ['./editable-list.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: EditableListComponent,
      multi: true,
    },
  ],
})
export class EditableListComponent implements ControlValueAccessor {

  @Input() listTemplate: TemplateRef<any>;
  //@Input() dataSource: any[];
  @Input() title: string;
  @Input() fieldStyle: string = "width:300px";
  @Input() disabled = false;
  @Input() displayedColumns: string[] = [];
  // default string schema
  @Input() dataSchema: any = {"value": "text"};
  @Input() set dataSource(value: any[]) {
    console.log("Set datasource: "+this.title+":" + JSON.stringify(value))
    this._dataSource = value;
    // init internalData values
    this.initInternalData();
  }
  get dataSource(): any[] {
    return this._dataSource;
  }

  // internal data is array of objects where properties are column values. Property names are defined in dataSchema
  internalData: any[];
  // data source can be is array of arrays.
  _dataSource: any[];
  selected: any;

  // datasource type can be string, object or array (of strings)
  // value is determinate and parsing datasource
  private datasourceType = "string"



  onChange = (dataSource: any[]) => {
    this.initInternalData();
  };
  onTouch = () => {
  };

  constructor(public dialog: MatDialog) {

  }

  ngOnInit() {
    if (!this.displayedColumns || this.displayedColumns.length < 1) {
      // the default schema for string list. The Edit column add edit action button.
      this.displayedColumns = this.disabled ? ['value'] : ['value', 'edit']
    }

    this.initInternalData();
  }


  openListEditor(event, config?: MatDialogConfig) {
    event.stopPropagation();
    let editorDialog: MatDialogRef<any> = this.dialog.open(EditableListDialogComponent, this.updateDialogData(config));
    editorDialog.afterClosed().subscribe(result => {
      if (result) {
        this.internalData = [...editorDialog.componentInstance.getDataSource()];
        this.updateDatasource();
        this.onChange(this.dataSource);
      }
    });
  }

  private updateDialogData(config) {
    return {
      ...config,
      panelClass: 'dialog-container-custom',
      data: {
        dataSource: [...this.internalData],
        dataSchema: this.dataSchema,
        displayedColumns: this.displayedColumns,
      }
    };
  }

  public updateDatasource() {
    this._dataSource.length = 0;
    this.internalData.forEach( val => {
      if (this.datasourceType ==='array') {
        let rowValue =  Object.entries(this.dataSchema).map(( [propertyName, v] ) => val[propertyName]  );
        this._dataSource.push( rowValue);
      } else if (this.datasourceType ==='string')  {
        this._dataSource.push(  val.value);
      } else {
        this._dataSource.push(  val );
      }
    })

/*
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
*/
    this.onChange(this.dataSource);
  }

  // Allow Angular to set the value on the component
  writeValue(value: any[]): void {
    this.onChange(value);
    this.dataSource = value;
    this.initInternalData();
  }

  /**
   * Method converts row array to "property object"
   * @private
   */
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

}
