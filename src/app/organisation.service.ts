import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrganisationService {

  private apiUrl = 'http://localhost:9090/employee/org';


  constructor(private http: HttpClient) { }

  getOrganisations(): Observable<Organisation[]> {
    return this.http.get<Organisation[]>(this.apiUrl);
  }
}

export interface Organisation {
  idorg: string;
  nomorg: string;
  budget: number | null;
}
