import {SearchTableController} from '../../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {SearchTableEntity} from "../../common/search-table/search-table-entity.model";
import {SearchTableValidationResult} from "../../common/search-table/search-table-validation-result.model";
import {OrganizationUpdate} from "../organization-list/organization/organization-update.model";
import {ExtendedHttpClient} from "../../http/extended-http-client";


export class DsdDataUpdateListController implements SearchTableController {


  compareOrganizationProperties = ["identifier", "prefLabels", "altLabels", "classifications", "address"];
  compareAddressProperties = ["adminUnitLevel", "fullAddress"];

  currentDSDUpdate: OrganizationUpdate;
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

  public onCurrentRow(row:any) {
    this.currentDSDUpdate = {...row}
  };

  public getCurrentRow() :OrganizationUpdate {
    return this.currentDSDUpdate;
  };

  public newDialog(config?: MatDialogConfig): MatDialogRef<any> {
   // return this.dialog.open(OrganizationDetailsDialogComponent, this.convertWithMode(config));
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

  isAddressChanged(oldAddress, newAddress): boolean {
    if (this.isNull(oldAddress) && this.isNull(newAddress)) {
      console.log("both null return false! ");
      return false;
    }

    if (this.isNull(oldAddress)) {
      //oldCert = this.nullAddress;
    }

    if (this.isNull(newAddress)) {
      // newCert = this.nullAddress;
    }

    return this.propertyChanged(oldAddress, newAddress, this.compareAddressProperties);
  }

  isRecordChanged(oldModel, newModel): boolean {
    return this.propertyChanged(oldModel, newModel, this.compareOrganizationProperties);
  }

  propertyChanged(oldModel, newModel, arrayProperties): boolean {


    let propSize = arrayProperties.length;
    for (let i = 0; i < propSize; i++) {

      let property = arrayProperties[i];
      if (property === 'address') {
        if (this.isAddressChanged(oldModel[property], newModel[property])) {
          return true; // Property has changed
        }
      } else {
        const isEqual = this.isEqual(newModel[property], oldModel[property]);
        if (!isEqual) {
          console.log("property " + property + " is changed! ");
          return true; // Property has changed
        }
      }
    }
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
