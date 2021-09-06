import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {SearchTableComponent} from "../../common/search-table/search-table.component";
import {SecurityService} from "../../security/security.service";
import {AlertService} from "../../alert/alert.service";
import {MatDialog} from "@angular/material/dialog";
import {FormBuilder} from "@angular/forms";
import {ColumnPicker} from "../../common/column-picker/column-picker.model";
import {NationalBrokerConstants} from "../../national-broker.constants";
import {DsdDataUpdateListController} from "./dsd-data-update-list-controller";
import {ExtendedHttpClient} from "../../http/extended-http-client";

@Component({
  selector: 'dsd-data-update-list',
  templateUrl: './dsd-data-update-list.component.html',
  styleUrls: ['./dsd-data-update-list.component.scss']
})
export class DsdDataUpdateListComponent implements OnInit {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('rowExtensionAction') rowExtensionAction: TemplateRef<any>;
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;
  @ViewChild('certificateTemplate') certificateTemplate: TemplateRef<any>;

  columnPicker: ColumnPicker = new ColumnPicker();
  organizationUpdateListController: DsdDataUpdateListController;
  filter: any = {};

  constructor(  public securityService: SecurityService,
                protected http: ExtendedHttpClient,
                protected alertService: AlertService,
                public dialog: MatDialog,
                private _formBuilder: FormBuilder) {


  }

  ngOnInit(): void {
    this.organizationUpdateListController = new DsdDataUpdateListController(this.http, this.dialog);

    this.columnPicker.allColumns = [

      {
        name: 'Entity type',
        prop: 'entityType',
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Service',
        prop: 'service',
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Action',
        prop: 'action',
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Request Id',
        prop: 'updateRequestId',
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Request On',
        prop: 'updateRequestOn',
        width: 200,
        maxWidth: 200,
        sortable: false
      },
      {
        name: 'Response Id',
        prop: 'updateResponseId',
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Update Confirmed On',
        prop: 'updateConfirmedOn',
        width: 200,
        maxWidth: 200,
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Organization Id',
        prop: 'organizationIdentifier',
        width: 150,
        maxWidth: 150,
        canAutoResize: true
      },
      {
        name: 'Username',
        prop: 'username',
        width: 150,
        maxWidth: 150,
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Status',
        prop: 'dsdStatus',
        width: 150,
        maxWidth: 150,
        canAutoResize: true
      }


    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ['Entity type','Service','Action','Organization IO','Username','Request Id', 'Request On', 'Response Id','Update Confirmed On','Status' ].indexOf(col.name) != -1
    });
  }

  get organizationUpdatesUrlQuery (): string {
    return NationalBrokerConstants.REST_CONTEXT
      + NationalBrokerConstants.REST_USER_ORGANIZATION_UPDATES.replace("{USERNAME}",this.securityService.getCurrentUser()?.username);
  }

  get selectedRequestIdentifier(){
    return this.organizationUpdateListController.currentDSDUpdate?.updateRequestId;
  }

  get selectedResponseIdentifier(){
    return this.organizationUpdateListController.currentDSDUpdate?.updateResponseId;
  }

}
