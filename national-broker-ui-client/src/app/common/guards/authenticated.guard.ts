import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {SecurityService} from '../../security/security.service';
import {ReplaySubject} from 'rxjs';

@Injectable()
export class AuthenticatedGuard implements CanActivate {

  constructor(private router: Router, private securityService: SecurityService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const subject = new ReplaySubject<boolean>();
    this.securityService.isAuthenticated.subscribe((isAuthenticated: boolean) => {
      if(isAuthenticated) {
        console.log("is logged");
        subject.next(true);
      } else {
        // not logged in so redirect to login page with the return urlQuery
        this.router.navigate(['/login'], {queryParams: {returnUrl: state.url}});
        subject.next(false);
      }
    });
    return subject.asObservable();

  }
}
