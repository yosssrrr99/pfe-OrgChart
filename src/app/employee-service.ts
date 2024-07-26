import { HttpClient, HttpEvent, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private apiUrl = 'http://localhost:9090/employee/Empdepart'; // Remplacez par l'URL correcte de votre API
  private api = 'http://localhost:9090/employee';
  constructor(private http: HttpClient) { }

  getEmployeesByDepartment(departmentId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${departmentId}`).pipe(
      catchError(this.handleError<any>('getEmployeesByDepartment'))
    );
  }
  saveEmployeeRem(data: any, budgetGlobal: number, gab: number, idorg: string): Observable<any> {
    return this.http.post(`${this.api}/save/${budgetGlobal}/${gab}/${idorg}`, data);
  }
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed: ${error.message}`);
      return of(result as T);
    };
  }



  calculateBudget(employee: Employee, currentDate: string): Observable<number> {
    return this.http.post<number>(`${this.apiUrl}/budget`, { employee, date: currentDate });
  }

  updateEmployees(employees: Employee[], idOrg: string, currentDate: Date): Observable<Employee[]> {
    const formattedDate = currentDate.toISOString().split('T')[0]; // Convertir la date en yyyy-mm-dd
    return this.http.put<Employee[]>(`${this.api}/update/${idOrg}/${formattedDate}`, employees).pipe(
      catchError(this.handleError<Employee[]>('updateEmployees'))
    );
  }
  calculateRemainingSalary(mtsal: number,currentDate:Date): Observable<number> {
    const formattedDate = currentDate.toISOString().split('T')[0]; 
    const endpoint = `${this.api}/calculateRemainingSalary/${mtsal}/${formattedDate}`;
    return this.http.get<number>(endpoint);
  }
  calculateBudgetPercentageDep(budgetGlobal: number, budgetDepense: number): Observable<number> {
    const endpoint = `${this.api}/calculatePourcentageBudgetDep/${budgetGlobal}/${budgetDepense}`;
    return this.http.get<number>(endpoint);
  }

  calculateBudgetPercentageRes(budgetGlobal: number, budgetRestant: number): Observable<number> {
    const endpoint = `${this.api}/calculatePourcentageBudgetRes/${budgetGlobal}/${budgetRestant}`;
    return this.http.get<number>(endpoint);
  }
  updateEmployeeStatus(idOrg: any) {
    return this.http.put(`${this.api}/status/${idOrg}`, {});
  }
 
  loadAndDisplayLastOccurrence(EmployeeZy3b: any, idorg: string, numdoss: number): Observable<string> {
    const url = `${this.api}/occur/${idorg}/${numdoss}`;
    return this.http.post<string>(url, EmployeeZy3b);
  }
  poste(): Observable<Poste[]> {
    return this.http.get<Poste[]>(`${this.api}/emploi`);
  }

  motif(): Observable<String[]> {
    return this.http.get<String[]>(`${this.api}/motif`);
  }
 
}



export interface Employee {
  matcle: string;
  mtsal: number;
  blobData: string;
  nomuse: string;
  prenom: string;
  idorg: string;
  nomorg: string;
  poste: string;
  selectedDate:Date;
  nbHeure:number;
  selectedMotif:string;
  nudoss:number;
   // disableDateField?: boolean; 
}
export interface EmployeeZy3b {
  
  selectedDate:Date;
  nbHeure:number;
  motif:String; 
}
export interface EmployeeAndTotalSalaryResponse {
  employees: Employee[]; // Liste des employés
  totalSalary: number; // Total du salaire
}
export interface Poste {
  idjbo00: string;
  emploi: string;
}