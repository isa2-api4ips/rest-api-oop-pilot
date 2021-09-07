import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {SearchTableComponent} from "../../common/search-table/search-table.component";
import {SecurityService} from "../../security/security.service";
import {HttpClient} from "@angular/common/http";
import {AlertService} from "../../alert/alert.service";
import {MatDialog} from "@angular/material/dialog";
import {FormBuilder} from "@angular/forms";
import {ColumnPicker} from "../../common/column-picker/column-picker.model";
import {UserListController} from "./user-list-controller";
import {NationalBrokerConstants} from "../../national-broker.constants";

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('rowExtensionAction') rowExtensionAction: TemplateRef<any>;
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;
  @ViewChild('certificateTemplate') certificateTemplate: TemplateRef<any>;

  columnPicker: ColumnPicker = new ColumnPicker();
  userListController: UserListController;

  constructor(  public securityService: SecurityService,
                protected http: HttpClient,
                protected alertService: AlertService,
                public dialog: MatDialog,
                private _formBuilder: FormBuilder) {


  }

  ngOnInit(): void {
    this.userListController = new UserListController(this.http, this.dialog);

    this.columnPicker.allColumns = [
      {
        name: 'Active',
        prop: 'active',
        canAutoResize: true
      },
      {
        name: 'UserName',
        prop: 'username',
        canAutoResize: true
      },
      {
        name: 'Name',
        prop: 'name',
        canAutoResize: true
      },
      {
        name: 'e-mail',
        prop: 'email',
        canAutoResize: true
      },
      {
        name: 'DSD organizations',
        prop: 'organizations',
        canAutoResize: true
      },
      {
        name: 'Roles',
        prop: 'userAuthRoles',
        canAutoResize: true
      },
    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ['Active','UserName', 'Name', 'Roles','e-mail' ].indexOf(col.name) != -1
    });
  }

  get usersUrlQuery (): string {
    return NationalBrokerConstants.REST_CONTEXT
      + NationalBrokerConstants.REST_ADMIN;
  }

}
