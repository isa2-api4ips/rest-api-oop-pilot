import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {OrganizationListComponent} from "./dsd/organization-list/organization-list.component";
import {UserListComponent} from "./admin/users-list/user-list.component";
import {DatasetListComponent} from "./dsd/dataset-list/dataset-list.component";
import {DsdRequestListComponent} from "./dsd/dsd-request-list/dsd-request-list.component";
import {DsdDataUpdateListComponent} from "./dsd/dsd-data-update-list/dsd-data-update-list.component";
import {AuthenticatedGuard} from "./common/guards/authenticated.guard";


const routes: Routes = [

  {path: '', component: OrganizationListComponent, canActivate: [AuthenticatedGuard]},
  {path: 'login', component: LoginComponent},
  {path: 'dsd/organizations', redirectTo: ""},
  {path: 'admin/users', component: UserListComponent,  canActivate: [AuthenticatedGuard]},
  {path: 'dsd/dsd-data-updates', component: DsdDataUpdateListComponent,  canActivate: [AuthenticatedGuard]},
  {path: 'dsd/datasets/:organizationIdentifier', component: DatasetListComponent,  canActivate: [AuthenticatedGuard]},
  {path: 'dsd/dsd-request-list/:messageIdentifier', component: DsdRequestListComponent,  canActivate: [AuthenticatedGuard]},
  {path: '**', redirectTo: 'dsd/organizations'},


];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }


