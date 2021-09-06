import {SearchTableController} from '../../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {SearchTableEntity} from "../../common/search-table/search-table-entity.model";
import {SearchTableValidationResult} from "../../common/search-table/search-table-validation-result.model";
import {DsdRequestDetailsDialogComponent} from "./dsd-request-details-dialog/dsd-request-details-dialog.component";
import {ExtendedHttpClient} from "../../http/extended-http-client";


export class DsdRequestListController implements SearchTableController {


  constructor(protected http: ExtendedHttpClient, public dialog: MatDialog) {

  }

  public showDetails(row: any) {

    /*
    const formRef: MatDialogRef<any> = this.dialog.open(OrganizationDetailsDialogComponent);
    formRef.afterClosed().subscribe(result => {
    });
    */

  }

  public clearFilterValues(){

  }

  public edit(row: any) {
  }

  public delete(row: any) {
  }

  public onCurrentRow(row: any) {
  };

  public getCurrentRow(){
    return null;
  };

  public newDialog(config?: MatDialogConfig): MatDialogRef<any> {
    return this.dialog.open(DsdRequestDetailsDialogComponent, this.convertWithMode(config));
    return null;

  }

  private convertWithMode(config) {

    return (config && config.data)
      ? {
        ...config,
        data: {
          ...config.data,
          //mode: config.data.mode || (config.data.edit ? OrganizationDetailsDialogComponent.EDIT_MODE : OrganizationDetailsDialogComponent.NEW_MODE)
        }
      }
      : config;
  }

  public newRow(): any {
    return null;
  }

  public dataSaved() {
  }

  validateDeleteOperation(rows: Array<SearchTableEntity>) {
    return null;
  }

  public newValidationResult(lst: Array<number>): SearchTableValidationResult {
    return {
      validOperation: false,
      stringMessage: null,
      listId: lst,
    }
  }

  isRowExpanderDisabled(row: SearchTableEntity): boolean {
    return false;
  }


  isRecordChanged(oldModel, newModel): boolean {
    return this.propertyChanged(oldModel, newModel, null);
  }

  propertyChanged(oldModel, newModel, arrayProperties): boolean {

    return false;
  }



}
