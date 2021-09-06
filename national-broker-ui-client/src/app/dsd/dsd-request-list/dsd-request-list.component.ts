import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {SearchTableComponent} from "../../common/search-table/search-table.component";
import {SecurityService} from "../../security/security.service";
import {AlertService} from "../../alert/alert.service";
import {MatDialog} from "@angular/material/dialog";
import {FormBuilder} from "@angular/forms";
import {ColumnPicker} from "../../common/column-picker/column-picker.model";
import {DsdRequestListController} from "./dsd-request-list-controller";
import {NationalBrokerConstants} from "../../national-broker.constants";
import {ActivatedRoute} from "@angular/router";
import {ExtendedHttpClient} from "../../http/extended-http-client";

@Component({
  selector: 'dsd-request-list',
  templateUrl: './dsd-request-list.component.html',
  styleUrls: ['./dsd-request-list.component.scss']
})
export class DsdRequestListComponent implements OnInit {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('rowExtensionAction') rowExtensionAction: TemplateRef<any>;
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;
  @ViewChild('certificateTemplate') certificateTemplate: TemplateRef<any>;





  columnPicker: ColumnPicker = new ColumnPicker();
  dsdRequestListController: DsdRequestListController;
  filter: any = { queryId:"urn:toop:dsd:ebxml-regrem:queries:ByDSDRequestServiceAndAction"};

  constructor(  public securityService: SecurityService,
                protected http: ExtendedHttpClient,
                protected alertService: AlertService,
                public dialog: MatDialog,
                private _formBuilder: FormBuilder,
                private activeParms: ActivatedRoute) {


  }

  ngOnInit(): void {
    this.dsdRequestListController = new DsdRequestListController(this.http, this.dialog);
    this.filter.messageId  =  this.activeParms.snapshot.paramMap.get("messageIdentifier");

    this.columnPicker.allColumns = [
      {
        name: 'Id',
        prop: 'id',
        width: 120,
        maxWidth: 120,
        sortable: false
      },
      {
        name: 'Request Id',
        prop: 'messageId',
        sortable: false,
        width: 400,
        maxWidth: 400,
      },
      {
        name: 'HTTP Method',
        prop: 'httpMethod',
        width: 140,
        maxWidth: 140,
        canAutoResize: true,
        sortable: false
      },

      {
        name: 'HTTP Path',
        prop: 'httpPath',
        canAutoResize: true,
        sortable: false
      },

      {
        name: 'Service',
        prop: 'service',
        width: 200,
        maxWidth: 200,
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Action',
        prop: 'action',
        width: 200,
        maxWidth: 200,
        canAutoResize: true,
        sortable: false
      },

      {
        name: 'Request On',
        prop: 'requestOn',
        width: 200,
        maxWidth: 200,
        sortable: false
      },
      {
        name: 'Response On',
        prop: 'responseOn',
        width: 200,
        maxWidth: 200,
        sortable: false
      },

      {
        name: 'Status',
        prop: 'dsdStatus',
        width: 150,
        maxWidth: 150,
        canAutoResize: true
      },
    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ['Id', 'Request Id','Request On','Response On', 'Request On', 'HTTP Method','HTTP Path', 'Service','Action','Status' ].indexOf(col.name) != -1
    });
  }

  get messagesUrlQuery (): string {
    return NationalBrokerConstants.REST_CONTEXT
      + NationalBrokerConstants.REST_USER_LOG_MESSAGES.replace("{USERNAME}",this.securityService.getCurrentUser()?.username);
  }

}
