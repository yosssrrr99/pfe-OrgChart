import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { LoginService } from './login.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private loginService: LoginService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.loginService.isLoggedIn().pipe(
      switchMap((loggedIn: boolean) => {
        if (!loggedIn) {
          console.log('User not logged in, redirecting to login page');
          this.router.navigate(['/login']);
          return of(false);
        }
        
        const expectedRoles = route.data['expectedRoles'] as string[];
        return this.loginService.userHasRole(expectedRoles).pipe(
          map(hasRole => {
            if (!hasRole) {
              console.log('User does not have required roles:', expectedRoles);
              this.router.navigate(['/404']);
              return false;
            }
            return true;
          }),
          catchError(() => {
            console.error('Error checking user roles');
            this.router.navigate(['/login']);
            return of(false);
          })
        );
      }),
      catchError(() => {
        console.error('Error checking login status');
        this.router.navigate(['/login']);
        return of(false);
      })
    );
  }
}
