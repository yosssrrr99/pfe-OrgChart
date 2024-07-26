import { HttpClient, HttpEvent, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  private apiUrl = 'http://localhost:9095/Cool/user';


  constructor(private http: HttpClient) { }

  download(filename: string): Observable<HttpEvent<Blob>> {
    return this.http.get(`${this.apiUrl}/download/${filename}/`, {
      reportProgress: true,
      observe: 'events',
      responseType: 'blob'
    });
  }
}
