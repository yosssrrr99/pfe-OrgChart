import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:9090/employee'; 
  private api = 'http://localhost:9090/kafka'; 
  constructor(private http: HttpClient) { }

  getPendingRequestsCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count`);
  }
  getPendingRequestsCountRem(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/countRem`);
  }

  getManagersByStatus(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/demande`);
  }

  getMessages(): Observable<string[]> {
    return this.http.get<string[]>(`${this.api}/messages`);
  }

  clearMessage(message: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/messages/${encodeURIComponent(message)}`);
  }

  clearAllMessages(): Observable<void> {
    return this.http.delete<void>(`${this.api}/messages`);
  }


  getStatusRecMessages(): Observable<string[]> {
    return this.http.get<string[]>(`${this.api}/messagesRec`);
  }

  clearMessageRec(message: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/messagesRec/${message}`);
  }

  clearAllMessagesRec(): Observable<void> {
    return this.http.delete<void>(`${this.api}/messagesRec`);
  }

  getMessagesRem(): Observable<string[]> {
    return this.http.get<string[]>(`${this.api}/messagesRem`);
  }

  clearMessageRem(message: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/messagesRem/${message}`);
  }

  clearAllMessagesRem(): Observable<void> {
    return this.http.delete<void>(`${this.api}/messagesRem`);
  }


  getMessagesRemm(): Observable<string[]> {
    return this.http.get<string[]>(`${this.api}/messagesRemm`);
  }

  clearMessageRemm(message: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/messagesRemm/${message}`);
  }

  clearAllMessagesRemm(): Observable<void> {
    return this.http.delete<void>(`${this.api}/messagesRemm`);
  }
}