import {ChangeDetectorRef, Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {AlertService} from "../../../alert/alert.service";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {SecurityService} from "../../../security/security.service";
import {SearchTableEntityStatus} from "../../../common/search-table/search-table-entity-status.model";
import {MatChipInputEvent} from "@angular/material/chips";
import {DatasetModel} from "../dataset/dataset.model";
import {OrganizationAddress} from "../../organization-list/organization/organization-address.model";
import {SaveDialogComponent} from "../../../common/dialog/save-dialog/save-dialog.component";
import {Observable} from "rxjs";
import {map, startWith} from 'rxjs/operators';
import {OrganizationModel} from "../../organization-list/organization/organization.model";
import {NationalBrokerConstants} from "../../../national-broker.constants";
import {SearchTableResult} from "../../../common/search-table/search-table-result.model";
import {v4 as uuidv4} from 'uuid';
import {DistributionModel} from "../dataset/distribution.model";
import {ExtendedHttpClient} from "../../../http/extended-http-client";

@Component({
  selector: 'dataset-details',
  templateUrl: './dataset-details-dialog.component.html',
  styleUrls: ['./dataset-details-dialog.component.scss']
})
export class DatasetDetailsDialogComponent implements OnInit {

  static readonly NEW_MODE = 'New DataSet';
  static readonly EDIT_MODE = 'DataSet Edit';

  @ViewChild('inputDistribution') inputDistribution: any;

  editMode: boolean;
  formTitle: string;
  showSpinner: boolean = false;
  dialogForm: FormGroup;
  current: DatasetModel;
  immediateRowManageAction: boolean = false;
  urlManage: string = "";
  autoFilter: Observable<OrganizationModel[]>;
  organizationList: OrganizationModel[] = [];


  constructor(public securityService: SecurityService,
              public dialog: MatDialog,
              protected http: ExtendedHttpClient,
              public dialogRef: MatDialogRef<DatasetDetailsDialogComponent>,
              private alertService: AlertService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private changeDetector: ChangeDetectorRef,
              private formBuilder: FormBuilder) {
    this.editMode = this.data.edit;
    this.updateOrganizationList();

    this.formTitle = this.editMode ? DatasetDetailsDialogComponent.EDIT_MODE : DatasetDetailsDialogComponent.NEW_MODE;

    this.current = this.editMode
      ? {
        ...data.row,
      } : {
        ...this.newDatasetRO
      };
    this.immediateRowManageAction = data.immediateRowManageAction;
    this.urlManage = data.urlManage;

    this.dialogForm = formBuilder.group({
      // dataset values
      'type': new FormControl({value: null, disabled: this.editMode}, Validators.required),
      'conformsTo': new FormControl({value: null}, []),
      'identifiers': new FormControl({value: null, disabled: this.editMode}, []),
      'titles': new FormControl({value: null}, []),
      'descriptions': new FormControl({value: null}, []),

      // publisher values
      'publisher.identifier': new FormControl({value: null, disabled: this.editMode}, Validators.required),
      'publisher.address.fullAddress': new FormControl({value: null, disabled: true}, []),
      'publisher.address.adminUnitLevel': new FormControl({value: null, disabled: true}, []),

      'publisher.prefLabels': new FormControl({value: null, disabled: true}, []),
      'publisher.altLabels': new FormControl({value: null, disabled: true}, []),
      'publisher.classifications': new FormControl({value: null, disabled: true}, []),

      'qualifiedRelationships': new FormControl({value: null}, []),
      'distributions': new FormControl({value: null, disabled: true}, []),


    });
    // bind values to form! not property
    this.dialogForm.controls['type'].setValue(this.current.type);
    this.dialogForm.controls['conformsTo'].setValue(this.current.conformsTo);
    this.dialogForm.controls['identifiers'].setValue(this.current.identifiers);
    this.dialogForm.controls['titles'].setValue(this.current.titles);
    this.dialogForm.controls['descriptions'].setValue(this.current.descriptions);
    // publisher values
    this.dialogForm.controls['publisher.identifier'].setValue(this.current.publisher.identifier);
    this.dialogForm.controls['publisher.address.fullAddress'].setValue(this.current.publisher.address.fullAddress);
    this.dialogForm.controls['publisher.address.adminUnitLevel'].setValue(this.current.publisher.address.adminUnitLevel);
    this.dialogForm.controls['publisher.prefLabels'].setValue(this.current.publisher.prefLabels);
    this.dialogForm.controls['publisher.altLabels'].setValue(this.current.publisher.altLabels);
    this.dialogForm.controls['publisher.classifications'].setValue(this.current.publisher.classifications);

    this.dialogForm.controls['qualifiedRelationships'].setValue([...this.current.qualifiedRelationships]);
    this.dialogForm.controls['distributions'].setValue([...this.current.distributions]);

    this.autoFilter = this.dialogForm.controls['publisher.identifier'].valueChanges.pipe(
      startWith(''),
      map(value => this.organizationFilter(value))
    );


  }

  ngOnInit() {
    this.changeDetector.detectChanges();
    this.inputDistribution.registerOnCreate(this.newDistributionRo);

  }

  private organizationFilter(value: string): OrganizationModel[] {
    console.log("Value:" + value + ", number of items: " + this.organizationList.length);

    const filterValue = value.toLowerCase();
    return this.organizationList.filter(option => option.identifier.toLowerCase().indexOf(filterValue) === 0);
  }

  onSelectOrganization(organization: OrganizationModel) {

    this.dialogForm.controls['publisher.identifier'].setValue(organization.identifier);
    this.dialogForm.controls['publisher.address.fullAddress'].setValue(organization.address?.fullAddress);
    this.dialogForm.controls['publisher.address.adminUnitLevel'].setValue(organization.address?.adminUnitLevel);
    this.dialogForm.controls['publisher.prefLabels'].setValue(organization.prefLabels);
    this.dialogForm.controls['publisher.altLabels'].setValue(organization.altLabels);
    this.dialogForm.controls['publisher.classifications'].setValue(organization.classifications);

  }

  private updateOrganizationList() {


    this.http.get<SearchTableResult>(this.organizationUrlQuery).subscribe((result: SearchTableResult) => {
      // empty page - probably refresh from delete...check if we can go one page back
      // try again
      if (result.count < 1) {
        this.organizationList = [];
      } else {

        this.organizationList = result.serviceEntities.map(serviceEntity => {
          return <OrganizationModel>{
            ...serviceEntity,
          }
        });
      }
    }, (error: any) => {
      this.alertService.error("Error occurred:" + error);
    });
  }

  get organizationUrlQuery(): string {
    return NationalBrokerConstants.REST_CONTEXT
      + NationalBrokerConstants.REST_USER_ORGANIZATIONS.replace("{USERNAME}", this.securityService.getCurrentUser()?.username);
  }

  public getCurrent(): DatasetModel {

    // change this two properties only on new
    if (this.current.status === SearchTableEntityStatus.NEW) {
      this.current.type = this.dialogForm.value['type'];
      this.current.identifiers = this.dialogForm.value['identifiers'];

      if (this.dialogForm.value['publisher.identifier'] !== '') {
        const orgList: OrganizationModel[] = this.organizationFilter(this.dialogForm.value['publisher.identifier']);
        if (orgList.length > 0)
          this.current.publisher = orgList[0];
      }
    }
    this.current.conformsTo = this.dialogForm.value['conformsTo'];
    this.current.titles = this.dialogForm.value['titles'];
    this.current.descriptions = this.dialogForm.value['descriptions'];
    this.current.qualifiedRelationships = this.dialogForm.value['qualifiedRelationships'];
    return this.current;
  }


  get newDatasetRO(): DatasetModel {
    return {
      type: uuidv4(),
      conformsTo: 'https://semantic-repository.toop.eu/ontology',
      identifiers: [],
      titles: ['Dataset title'],
      descriptions: ['Dataset description'],
      publisher: {
        status: SearchTableEntityStatus.NEW,
        identifier: '',
        prefLabels: [],
        altLabels: [],
        classifications: [],
        address: this.newAddressRo,

      },
      qualifiedRelationships: [{
        relation: 'urn:oasis:names:tc:ebcore:partyid-type:iso6523:9999:0000000005',
        hadRole: 'https://toop.eu/dataset/supportedIdScheme'
      }],
      distributions: [this.newDistributionRo()],
      status: SearchTableEntityStatus.NEW,
    };
  }

   newDistributionRo(): DistributionModel {
    return {
      descriptions: [],
      conformsTo: 'RegRepv4-EDMv2',
      format: 'UNSTRUCTURED',
      mediaType: 'application/pdf',
      accessURL: 'http://' + uuidv4() + '.eu/test',
      dataServices: [{
        identifier:  uuidv4(),
        conformsTo: 'toop-doctypeid-qns::urn:eu:toop:ns:dataexchange-2::Request##urn:eu.toop.request.example::2.0',
        title: 'Data service example title',
        endpointURL: 'https://smp.bonn.toop.eu/9999::0000000001/services/example?type=pdf'
      }]
    }
  }

  get newAddressRo(): OrganizationAddress {
    return {
      adminUnitLevel: 'DE',
      fullAddress: null,
    }
  }

  submitForm() {
    console.log("submit form")
    this.checkValidity(this.dialogForm);
    if (this.immediateRowManageAction) {
      console.log("immediateRowManageAction")
      if (this.editMode) {
        this.fireRecordUpdate();
      } else {
        this.fireRecordAdd();
      }

    } else {
      console.log("just close")
      this.dialogRef.close(true);
    }
  }

  fireRecordAdd() {
    try {
      this.dialog.open(SaveDialogComponent).afterClosed().subscribe(result => {
        if (result) {
          // this.unselectRows();
          this.showSpinner = true;
          let record = this.getCurrent();

          this.http.post(this.urlManage, record).toPromise().then(res => {
            this.showSpinner = false;
            // this.isBusy = false;
            // this.getUsers();
            this.alertService.success('The operation \'update\' completed successfully.', false);
            // update status to modified
            /*
            this.current.updateRequestOn="Submitting update"
            this.current.updateRequests.unshift(  { dsdStatus:null,
              dsdMessage:null,
              updateRequestId:"Submitting update",
              updateResponseId:null,
              updateRequestOn:null,
              updateConfirmedOn:null,
              username:this.securityService.getCurrentUser()?.username,
              organizationIdentifier:null,})

             */
            this.current.status = SearchTableEntityStatus.PERSISTED;
            this.dialogRef.close(true);
          }, err => {
            this.showSpinner = false;
            try {
              console.log("error: " + err)
              let parser = new DOMParser();
              let xmlDoc = parser.parseFromString(err.error, "text/xml");
              let errDesc = xmlDoc.getElementsByTagName("ErrorDescription")[0].childNodes[0].nodeValue;
              this.alertService.exception('The operation \'add\' not completed successfully.', errDesc, false);
            } catch (err2) {
              // if parse failed
              this.alertService.exception('The operation \'add\' not completed successfully.', err, false);
            }
          });
        } else {
          this.showSpinner = false;

        }
      });
    } catch (err) {
      // this.isBusy = false;
      this.showSpinner = false;
      this.alertService.exception('The operation \'add\' completed with errors.', err);
    }
  }

  fireRecordUpdate() {
    try {
      this.dialog.open(SaveDialogComponent).afterClosed().subscribe(result => {
        if (result) {
          // this.unselectRows();
          this.showSpinner = true;
          let record = this.getCurrent();

          this.http.put(this.urlManage + "/" + record.identifiers[0], record).toPromise().then(res => {
            this.showSpinner = false;
            // this.isBusy = false;
            // this.getUsers();
            this.alertService.success('The operation \'update\' completed successfully.', false);
            // update status to modified
            /*
            this.current.updateRequestOn="Submitting update"
            this.current.updateRequests.unshift(  { dsdStatus:null,
              dsdMessage:null,
              updateRequestId:"Submitting update",
              updateResponseId:null,
              updateRequestOn:null,
              updateConfirmedOn:null,
              username:this.securityService.getCurrentUser()?.username,
              organizationIdentifier:null,})

             */
            this.dialogRef.close(true);
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

        }
      });
    } catch (err) {
      // this.isBusy = false;
      this.showSpinner = false;
      this.alertService.exception('The operation \'update\' completed with errors.', err);
    }
  }


  checkValidity(g: FormGroup) {
    Object.keys(g.controls).forEach(key => {
      if (g.contains(key) && g.get(key) !== null) {
        g.get(key).markAsDirty();
      }

    });
    Object.keys(g.controls).forEach(key => {
      if (g.contains(key) && g.get(key) !== null) {
        g.get(key).markAsTouched();
      }
    });
    //!!! updateValueAndValidity - else some filed did no update current / on blur never happened
    Object.keys(g.controls).forEach(key => {
      if (g.contains(key) && g.get(key) !== null) {
        g.get(key).updateValueAndValidity();
      }
    });
  }


  compareTableItemById(item1, item2): boolean {
    return item1.id === item2.id;
  }


  isEqual(val1, val2): boolean {
    return (this.isEmpty(val1) && this.isEmpty(val2)
      || val1 === val2);
  }

  isEmpty(str): boolean {
    return (!str || 0 === str.length);
  }


  addListItem(list: string[], event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    // Add our fruit
    if (value) {
      list.push(value);
    }

    // Clear the input value
    event.chipInput!.clear();
  }

  removeListItem(list: string[], item: string): void {
    const index = list.indexOf(item);

    if (index >= 0) {
      list.splice(index, 1);
    }
  }
}
