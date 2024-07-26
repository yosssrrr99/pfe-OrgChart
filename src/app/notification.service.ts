import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:9090/employee'; 

  constructor(private http: HttpClient) { }

  getPendingRequestsCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count`);
  }

  getManagersByStatus(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/demande`);
  }
}