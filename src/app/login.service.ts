import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private apiUrl = 'http://localhost:9090/employee';


  constructor(private http: HttpClient) { }
  login(username: string, password: string): Observable<string> {
    const url = `${this.apiUrl}/login/${username}/${password}`;
    return this.http.post<string>(url, "login avec succes"); // Assuming the response is a string, adjust as per your actual response type
  }
  logout(): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/logout`,"logout avec succes");
  }
  getUserRole(): Observable<string> {
    return this.http.get<{ role: string }>(`${this.apiUrl}/role`).pipe(
      map(response => response.role)
    );
  }
  userHasRole(expectedRoles: string[]): Observable<boolean> {
    return this.getUserRole().pipe(
      map(userRoles => {
        // Utilisation de includes sur le tableau de rôles une fois qu'il est émis par l'Observable
        return expectedRoles.some(role => userRoles.includes(role));
      })
    );
  }
  isLoggedIn():  Observable<boolean>  {
    return this.http.get<boolean>(`${this.apiUrl}/logged`) ;
  }
}
