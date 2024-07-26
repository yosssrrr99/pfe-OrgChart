import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  

 

  private baseUrl = 'http://localhost:9095/Cool/api/files';

  constructor(private http: HttpClient) { }

  getFile(fileName: string): Observable<any> {
    const options = {
      responseType: 'blob' as 'json',
      observe: 'response' as 'body'
    };
    return this.http.get<Blob>(this.baseUrl + fileName, options);
  }

  getFileAsBase64(fileName: string): Observable<any> {
    return this.http.get(this.baseUrl + fileName, { responseType: 'text' });
  }
  getImage(filename: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/list/${filename}`, { responseType: 'blob' });
  }

 

  uploadFiles(files: File[]): Observable<any> {
    const formData: FormData = new FormData();
    files.forEach((file: File) => {
      formData.append('files', file, file.name);
    });
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data');
    return this.http.post<any>(this.baseUrl, formData, { headers });
  }

  downloadFile(blobName: string): Observable<Blob> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      // Vous pouvez ajouter des en-têtes supplémentaires si nécessaire
    });

    return this.http.get<Blob>(`http://localhost:9095/Cool/api/files/download/${blobName}`, {
      headers: headers,
      responseType: 'blob' as 'json'  // Définissez le type de réponse sur blob
    });
  }

  
  getDocumentByCategory(category: string): Observable<Document[]> {
    return this.http.get<Document[]>(`${this.baseUrl}/all/${category}`);
  }
  

  deleteFile(filename: string): Observable<string> {
    return this.http.delete<string>(`${this.baseUrl}/delete/${filename}`);
  }
}
