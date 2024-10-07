import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  

 

  private baseUrl = 'http://localhost:9099/Cool/api/files';
  private categoriesUrl=' http://localhost:9099/Cool/api/files/api/categories'

  constructor(private http: HttpClient) { }

  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(this.categoriesUrl);
  }

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

    return this.http.get<Blob>(`http://localhost:9099/Cool/api/files/download/${blobName}`, {
      headers: headers,
      responseType: 'blob' as 'json'  // Définissez le type de réponse sur blob
    });

  }
 isLoggedIn(): Observable<string> {
    const token = localStorage.getItem('authToken'); // Récupérer le token du stockage local
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    // Utilisez `map` pour transformer la réponse en nom d'utilisateur
    return this.http.get<UserResponse>(`${this.baseUrl}/verify-user`, { headers })
      .pipe(
        map(response => response.userName)  // Extraire le nom d'utilisateur de la réponse
      );
  }
  getUsers(): Observable<UsersResponse[]> {
    return this.http.get<UsersResponse[]>(this.baseUrl+"/users");
  }
  getDocumentByCategory(category: string): Observable<Document[]> {
    const token = localStorage.getItem('authToken'); // Récupérer le token du stockage local
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    // Utilisez `map` pour transformer la réponse en nom d'utilisateur
  
    return this.http.get<Document[]>(`${this.baseUrl}/all/${category}`, { headers });
  }
  

  deleteFile(filename: string): Observable<string> {
    return this.http.delete<string>(`${this.baseUrl}/delete/${filename}`);
  }
  
  getDocumentsByUserAndCategory(userName: string, category: string): Observable<Document[]> {
    return this.http.get<Document[]>(`http://localhost:9099/Cool/api/files/doc/${userName}/${category}`);
  }
  
  getDocumentsByUserName(userName: string): Observable<Document[]> {
    const url = `${this.baseUrl}/doc/${userName}`;
    return this.http.get<Document[]>(url);
  }
}
export interface UsersResponse {
  id: number;
  firstName: string;
  lastName: string;
  nomorg: string; // Assurez-vous que cela correspond aux propriétés de votre réponse
}
export interface UserResponse {
  userName: string;
}
