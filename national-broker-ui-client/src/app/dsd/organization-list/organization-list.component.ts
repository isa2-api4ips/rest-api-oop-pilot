import {Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {SearchTableComponent} from "../../common/search-table/search-table.component";
import {ColumnPicker} from "../../common/column-picker/column-picker.model";
import {SecurityService} from "../../security/security.service";
import {AlertService} from "../../alert/alert.service";
import {MatDialog} from "@angular/material/dialog";
import {OrganizationListController} from "./organization-list-controller";
import {Observable} from "rxjs";
import {FormBuilder, FormGroup} from "@angular/forms";
import {map, startWith} from "rxjs/operators";
import {OrganizationModel} from "./organization/organization.model";
import {NationalBrokerConstants} from "../../national-broker.constants";
import {ExtendedHttpClient} from "../../http/extended-http-client";

export interface CountryGroup {
  letter: string;
  names: string[];
}

export const _filter = (opt: string[], value: string): string[] => {
  const filterValue = value.toLowerCase();

  return opt.filter(item => item.toLowerCase().indexOf(filterValue) === 0);
};

@Component({
  selector: 'dsd-organization-list',
  templateUrl: './organization-list.component.html',
  styleUrls: ['./organization-list.component.scss']
})
export class OrganizationListComponent implements OnInit {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('rowExtensionAction') rowExtensionAction: TemplateRef<any>;
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;
  @ViewChild('certificateTemplate') certificateTemplate: TemplateRef<any>;

  stateForm: FormGroup = this._formBuilder.group({
    stateGroup: '',
  });

  stateGroups: CountryGroup[] = [{
    letter: 'A',
    names: ['Austria']
  },
    {
      letter: 'B',
      names: ['Belgium', 'Bulgaria']
    }, {
      letter: 'C',
      names: ['Croatia', 'Cyprus', 'Czechia']
    }, {
      letter: 'D',
      names: ['Denmark']
    },
    {
      letter: 'E',
      names: ['Estonia']
    }, {
      letter: 'F',
      names: ['France']
    }, {
      letter: 'G',
      names: ['Germany', "Greece"]
    }, {
      letter: 'H',
      names: ['Hungary']
    }, {
      letter: 'I',
      names: ['Ireland', "Italy"]
    }, {
      letter: 'L',
      names: ['Latvia', 'Lithuania', 'Luxembourg']
    }, {
      letter: 'M',
      names: ['Malta']
    }, {
      letter: 'N',
      names: ['Netherlands']
    }, {
      letter: 'P',
      names: ['Poland', 'Portugal']
    }, {
      letter: 'R',
      names: ['Romania']
    }, {
      letter: 'S',
      names: ['Slovakia', 'Slovenia', 'Spain', '\tSweden']

    }];

  stateGroupOptions: Observable<CountryGroup[]>;
  columnPicker: ColumnPicker = new ColumnPicker();
  organizationListController: OrganizationListController;
  filter: any = { queryId:"urn:toop:dsd:ebxml-regrem:queries:ByOrganizationIdAndName"};
  collapsed: boolean;
  @Input() selectedOrganization: OrganizationModel;

  constructor(
    public securityService: SecurityService,
    protected http: ExtendedHttpClient,
    protected alertService: AlertService,
    public dialog: MatDialog,
    private _formBuilder: FormBuilder) {
  }
  private _filterGroup(value: string): CountryGroup[] {
    if (value) {
      return this.stateGroups
        .map(group => ({letter: group.letter, names: _filter(group.names, value)}))
        .filter(group => group.names.length > 0);
    }

    return this.stateGroups;
  }
  ngOnInit() {
    this.organizationListController = new OrganizationListController(this.http, this.dialog);
    this.stateGroupOptions = this.stateForm.get('stateGroup')!.valueChanges
      .pipe(
        startWith(''),
        map(value => this._filterGroup(value))
      );
    this.columnPicker.allColumns = [
      {
        name: 'Organization ID',
        prop: 'identifier',
        width: 150,
        maxWidth: 150,
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Organization name',
        prop: 'prefLabels',
        width: 200,
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Description',
        prop: 'altLabels',
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Last request',
        prop: 'updateRequestId',
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Update request on',
        prop: 'updateRequestOn',
        width: 200,
        maxWidth: 200,
        canAutoResize: true,
        sortable: false
      },
      {
        name: 'Last response',
        prop: 'updateResponseId',
        canAutoResize: true,
        sortable: false
      },
       {
        name: 'Updated on',
        prop: 'updateConfirmedOn',
         width: 200,
         maxWidth: 200,
        canAutoResize: false,
        sortable: false
      },

    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ['Organization ID', 'Organization name', 'Description'].indexOf(col.name) != -1

    });

  }

  get editButtonEnabled(){
    return this.searchTable?.editButtonEnabled;
  }

  get selectedIdentifier(){
    return this.organizationListController.currentOrganization?.identifier;
  }

  get organizationUrlQuery (): string {
    return NationalBrokerConstants.REST_CONTEXT
      + NationalBrokerConstants.REST_USER_ORGANIZATIONS.replace("{USERNAME}",this.securityService.getCurrentUser()?.username);
  }
  get organizationManageUrlQuery (): string {
    return NationalBrokerConstants.REST_CONTEXT
      + NationalBrokerConstants.REST_USER_ORGANIZATION_MANAGE.replace("{USERNAME}",this.securityService.getCurrentUser()?.username);
  }



  details(row: any) {
    this.organizationListController.showDetails(row);
  }

  // for dirty guard...
  isDirty(): boolean {
    return this.searchTable.isDirty();
  }

  toggleExpandRow(selectedRow: any) {
    //this.searchTableController.toggleExpandRow(selectedRow);
   // this.searchTable.tableRowDetailContainer.toggleExpandRow(selectedRow);
  }

  onDetailToggle(event) {

  }

}
