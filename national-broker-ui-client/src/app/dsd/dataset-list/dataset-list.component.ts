import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {SearchTableComponent} from "../../common/search-table/search-table.component";
import {SecurityService} from "../../security/security.service";
import {AlertService} from "../../alert/alert.service";
import {MatDialog} from "@angular/material/dialog";
import {FormBuilder} from "@angular/forms";
import {ColumnPicker} from "../../common/column-picker/column-picker.model";
import {DatasetListController} from "./dataset-list-controller";
import {NationalBrokerConstants} from "../../national-broker.constants";
import {ActivatedRoute} from "@angular/router";
import {ExtendedHttpClient} from "../../http/extended-http-client";

@Component({
  selector: 'dataset-list',
  templateUrl: './dataset-list.component.html',
  styleUrls: ['./dataset-list.component.scss']
})
export class DatasetListComponent implements OnInit {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('rowExtensionAction') rowExtensionAction: TemplateRef<any>;
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;
  @ViewChild('certificateTemplate') certificateTemplate: TemplateRef<any>;

  columnPicker: ColumnPicker = new ColumnPicker();
  datasetListController: DatasetListController;
  filter: any = { queryId:"urn:toop:dsd:ebxml-regrem:queries:ByDatasetTypeAndLocation"};

  constructor(public securityService: SecurityService,
              protected http: ExtendedHttpClient,
              protected alertService: AlertService,
              public dialog: MatDialog,
              private _formBuilder: FormBuilder,
              private activeParms: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.datasetListController = new DatasetListController(this.http, this.dialog, this.alertService);
    this.filter.organizationIdentifier  =  this.activeParms.snapshot.paramMap.get("organizationIdentifier");

    this.columnPicker.allColumns = [
      {
        name: 'Type',
        prop: 'type',
        width: 200,
        maxWidth: 200,
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Conforms To',
        prop: 'conformsTo',
        sortable: false
      },
      {
        name: 'Identifiers',
        prop: 'identifiers',
        width: 300,
        maxWidth: 400,
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Titles',
        prop: 'titles',
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Publisher Id',
        prop: 'publisher.identifier',
        width: 150,
        maxWidth: 150,
        canAutoResize: true
      },
    ];


    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ['Type', 'Conforms To', 'Identifiers', 'Titles', 'Publisher Id'].indexOf(col.name) != -1
    });
  }

  get datasetUrlQuery (): string {
    return NationalBrokerConstants.REST_CONTEXT
      + NationalBrokerConstants.REST_USER_DATASET.replace("{USERNAME}",this.securityService.getCurrentUser()?.username);
  }

  get organizationManageUrlQuery (): string {
    return NationalBrokerConstants.REST_CONTEXT
      + NationalBrokerConstants.REST_USER_DATASET_MANAGE.replace("{USERNAME}",this.securityService.getCurrentUser()?.username);
  }
}
