import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatDialogModule} from '@angular/material/dialog';
import {MatTableModule} from '@angular/material/table';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from "@angular/material/card";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatMenuModule} from "@angular/material/menu";
import {MatIconModule} from "@angular/material/icon";
import {MatListModule} from "@angular/material/list";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatSelectModule} from "@angular/material/select";
import {MatTabsModule} from "@angular/material/tabs";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {MatChipsModule} from "@angular/material/chips";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {OrganizationListComponent} from "./dsd/organization-list/organization-list.component";
import {OrganizationDetailsDialogComponent} from "./dsd/organization-list/organization-details-dialog/organization-details-dialog.component";
import {LoginComponent} from "./login/login.component";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {HttpEventService} from "./http/http-event.service";
import {AlertService} from "./alert/alert.service";
import {SecurityService} from "./security/security.service";
import {SecurityEventService} from "./security/security-event.service";
import {UserService} from "./user/user.service";
import {RowLimiterComponent} from "./common/row-limiter/row-limiter.component";
import {ColumnPickerComponent} from "./common/column-picker/column-picker.component";
import {CancelDialogComponent} from "./common/dialog/cancel-dialog/cancel-dialog.component";
import {SaveDialogComponent} from "./common/dialog/save-dialog/save-dialog.component";
import {ConfirmationDialogComponent} from "./common/dialog/confirmation-dialog/confirmation-dialog.component";
import {DownloadService} from "./common/download/download.service";
import {SearchTableComponent} from "./common/search-table/search-table.component";
import {DialogComponent} from "./common/dialog/dialog.component";
import {NgxDatatableModule} from "@swimlane/ngx-datatable";
import {ExtendedHttpClient} from "./http/extended-http-client";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {UserListComponent} from './admin/users-list/user-list.component';
import {DemoComponent} from './demo/demo.component';
import {DatasetListComponent} from "./dsd/dataset-list/dataset-list.component";
import {DatasetDetailsDialogComponent} from "./dsd/dataset-list/dataset-details-dialog/dataset-details-dialog.component";
import {EditableListComponent} from "./common/editable-list/editable-list.component";
import {EditableListDialogComponent} from "./common/editable-list/editable-list-dialog/editable-list-dialog.component";
import {DetailsListComponent} from "./common/details-list/details-list.component";
import {DsdRequestListComponent} from "./dsd/dsd-request-list/dsd-request-list.component";
import {DsdRequestDetailsDialogComponent} from "./dsd/dsd-request-list/dsd-request-details-dialog/dsd-request-details-dialog.component";
import {CodemirrorModule} from "@ctrl/ngx-codemirror";
import {SignatureDetailsDialogComponent} from "./dsd/dsd-request-list/signature-details-dialog/signature-details-dialog.component";
import {DsdDataUpdateListComponent} from "./dsd/dsd-data-update-list/dsd-data-update-list.component";
import {AlertComponent} from "./alert/alert.component";
import {AuthenticatedGuard} from "./common/guards/authenticated.guard";
import {DirtyGuard} from "./common/guards/dirty.guard";
// Import the module from the SDK
// Import the HTTP interceptor from the Auth0 Angular SDK
import {AuthHttpInterceptor, AuthModule} from '@auth0/auth0-angular';

@NgModule({
  declarations: [
    AppComponent,
    DialogComponent,
    CancelDialogComponent,
    ColumnPickerComponent,
    ConfirmationDialogComponent,
    LoginComponent,
    OrganizationListComponent,
    OrganizationDetailsDialogComponent,
    DsdDataUpdateListComponent,
    DatasetListComponent,
    DatasetDetailsDialogComponent,
    DsdRequestListComponent,
    DsdRequestDetailsDialogComponent,
    RowLimiterComponent,
    SaveDialogComponent,
    SearchTableComponent,
    EditableListComponent,
    EditableListDialogComponent,
    DetailsListComponent,
    UserListComponent,
    DemoComponent,
    SignatureDetailsDialogComponent,
    AlertComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    CodemirrorModule,
    FormsModule,
    ReactiveFormsModule,
    NgxDatatableModule,
    MatAutocompleteModule,
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatDatepickerModule,
    MatDialogModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatTableModule,
    MatTooltipModule,
    MatToolbarModule,
    MatMenuModule,
    MatInputModule,
    MatIconModule,
    MatListModule,
    MatSidenavModule,
    MatSelectModule,
    MatTabsModule,
    MatSlideToggleModule,
    MatProgressSpinnerModule,
    HttpClientModule,
    AuthModule.forRoot({
      domain: 'national-broker-poc.eu.auth0.com',
      clientId: '6WfybVUUWhj6bOaXPW7RPRFTR2zHbiLr',
    }),
  ],
  providers: [
    AuthenticatedGuard,
    DirtyGuard,
    HttpEventService,
    ExtendedHttpClient,
    AlertService,
    SecurityService,
    SecurityEventService,
    UserService,
    DownloadService,
    {provide: HTTP_INTERCEPTORS, useClass: AuthHttpInterceptor, multi: true},
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
