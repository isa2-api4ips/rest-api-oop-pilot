import {Component} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-save-dialog',
  templateUrl: './save-dialog.component.html',
})
export class SaveDialogComponent {

  constructor(public dialogRef: MatDialogRef<SaveDialogComponent>) {
  }

}

