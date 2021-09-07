import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {RestApiUser} from './user.model';
import {SecurityEventService} from './security-event.service';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {NationalBrokerConstants} from "../national-broker.constants";
import {Authority} from "./authority.model";
import {AlertService} from "../alert/alert.service";
import {AuthService} from "@auth0/auth0-angular";
import {LogoutOptions, User} from "@auth0/auth0-spa-js";
import {NationalBrokerToken} from "./token.mode";


@Injectable()
export class SecurityService {

  readonly LOCAL_STORAGE_KEY_CURRENT_USER = 'currentUser';

  constructor(
    private http: HttpClient,
    private alertService: AlertService,
    private securityEventService: SecurityEventService,
    public authService: AuthService
  ) {
    this.securityEventService.onLogoutSuccessEvent().subscribe(() => window.location.reload());
    this.securityEventService.onLogoutErrorEvent().subscribe((error) => this.alertService.error(error));

    /*
    // this is just for test purpose. Login user
    let userDetails: RestApiUser = {
      id: 1,
      username: "ADMIN_USER",
      name: "Admin User",
      active: true,
      email: "admin.user@isa2.eu",
      authorities: [Authority.USER, Authority.ADMIN],
      defaultPasswordUsed: false
    };
    this.updateUserDetails(userDetails);
    */

  }

  loginWithPopup() {
    // first get user
    // then get token from IDP
    // then get access_token from NationalBroker
    // then store user details
    this.authService.loginWithPopup().subscribe(() => {
        console.log("The user is logged in");
        this.authService.user$.subscribe((oAuthuser: User) => {
            console.log("got user: " + JSON.stringify(oAuthuser));
            this.getIdToken().subscribe(token => {
                console.log("get National token: " + token);
                let params: HttpParams = new HttpParams();
                params = params.set("grant_type", encodeURIComponent("urn:ietf:params:oauth:grant-type:jwt-bearer"));
                params = params.set("assertion", token.__raw);
                this.http.post<NationalBrokerToken>(this.nationalBrokerTokenUrl, params).subscribe(
                  value => {
                    console.log("get national broker token: " + JSON.stringify(value));
                    let userDetails: RestApiUser = {
                      id: 0,
                      active: true,
                      username: oAuthuser.name,
                      name: oAuthuser.name,
                      email: oAuthuser.email,
                      authorities: [Authority.USER, Authority.ADMIN],
                      defaultPasswordUsed: false,
                      idToken: value
                    };
                    this.updateUserDetails(userDetails);
                  },
                  (error: any) => {
                    this.securityEventService.notifyLoginErrorEvent(error);
                  });
              },
              (error: any) => {
                this.securityEventService.notifyLoginErrorEvent(error);
              });
          },
          (error: any) => {
            this.securityEventService.notifyLoginErrorEvent(error);
          });

      },
      (error: any) => {
        this.securityEventService.notifyLoginErrorEvent(error);
      });
  }

  getIdToken(): Observable<import("@auth0/auth0-spa-js").IdToken> {
    return this.authService.idTokenClaims$;
  }


  login(username: string, password: string) {
    let headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post<string>(NationalBrokerConstants.REST_SECURITY_AUTHENTICATION,
      JSON.stringify({
        username: username,
        password: password
      }),
      {headers})
      .subscribe((response: string) => {
          this.updateUserDetails(response);
        },
        (error: any) => {
          this.securityEventService.notifyLoginErrorEvent(error);
        });
  }

  logout(options?: LogoutOptions) {
    this.authService.logout(options);
    this.clearLocalStorage();
    //this.securityEventService.notifyLogoutSuccessEvent(null);


  }

  getCurrentUser(): RestApiUser {
    return JSON.parse(this.readLocalStorage());
  }

  private getCurrentUsernameFromServer(): Observable<string> {
    let subject = new ReplaySubject<string>();
    this.http.get<string>(NationalBrokerConstants.REST_SECURITY_USER)
      .subscribe((res: string) => {
        subject.next(res);
      }, (error: any) => {
        //console.log('getCurrentUsernameFromServer:' + error);
        subject.next(null);
      });
    return subject.asObservable();
  }

  get isAuthenticated(): Observable<boolean> {
    return this.authService.isAuthenticated$;
  }

  hasRoleAdmin(): boolean {
    return this.isCurrentUserInRole([Authority.ADMIN]);
  }

  hasRoleUser(): boolean {
    return this.isCurrentUserInRole([Authority.USER]);
  }

  isCurrentUserInRole(roles: Array<Authority>): boolean {

    let hasRole = false;
    const currentUser = this.getCurrentUser();
    if (currentUser && currentUser.authorities) {
      roles.forEach((role: Authority) => {
        if (currentUser.authorities.indexOf(role) !== -1) {
          hasRole = true;
        }
      });
    }
    return hasRole;
  }

  isAuthorized(roles: Array<Authority>): Observable<boolean> {
    let subject = new ReplaySubject<boolean>();

    this.isAuthenticated.subscribe((isAuthenticated: boolean) => {
      if (isAuthenticated && roles) {
        let hasRole = this.isCurrentUserInRole(roles);
        subject.next(hasRole);
      }
    });
    return subject.asObservable();
  }

  updateUserDetails(userDetails) {
    this.populateLocalStorage(JSON.stringify(userDetails));
    this.securityEventService.notifyLoginSuccessEvent(userDetails);
  }

  private populateLocalStorage(userDetails: string) {
    localStorage.setItem(this.LOCAL_STORAGE_KEY_CURRENT_USER, userDetails);
  }

  private readLocalStorage(): string {
    return localStorage.getItem(this.LOCAL_STORAGE_KEY_CURRENT_USER);
  }

  private clearLocalStorage() {
    localStorage.removeItem(this.LOCAL_STORAGE_KEY_CURRENT_USER);
  }

  get nationalBrokerTokenUrl(): string {
    return NationalBrokerConstants.REST_CONTEXT
      + NationalBrokerConstants.REST_OATH_TOKEN;
  }

}
