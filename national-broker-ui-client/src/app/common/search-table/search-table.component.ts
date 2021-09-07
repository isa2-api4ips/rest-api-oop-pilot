import {Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {SearchTableResult} from './search-table-result.model';
import {Observable} from 'rxjs';
import {AlertService} from '../../alert/alert.service';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {ColumnPicker} from '../column-picker/column-picker.model';
import {RowLimiter} from '../row-limiter/row-limiter.model';
import {SearchTableController} from './search-table-controller';
import {finalize} from 'rxjs/operators';
import {SearchTableEntity} from './search-table-entity.model';
import {SearchTableEntityStatus} from './search-table-entity-status.model';
import {CancelDialogComponent} from '../dialog/cancel-dialog/cancel-dialog.component';
import {SaveDialogComponent} from '../dialog/save-dialog/save-dialog.component';
import {DownloadService} from '../../common/download/download.service';
import {HttpParams} from '@angular/common/http';
import {ConfirmationDialogComponent} from "../dialog/confirmation-dialog/confirmation-dialog.component";
import {SearchTableValidationResult} from "./search-table-validation-result.model";
import {ExtendedHttpClient} from "../../http/extended-http-client";


@Component({
  selector: 'app-search-table',
  templateUrl: './search-table.component.html',
  styleUrls: ['./search-table.component.scss']
})
export class SearchTableComponent implements OnInit {
  @ViewChild('searchTable') searchTable: any;
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('rowIndex') rowIndex: TemplateRef<any>;
  @ViewChild('rowExpand') rowExpand: TemplateRef<any>;


  // @ViewChild('additionalToolButtons') additionalToolButtons: TemplateRef<any>;
  @ViewChild('additionalRowActionButtons') additionalRowActionButtons: TemplateRef<any>;

  @Input() id: string = "";
  @Input() title: string = "";
  @Input() icon: string = "";
  @Input() tooltip: string = "";
  @Input() searchPanelHeight: number = 170;
  @Input() columnPicker: ColumnPicker;
  @Input() urlQuery: string = '';
  @Input() urlManage: string = '';
  @Input() searchTableController: SearchTableController;
  @Input() filter: any = {};
  @Input() sort: string = '';
  @Input() showActionButtons: boolean = true;
  @Input() showSearchPanel: boolean = true;
  @Input() showIndexColumn: boolean = false;
  @Input() allowNewItems: boolean = false;
  @Input() allowDeleteItems: boolean = false;
  @Input() allowEditItems: boolean = true;

  @Input() additionalToolButtons: TemplateRef<any>;
  @Input() searchPanel: TemplateRef<any>;
  @Input() tableRowDetailContainer: TemplateRef<any>;
  @Input() immediateRowManageAction: boolean = true;
  @Input() showBottomDetails: boolean = false;
  @Input() tableBottomDetailContainer: TemplateRef<any>;


  loading = false;

  columnActions: any;
  columnExpandDetails: any;
  columnIndex: any;

  rowLimiter: RowLimiter = new RowLimiter();

  rowNumber: number;

  rows: Array<SearchTableEntity> = [];
  selected: Array<SearchTableEntity> = [];

  count: number = 0;
  currentPage: number = 0;
  orderBy: string = null;
  asc = false;
  forceRefresh: boolean = false;
  showSpinner: boolean = false;


  constructor(protected http: ExtendedHttpClient,
              protected alertService: AlertService,
              private downloadService: DownloadService,
              public dialog: MatDialog) {
  }

  ngOnInit() {
    this.columnIndex = {
      cellTemplate: this.rowIndex,
      name: 'Index',
      width: 50,
      maxWidth: 80,
      sortable: false
    };

    this.columnActions = {
      cellTemplate: this.rowActions,
      name: 'Actions',
      width: 250,
      maxWidth: 250,
      sortable: false
    };
    this.columnExpandDetails = {
      cellTemplate: this.rowExpand,
      name: 'Upd.',
      width: 40,
      maxWidth: 50,
      sortable: false
    };

    // Add actions to last column
    if (this.columnPicker) {
      // prepend columns
      if (!!this.tableRowDetailContainer) {
        this.columnPicker.allColumns.unshift(this.columnExpandDetails);
        this.columnPicker.selectedColumns.unshift(this.columnExpandDetails);
      }
      if (this.showIndexColumn) {
        this.columnPicker.allColumns.unshift(this.columnIndex);
        this.columnPicker.selectedColumns.unshift(this.columnIndex);
      }
      /* for the demo disable show
            if (this.showActionButtons) {
              this.columnPicker.allColumns.push(this.columnActions);
              this.columnPicker.selectedColumns.push(this.columnActions);
            }

       */
    }
  }

  getRowClass(row) {
    return {
      'table-row-new': (row.status === SearchTableEntityStatus.NEW),
      'table-row-updated': (row.status === SearchTableEntityStatus.UPDATED),
      'deleted': (row.status === SearchTableEntityStatus.REMOVED)
    };
  }

  getTableDataEntries$(currentPage: number, limit: number, sort: string, asc: boolean): Observable<SearchTableResult> {
    let params: HttpParams = new HttpParams()
      .set('offset', (currentPage * limit).toString())
      .set('limit', limit.toString());

    // set sort parameter chars must be encoded because of sign +
    params = params.set("sort", encodeURIComponent(this.sort));
    // for the demo filter is parameter q with the JSON object
    params = params.set("q", btoa(JSON.stringify(this.filter)));

    /*for (let filterProperty in this.filter) {
      if (this.filter.hasOwnProperty(filterProperty)) {
        // must encode else problem with + sign
        query = queryId=urn:toop:dsd:ebxml-regrem:queries:ByDatasetTypeAndLocation&JSON.stringify(g)
        params = params.set(filterProperty, encodeURIComponent(this.filter[filterProperty]));
      }
    }*/

    this.loading = true;
    return this.http.get<SearchTableResult>(this.urlQuery, {params}).pipe(
      finalize(() => {
        this.loading = false;
      })
    );
  }

  page(currentPage: number, limit: number, orderBy: string, asc: boolean) {
    if (this.safeRefresh) {

      this.dialog.open(ConfirmationDialogComponent, {
        data: {
          title: "Not persisted data!",
          description: "Action will refresh all data and not saved data will be lost. Do you wish to continue?"
        }
      }).afterClosed().subscribe(result => {
        if (result) {
          this.pageInternal(currentPage, limit, orderBy, asc);
        }
      })
    } else {
      this.pageInternal(currentPage, limit, orderBy, asc);
    }
  }

  private pageInternal(currentPage: number, limit: number, orderBy: string, asc: boolean) {
    this.getTableDataEntries$(currentPage, limit, orderBy, asc).subscribe((result: SearchTableResult) => {
      this.searchTableController.onCurrentRow(null);
      // empty page - probably refresh from delete...check if we can go one page back
      // try again
      if (result.count < 1 && currentPage > 0) {
        this.pageInternal(currentPage--, limit, orderBy, asc)
      } else {
        let currentSelectedRow = this.rowNumber;

        // calculate currentPage!
        this.currentPage = currentPage;
        this.rowLimiter.limit = limit;
        this.orderBy = orderBy;
        this.asc = asc;
        this.unselectRows();
        this.forceRefresh = false;
        this.count = result.count; // must be set else table can not calculate page numbers
        this.rows = result.serviceEntities.map(serviceEntity => {
          return {
            ...serviceEntity,
            status: SearchTableEntityStatus.PERSISTED,
            deleted: false
          }
        });


        setTimeout(() => {
          this.searchTable.selected = [];
          this.searchTable.selected.push(this.rows[currentSelectedRow]);
          this.searchTableController.onCurrentRow(this.rows[currentSelectedRow]);
          this.rowNumber = currentSelectedRow;
        }, 200)


      }
    }, (error: any) => {
      this.alertService.error("Error occurred:" + error);
    });
  }

  onPage(event) {
    this.page(event.offset, event.limit, this.orderBy, this.asc);
  }

  onSort(event) {
    let ascending = event.newValue !== 'desc';
    this.page(this.currentPage, this.rowLimiter.limit, event.column.prop, ascending);
  }

  onSelect({selected}) {
    this.selected = [...selected];
    if (this.editButtonEnabled) {
      this.rowNumber = this.rows.indexOf(this.selected[0]);
      this.onSelectCurrentRow(this.selected[0]);
    } else {
      this.onSelectCurrentRow(null);
    }
  }

  onActivate(event) {
    if ("dblclick" === event.type) {
      this.editSearchTableEntityRow(event.row);
    }
  }

  changePageSize(newPageLimit: number) {
    this.page(0, newPageLimit, this.orderBy, this.asc);
  }

  search() {
    this.page(0, this.rowLimiter.limit, this.orderBy, this.asc);
  }


  onDeleteRowActionClicked(row: SearchTableEntity) {
    this.validateAndDeleteSearchTableEntities([row]);
  }

  onNewButtonClicked() {
    const formRef: MatDialogRef<any> = this.searchTableController.newDialog({
      data: {
        edit: false,
        immediateRowManageAction: this.immediateRowManageAction,
        urlManage: this.urlManage
      }
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        if (this.immediateRowManageAction) {
          this.rows = [...this.rows, {...formRef.componentInstance.getCurrent()}];
          this.count++;
          // just to force table update
          this.searchTable.selected = [];


        } else {

          this.rows = [...this.rows, {...formRef.componentInstance.getCurrent()}];
          //this.rows = this.rows.concat(formRef.componentInstance.current);
          this.count++;
        }
      } else {
        this.unselectRows();
      }
    });
  }

  onDeleteButtonClicked() {
    this.validateAndDeleteSearchTableEntities(this.selected);
  }

  onEditButtonClicked() {
    if (this.rowNumber >= 0 && this.rows[this.rowNumber] && this.rows[this.rowNumber].deleted) {
      this.alertService.error('You cannot edit a deleted entry.', false);
      return;
    }
    this.editSearchTableEntity(this.rowNumber);
  }

  onSaveButtonClicked(withDownloadCSV: boolean) {
    try {
      this.dialog.open(SaveDialogComponent).afterClosed().subscribe(result => {
        if (result) {
          // this.unselectRows();
          const modifiedRowEntities = this.rows.filter(el => el.status !== SearchTableEntityStatus.PERSISTED);
          // this.isBusy = true;
          this.showSpinner = true;
          this.http.put(this.urlManage, modifiedRowEntities).toPromise().then(res => {
            this.showSpinner = false;
            // this.isBusy = false;
            // this.getUsers();
            this.alertService.success('The operation \'update\' completed successfully.', false);
            this.forceRefresh = true;
            this.onRefresh();
            this.searchTableController.dataSaved();
            if (withDownloadCSV) {
              this.downloadService.downloadNative(/*UserComponent.USER_CSV_URL TODO: use CSV urlQuery*/ '');
            }
          }, err => {
            this.showSpinner = false;
            try {
              console.log("eror: " + err)
              let parser = new DOMParser();
              let xmlDoc = parser.parseFromString(err.error, "text/xml");
              let errDesc = xmlDoc.getElementsByTagName("ErrorDescription")[0].childNodes[0].nodeValue;
              this.alertService.exception('The operation \'update\' not completed successfully.', errDesc, false);
            } catch (err2) {
              // if parse failed
              this.alertService.exception('The operation \'update\' not completed successfully.', err, false);
            }
          });
        } else {
          this.showSpinner = false;
          if (withDownloadCSV) {
            this.downloadService.downloadNative(/*UserComponent.USER_CSV_URL TODO: use CSV urlQuery*/ '');
          }
        }
      });
    } catch (err) {
      // this.isBusy = false;
      this.showSpinner = false;
      this.alertService.exception('The operation \'update\' completed with errors.', err);
    }
  }

  onRefresh() {
    this.page(this.currentPage, this.rowLimiter.limit, this.orderBy, this.asc);
  }

  onCancelButtonClicked() {
    this.dialog.open(CancelDialogComponent).afterClosed().subscribe(result => {
      if (result) {
        this.onRefresh();
      }
    });
  }

  getRowsAsString(): number {
    return this.rows.length;
  }

  get editButtonEnabled(): boolean {
    return this.selected && this.selected.length == 1 && !this.selected[0].deleted;
  }

  get deleteButtonEnabled(): boolean {
    return this.selected && this.selected.length > 0 && !this.selected.every(el => el.deleted);
  }

  get submitButtonsEnabled(): boolean {
    const rowsDeleted = !!this.rows.find(row => row.deleted);
    const dirty = rowsDeleted || !!this.rows.find(el => el.status !== SearchTableEntityStatus.PERSISTED);
    return dirty;
  }

  get safeRefresh(): boolean {
    return !(!this.submitButtonsEnabled || this.forceRefresh);
  }

  isRowExpanderDisabled(row: any, rowDisabled: boolean): boolean {
    return rowDisabled || this.searchTableController.isRowExpanderDisabled(row);
  }

  public clearFilterValues() {
    this.searchTableController.clearFilterValues();
  }

  editSearchTableEntity(rowNumber: number) {
    const row = this.rows[rowNumber];
    const formRef: MatDialogRef<any> = this.searchTableController.newDialog({
      data: {
        edit: true,
        row,
        immediateRowManageAction: this.immediateRowManageAction,
        urlManage: this.urlManage
      }
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        const changed = this.searchTableController.isRecordChanged(row, formRef.componentInstance.getCurrent());
        if (changed) {
          if (this.immediateRowManageAction) {
            this.rows[rowNumber] = {...formRef.componentInstance.getCurrent()};
            // just to force table update
            this.rows = [...this.rows];
            this.searchTable.selected = [];
            this.searchTable.selected.push(this.rows[rowNumber]);
            // Hack: just to force table update
            this.searchTableController.onCurrentRow(null);
            // refresh after delay else components listening this object are not updated
            setTimeout(() => {
              this.searchTableController.onCurrentRow(this.rows[rowNumber]);
            }, 200)


          } else {

            const status = row.status === SearchTableEntityStatus.PERSISTED
              ? SearchTableEntityStatus.UPDATED
              : row.status;
            this.rows[rowNumber] = {...formRef.componentInstance.getCurrent(), status};
            console.log("row has changed!")
          }
        }
      }
    });
  }

  public updateTableRow(rowNumber: number, row: any, status: SearchTableEntityStatus) {
    this.rows[rowNumber] = {...row, status};
    this.rows = [...this.rows];
  }

  public getRowNumber(row: any) {
    return this.rows.indexOf(row);
  }

  onSelectCurrentRow(row: SearchTableEntity) {
    this.searchTableController.onCurrentRow(row);
  }

  editSearchTableEntityRow(row: SearchTableEntity) {
    let rowNumber = this.rows.indexOf(row);
    this.editSearchTableEntity(rowNumber);

  }

  validateAndDeleteSearchTableEntities(rows: Array<SearchTableEntity>) {

    let validationObserver = this.searchTableController.validateDeleteOperation(rows);
    if (validationObserver === null) {

      this.deleteSearchTableEntities(rows);
    } else {
      this.searchTableController.validateDeleteOperation(rows).subscribe((res: SearchTableValidationResult) => {
        if (!res.validOperation) {

          this.alertService.exception("Delete validation error", res.stringMessage, false);
        } else {
          this.deleteSearchTableEntities(rows);

        }
        this.unselectRows();
      });
    }
  }

  deleteSearchTableEntities(rows: Array<SearchTableEntity>) {
    if (this.immediateRowManageAction) {
      this.dialog.open(ConfirmationDialogComponent, {
        data: {
          title: "Delete selected row!",
          description: "National broker will send request to DSD?"
        }
      }).afterClosed().subscribe(result => {
        if (result) {
          for (const row of rows) {
            this.searchTableController.delete(row, this.urlManage);
          }
        }
      })
    } else {
      for (const row of rows) {
        if (row.status === SearchTableEntityStatus.NEW) {
          this.rows.splice(this.rows.indexOf(row), 1);
        } else {
          this.searchTableController.delete(row);
          row.status = SearchTableEntityStatus.REMOVED;
          row.deleted = true;
        }
      }
    }
  }

  unselectRows() {
    this.selected = [];
    this.rowNumber = -1;
  }

  toggleExpandRow(selectedRow: any) {
    //this.searchTableController.toggleExpandRow(selectedRow);
    this.searchTable.rowDetail.toggleExpandRow(selectedRow);
  }

  onDetailToggle(event) {

  }

  isDirty(): boolean {
    return this.submitButtonsEnabled;
  }
}
