import {SearchTableController} from '../../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {SearchTableEntity} from "../../common/search-table/search-table-entity.model";
import {SearchTableValidationResult} from "../../common/search-table/search-table-validation-result.model";
import {DatasetDetailsDialogComponent} from "./dataset-details-dialog/dataset-details-dialog.component";
import {AlertService} from "../../alert/alert.service";
import {ExtendedHttpClient} from "../../http/extended-http-client";


export class DatasetListController implements SearchTableController {


  constructor(protected http: ExtendedHttpClient, public dialog: MatDialog,protected alertService: AlertService,) {

  }

  public showDetails(row: any) {
    const formRef: MatDialogRef<any> = this.dialog.open(DatasetDetailsDialogComponent);
    formRef.afterClosed().subscribe(result => {
    });
  }

  public clearFilterValues() {

  }

  public edit(row: any) {
  }

  public delete(row: any, urlManage?:string) {
    this.http.delete(urlManage + "/" + row.identifiers[0]).toPromise().then(res => {

      this.alertService.success('The operation \'delete\' was submitted successfully. To see the result refresh the page!', false);


    }, err => {

      try {
        console.log("error: " + err)
        let parser = new DOMParser();
        let xmlDoc = parser.parseFromString(err.error, "text/xml");
        let errDesc = xmlDoc.getElementsByTagName("ErrorDescription")[0].childNodes[0].nodeValue;
        this.alertService.exception('The operation \'delete\' not completed successfully.', errDesc, false);
      } catch (err2) {
        // if parse failed
        this.alertService.exception('The operation \'delete\' not completed successfully.', err, false);
      }
    });

  }


  public onCurrentRow(row: any) {
  };

  public getCurrentRow() {
    return null;
  };

  public newDialog(config?: MatDialogConfig): MatDialogRef<any> {
    return this.dialog.open(DatasetDetailsDialogComponent, this.convertWithMode(config));

    return null;
  }

  private convertWithMode(config) {

    return (config && config.data)
      ? {
        ...config,
        data: {
          ...config.data,
          mode: config.data.mode || (config.data.edit ? DatasetDetailsDialogComponent.EDIT_MODE : DatasetDetailsDialogComponent.NEW_MODE)
        }
      }
      : config;
  }

  public newRow(): any {
    return null;
  }

  public dataSaved() {
    // this.lookups.refreshUserLookup();
  }

  validateDeleteOperation(rows: Array<SearchTableEntity>) {
    var deleteRowIds = rows.map(rows => rows.id);
    //return this.http.post<SearchTableValidationResult>(SmpConstants.REST_USER_VALIDATE_DELETE, deleteRowIds);
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
    return false;
  }

  propertyChanged(oldModel, newModel, arrayProperties): boolean {


    return false;
  }

  isEqual(val1, val2): boolean {
    return (this.isEmpty(val1) && this.isEmpty(val2)
      || val1 === val2);
  }

  isEmpty(str): boolean {
    return (!str || 0 === str.length);
  }

  isNull(obj): boolean {
    return !obj
  }


}
