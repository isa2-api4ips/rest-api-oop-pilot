import {ChangeDetectorRef, Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {AlertService} from "../../../alert/alert.service";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {SecurityService} from "../../../security/security.service";
import {DsdRequestModel} from "../dsd-request/dsd-request.model";
import {Observable} from "rxjs";
import {NationalBrokerConstants} from "../../../national-broker.constants";
import {MatTabChangeEvent} from "@angular/material/tabs";
import {SignatureDetailsDialogComponent} from "../signature-details-dialog/signature-details-dialog.component";
import {ExtendedHttpClient} from "../../../http/extended-http-client";


@Component({
  selector: 'dsd-request-details-dialog',
  templateUrl: './dsd-request-details-dialog.component.html',
  styleUrls: ['./dsd-request-details-dialog.component.scss']
})
export class DsdRequestDetailsDialogComponent implements OnInit {

  requestHeaderOpen = false;
  requestBodyOpen = true;
  responseHeaderOpen = false;
  responseBodyOpen = true;

  editMode: boolean;
  formTitle: string;

  dialogForm: FormGroup;
  current: DsdRequestModel;
  dsdRequestModelObserver: Observable<DsdRequestModel>;
  urlManage: string = "";


  constructor(public securityService: SecurityService,
              public dialog: MatDialog,
              protected http: ExtendedHttpClient,
              public dialogRef: MatDialogRef<DsdRequestDetailsDialogComponent>,
              private alertService: AlertService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private changeDetector: ChangeDetectorRef,
              private formBuilder: FormBuilder) {
    this.editMode = this.data.edit;
    this.formTitle = "DSD Request details";

    this.current = {
      ...data.row,
    };
    this.dialogForm = formBuilder.group({
      // common values
      'messageId': new FormControl({value: null, disabled: true}, Validators.required),
      'responseMessageId': new FormControl({value: null}, []),
      'requestOn': new FormControl({value: null}, []),
      'responseOn': new FormControl({value: null}, []),
      'dsdStatus': new FormControl({value: null}, []),
      'requestStoragePath': new FormControl({value: null}, []),
      'responseStoragePath': new FormControl({value: null}, []),
      'requestBody': new FormControl({value: null}, []),
      'responseBody': new FormControl({value: null}, []),
      'httpMethod': new FormControl({value: null}, []),
      'httpPath': new FormControl({value: null}, []),

      'requestHeaders': new FormControl({value: null}, []),
      'responseHeaders': new FormControl({value: null}, []),

    });
    this.dialogForm.controls['messageId'].setValue(this.current?.messageId);
    this.dialogForm.controls['responseMessageId'].setValue(this.current?.responseMessageId);
    this.dialogForm.controls['requestOn'].setValue(this.current?.requestOn);
    this.dialogForm.controls['responseOn'].setValue(this.current?.responseOn);
    this.dialogForm.controls['dsdStatus'].setValue(this.current?.dsdStatus);
    this.dialogForm.controls['requestStoragePath'].setValue(this.current?.requestStoragePath);
    this.dialogForm.controls['responseStoragePath'].setValue(this.current?.responseStoragePath);
    this.dialogForm.controls['httpMethod'].setValue(this.current?.httpMethod);
    this.dialogForm.controls['httpPath'].setValue(this.current?.httpPath);
    this.dialogForm.controls['requestBody'].setValue('');
    this.dialogForm.controls['responseBody'].setValue('');
    this.dialogForm.controls['requestHeaders'].setValue([]);
    this.dialogForm.controls['responseHeaders'].setValue([]);

    /*

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
    */
  }

  ngOnInit() {
    // retrieve xml extension for this service group

    if (this.current) {
      // init domains
      this.dsdRequestModelObserver = this.http.get<DsdRequestModel>(this.messageUrlQuery);
      this.dsdRequestModelObserver.subscribe((res: DsdRequestModel) => {

        // store init xml to current value for change validation
        this.current.requestMessage = res.requestMessage;
        this.current.responseMessage = res.responseMessage;

        this.dialogForm.controls['requestBody'].setValue(res.requestMessage?.body);
        this.dialogForm.controls['responseBody'].setValue(res.responseMessage?.body);
        // copy to array of objects with properties name and value
        this.dialogForm.controls['requestHeaders'].setValue(Object.entries(res.requestMessage?.headers)
          .map(([k, v]) => ({"name": k, "value": v})));

        this.dialogForm.controls['responseHeaders'].setValue(Object.entries(res.responseMessage?.headers)
          .map(([k, v]) => ({"name": k, "value": v})));

      });
    }

    // detect changes for updated values in mat-selection-list (check change detection operations)
    // else the following error is thrown :xpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:
    // 'aria-selected: false'. Current value: 'aria-selected: true'
    //
    this.changeDetector.detectChanges()
  }

  get messageUrlQuery(): string {
    return NationalBrokerConstants.REST_CONTEXT
      + NationalBrokerConstants.REST_USER_LOG_GET_MESSAGE
        .replace("{USERNAME}", this.securityService.getCurrentUser()?.username)
        .replace("{id}", this.current?.id + "");
  }

  tabChanged(tabChangeEvent: MatTabChangeEvent): void {

    //force refresh
    if (tabChangeEvent.index === 0) {
      this.dialogForm.controls['requestBody'].setValue('')
      this.dialogForm.controls['requestBody'].setValue(this.current.requestMessage?.body);
    } else {
      this.dialogForm.controls['responseBody'].setValue('');
      this.dialogForm.controls['responseBody'].setValue(this.current.responseMessage?.body);
    }
  }

  showSignature(row) {
    this.dialog.open(SignatureDetailsDialogComponent, {
      data: {signature: {...row}}
    });
  }

  downloadMessageType(type: string) {
    let url = NationalBrokerConstants.REST_CONTEXT
      + NationalBrokerConstants.REST_USER_LOG_MESSAGE_DOWNLOAD
        .replace("{USERNAME}", this.securityService.getCurrentUser()?.username)
        .replace("{id}", this.current?.id + "")
        .replace("{type}", type);
    window.open(url, "_blank");
  }

  validateSignature(row) {
    if (row) {
      // init domains
      this.dsdRequestModelObserver = this.http.get<DsdRequestModel>(this.messageUrlQuery);
      this.dsdRequestModelObserver.subscribe((res: DsdRequestModel) => {

        // store init xml to current value for change validation
        this.current.requestMessage = res.requestMessage;
        this.current.responseMessage = res.responseMessage;

        this.dialogForm.controls['requestBody'].setValue(res.requestMessage?.body);
        this.dialogForm.controls['responseBody'].setValue(res.responseMessage?.body);
        // copy to array of objects with properties name and value
        this.dialogForm.controls['requestHeaders'].setValue(Object.entries(res.requestMessage?.headers)
          .map(([k, v]) => ({"name": k, "value": v})));

        this.dialogForm.controls['responseHeaders'].setValue(Object.entries(res.responseMessage?.headers)
          .map(([k, v]) => ({"name": k, "value": v})));

      });
    }
  }


}
