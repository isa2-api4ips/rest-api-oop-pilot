import {ChangeDetectorRef, Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {AlertService} from "../../../alert/alert.service";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {SecurityService} from "../../../security/security.service";
import {ExtendedHttpClient} from "../../../http/extended-http-client";


@Component({
  selector: 'signature-details',
  templateUrl: './signature-details-dialog.component.html',
  styleUrls: ['./signature-details-dialog.component.scss']
})
export class SignatureDetailsDialogComponent implements OnInit {

  formTitle: string = "Signature details";
  dialogForm: FormGroup;

  signature: any;



  constructor(public securityService: SecurityService,
              public dialog: MatDialog,
              protected http: ExtendedHttpClient,
              public dialogRef: MatDialogRef<SignatureDetailsDialogComponent>,
              private alertService: AlertService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private changeDetector: ChangeDetectorRef,
              private formBuilder: FormBuilder) {

    this.signature = {...data.signature};


    this.dialogForm = formBuilder.group({
      // common values
      'signature': new FormControl({value:  null, disabled:true},  Validators.required ),
      'headers': new FormControl({value: null}, []),
      'payload': new FormControl({value: null}, []),
      'signatureData': new FormControl({value: null}, []),



    });
    // bind values to form! not property
    this.dialogForm.controls['signature'].setValue(this.signature.value);
    let jwtArray = this.signature.value.replace("Bearer ","").split('.');
    if (jwtArray.length!==3){
      console.log("Invalid JWT token!")
    }
    let decHeaders:string = this.base64URLDecode(jwtArray[0]);
    let prettyPrintHeaders  =  this.prettyPrintJson(JSON.parse(decHeaders));
    this.dialogForm.controls['headers'].setValue(prettyPrintHeaders);
    this.dialogForm.controls['payload'].setValue("");
    if (jwtArray[1].length >0){
      let decPayload:string = this.base64URLDecode(jwtArray[1]);
      let prettyPrintPayload  =  this.prettyPrintJson(JSON.parse(decPayload));
      this.dialogForm.controls['payload'].setValue(prettyPrintPayload);
    }

    //let decSigData:string = atob(jwtArray[2]);
    //let prettyPrintSigData  =  this.prettyPrintJson(JSON.parse(decSigData));
    this.dialogForm.controls['signatureData'].setValue(jwtArray[2]);
  }


  ngOnInit() {


    // detect changes for updated values in mat-selection-list (check change detection operations)
    // else the following error is thrown :xpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:
    // 'aria-selected: false'. Current value: 'aria-selected: true'
    //
    this.changeDetector.detectChanges()
  }

  prettyPrintJson(json:string):string {
    return JSON ? JSON.stringify(json, null, 2) : json;

  }

  base64URLDecode(encoded:string){
    return atob(encoded.replace('_','/')
      .replace('-','+'));
  }

}
