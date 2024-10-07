import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, of, throwError } from 'rxjs';

interface AuthenticationRequest {
  username: string;
  password: string;
}

interface AuthenticationResponse {
  token: string;
  role: string;
}
@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private apiUrl = 'http://localhost:9090/employee';
  private api = 'http://localhost:9090'; // L'URL de votre API backend
  private app="http://localhost:9095/api/v1/auth"
  constructor(private http: HttpClient) { }




  logina(request: AuthenticationRequest): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(`${this.api}/login`, request);
  }

  logout() {
    // Supprimer le token du stockage local
    localStorage.removeItem('authToken');
  
  }
  
  getUserRole(): Observable<string> {
    return this.http.get<{ role: string }>(`${this.api}/role`).pipe(
      map(response => response.role)
    );
  }
  getHasRole(expectedRoles: string[]): Observable<boolean> {
    return this.getRole().pipe(
      map(userRole => {
        console.log('User role:', userRole);
        console.log('Expected roles:', expectedRoles);
        return expectedRoles.includes(userRole);
      }),
      catchError(() => {
        console.error('Error checking user roles');
        return of(false);
      })
    );
  }
  isLoggedIn():  Observable<boolean>  {
    const token = localStorage.getItem('authToken'); // Récupérer le token du stockage local
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.get<boolean>(`${this.app}/is-connected`,{ headers }) ;
  }

  getRole(): Observable<string> {
    const token = localStorage.getItem('authToken'); // Récupérer le token du stockage local
    if (!token) {
      return of('');
    }

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<{ role: string }>(`${this.app}/role`, { headers }).pipe(
      map(response => response.role),
      catchError((error) => {
        console.error('Error fetching role:', error);
        return of('');
      })
    );
  }



}
