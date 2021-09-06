import {Injectable} from '@angular/core';
import {NationalBrokerConstants} from "../national-broker.constants";
import {RestApiUser} from "../security/user.model";
import {AlertService} from "../alert/alert.service";
import {SecurityService} from "../security/security.service";
import {ExtendedHttpClient} from "../http/extended-http-client";

@Injectable()
export class UserService {

  constructor(
    private http: ExtendedHttpClient,
    private securityService: SecurityService,
    private alertService: AlertService,
  ) { }

  updateUser(user: RestApiUser) {
    this.http.put<string>(`${NationalBrokerConstants.REST_USER}/${user.id}`, user).subscribe(response => {
      this.securityService.updateUserDetails(response);
      this.alertService.success('The operation \'update user\' completed successfully.');
    }, err => {
      this.alertService.exception('The operation \'update user\' not completed successfully.', err);
    });
  }
}
