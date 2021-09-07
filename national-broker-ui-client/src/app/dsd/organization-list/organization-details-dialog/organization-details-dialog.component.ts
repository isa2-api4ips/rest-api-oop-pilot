import {ChangeDetectorRef, Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {AlertService} from "../../../alert/alert.service";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {SecurityService} from "../../../security/security.service";
import {OrganizationModel} from "../organization/organization.model";
import {SearchTableEntityStatus} from "../../../common/search-table/search-table-entity-status.model";
import {OrganizationAddress} from "../organization/organization-address.model";
import {SaveDialogComponent} from "../../../common/dialog/save-dialog/save-dialog.component";
import {MatChipInputEvent} from "@angular/material/chips";
import {ExtendedHttpClient} from "../../../http/extended-http-client";


@Component({
  selector: 'organization-details',
  templateUrl: './organization-details-dialog.component.html',
  styleUrls: ['./organization-details-dialog.component.scss']
})
export class OrganizationDetailsDialogComponent implements OnInit {

  static readonly NEW_MODE = 'New organization';
  static readonly EDIT_MODE = 'Organization Edit';

  editMode: boolean;
  formTitle: string;

  showSpinner: boolean = false;

  dialogForm: FormGroup;

  current: OrganizationModel;
  immediateRowManageAction: boolean = false;
  urlManage: string ="";


  constructor(public securityService: SecurityService,
              public dialog: MatDialog,
              protected http: ExtendedHttpClient,
              public dialogRef: MatDialogRef<OrganizationDetailsDialogComponent>,
              private alertService: AlertService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private changeDetector: ChangeDetectorRef,
              private formBuilder: FormBuilder) {
    this.editMode = this.data.edit;

    this.formTitle = this.editMode ? OrganizationDetailsDialogComponent.EDIT_MODE : OrganizationDetailsDialogComponent.NEW_MODE;

    this.current = this.editMode
      ? {
        ...data.row,
        // clone also the address!
        address: data.row.address? {...data.row.address}  : this.newAddressRo(),
        prefLabels: [...data.row.prefLabels],
        altLabels: [...data.row.altLabels],
        classifications: [...data.row.classifications]
      } : {
        identifier: '',
        prefLabels: [],
        altLabels: [],
        classification: [],
        status: SearchTableEntityStatus.NEW,
        address: this.newAddressRo(),

      };
    this.immediateRowManageAction = data.immediateRowManageAction;
    this.urlManage = data.urlManage;

    this.dialogForm = formBuilder.group({
      // common values
      'identifier': new FormControl({value:  null, disabled:true},  Validators.required ),
      'address.fullAddress': new FormControl({value: null}, []),
      'address.adminUnitLevel': new FormControl({value: null, disabled:true}, []),

      'prefLabels': new FormControl({value: null}, []),
      'altLabels': new FormControl({value: null}, []),
      'classifications': new FormControl({value: null, disabled:true}, []),


    });
    // bind values to form! not property
    this.dialogForm.controls['identifier'].setValue(this.current.identifier);
    this.dialogForm.controls['address.fullAddress'].setValue(this.current.address.fullAddress);
    this.dialogForm.controls['address.adminUnitLevel'].setValue(this.current.address.adminUnitLevel);
    this.dialogForm.controls['prefLabels'].setValue(this.current.prefLabels);
    this.dialogForm.controls['altLabels'].setValue(this.current.altLabels);
    this.dialogForm.controls['classifications'].setValue(this.current.classifications);

  }

  ngOnInit() {


    // detect changes for updated values in mat-selection-list (check change detection operations)
    // else the following error is thrown :xpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:
    // 'aria-selected: false'. Current value: 'aria-selected: true'
    //
    this.changeDetector.detectChanges()
  }
  private newAddressRo(): OrganizationAddress {
    return {
      adminUnitLevel: 'DE',
      fullAddress: null,
    }
  }

  submitForm() {
    this.checkValidity(this.dialogForm);
    if(this.immediateRowManageAction){
      this.fireRecordUpdate();
    } else {
      this.dialogRef.close(true);
    }
  }

  fireRecordUpdate(){
    try {
      this.dialog.open(SaveDialogComponent).afterClosed().subscribe(result => {
        if (result) {
          // this.unselectRows();
          this.showSpinner = true;
          let record = this.getCurrent();

          this.http.put(this.urlManage + "/" + record.identifier, record).toPromise().then(res => {
            this.showSpinner = false;
            // this.isBusy = false;
            // this.getUsers();
            this.alertService.success('The operation \'update\' completed successfully.', false);
            // update status to modified
            this.current.updateRequestOn="Submitting update"
            this.current.updateRequests.unshift(  { dsdStatus:null,

            dsdMessage:null,
            updateRequestId:"Submitting update",
            updateResponseId:null,
            updateRequestOn:null,
            updateConfirmedOn:null,
            username:this.securityService.getCurrentUser()?.username,
            organizationIdentifier:null,
            service:null,
            action:null,
            entityType:null})
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


  public getCurrent(): OrganizationModel {

    // change this two properties only on new
    if (this.current.status === SearchTableEntityStatus.NEW) {
      this.current.identifier = this.dialogForm.value['identifier'];
      this.current.classifications = this.dialogForm.value['classifications'];
      this.current.address.adminUnitLevel = this.dialogForm.value['address.adminUnitLevel'];

    }
    this.current.altLabels = this.dialogForm.value['altLabels'];
    this.current.prefLabels = this.dialogForm.value['prefLabels'];
    this.current.address.fullAddress = this.dialogForm.value['address.fullAddress'];



    return this.current;
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
