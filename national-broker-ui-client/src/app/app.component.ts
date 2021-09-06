import {Component, Inject} from '@angular/core';
import {Router} from "@angular/router";
import {AlertService} from "./alert/alert.service";
import {SecurityService} from "./security/security.service";
import {HttpClient} from "@angular/common/http";
import {MatDialog} from "@angular/material/dialog";
import {UserService} from "./user/user.service";
import {Authority} from "./security/authority.model";
import {DOCUMENT} from "@angular/common";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'national-broker-ui-client';
  fullMenu: boolean = true;
  menuClass: string = this.fullMenu ? "menu-expanded" : "menu-collapsed";

  constructor(
    @Inject(DOCUMENT) public document: Document,
    private alertService: AlertService,
    public securityService: SecurityService,
    private router: Router,
    private http: HttpClient,
    private dialog: MatDialog,
    private userService: UserService,
  ) {
    //this.userController = new UserController(this.http, this.lookups, this.dialog);
  }

  isCurrentUserAdmin(): boolean {
    return this.securityService.isCurrentUserInRole([Authority.ADMIN]);

  }

  isCurrentUserUser(): boolean {
    return this.securityService.isCurrentUserInRole([Authority.USER]);

  }


  toggleMenu() {
    this.fullMenu = !this.fullMenu;
    this.menuClass = this.fullMenu ? "menu-expanded" : "menu-collapsed";
    setTimeout(() => {
      var evt = document.createEvent("HTMLEvents");
      evt.initEvent('resize', true, false);
      window.dispatchEvent(evt);
    }, 500)
    //ugly hack but otherwise the ng-datatable doesn't resize when collapsing the menu
    //alternatively this can be tried (https://github.com/swimlane/ngx-datatable/issues/193) but one has to implement it on every page
    //containing a ng-datatable and it only works after one clicks inside the table
  }

  get currentUser(): string {
    let user = this.securityService.getCurrentUser();
    return user ? user.username : "";
  }

  get currentUserRoleDescription(): string {

    if (this.securityService.hasRoleAdmin()) {
      return "Administrator";
    } else if (this.securityService.hasRoleUser()) {
      return "User";
    }
    return "";
  }

  logout(event: Event): void {
    event.preventDefault();

    this.router.navigate(['/login']).then((ok) => {
      if (ok) {
        this.securityService.logout();
      }
    });

  }

  editCurrentUser() {
    /*
    const formRef: MatDialogRef<any> = this.userController.newDialog({
      data: {mode: UserDetailsDialogMode.PREFERENCES_MODE, row: this.securityService.getCurrentUser()}
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        const user = {...formRef.componentInstance.getCurrent(), status: SearchTableEntityStatus.UPDATED};
        this.userService.updateUser(user);
      }
    });*/
  }

  clearWarning() {
    this.alertService.clearAlert();
  }
}
