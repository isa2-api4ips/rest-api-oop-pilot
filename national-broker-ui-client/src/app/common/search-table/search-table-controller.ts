import {MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {SearchTableEntity} from './search-table-entity.model';

export interface SearchTableController {
  showDetails(row: any);
  edit(row: any);
  clearFilterValues();
  validateDeleteOperation(rows: Array<SearchTableEntity>);
  delete(row: any, urlManage?:string);
  newRow(): SearchTableEntity;
  newDialog(config?: MatDialogConfig): MatDialogRef<any>;
  dataSaved();
  isRecordChanged(oldModel, newModel): boolean;

  onCurrentRow(row: any);
  getCurrentRow(): any;

  /**
   * Returns whether the row expander should be shown as disabled even when the actual row is not fully disabled.
   *
   * @param row the row for which the row expander should be disabled or not
   */
  isRowExpanderDisabled(row: SearchTableEntity): boolean;

}
