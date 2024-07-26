import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SimulationRecService {


  private apiUrl = 'http://localhost:9090/SimulationRec';

private baseUrl='http://localhost:9090/employee'
  constructor(private http: HttpClient) { }

  updateDepartementAug(id: number, budget: number) {
    const url = `/updateAug/${id}/${budget}`;
   
    return this.http.put(url, {});
  }

  setStatus(id: string): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/status/${id}`, null);
  }
  setStatusRefuser(id: string): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/statusR/${id}`, null);
  }

  
  getNotifications(managerId: string): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.baseUrl}/manager/${managerId}`);
  }
  markNotificationAsRead(notificationId: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/${notificationId}/mark-as-read`, {});
  }
  getUnreadNotificationCount(idManager: string): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/nbNotif/${idManager}`);
  }
  
}

export interface Notification {
  id: number;
  message: string;
  idManger: string;
  date: string;
  isRead: boolean;
}