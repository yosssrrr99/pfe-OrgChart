import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, of, tap } from 'rxjs';


interface EmployeeDto {
  number: number;
  classification: string;
}

interface BudgetResponse {

  gab:number;
}
@Injectable({
  providedIn: 'root'
})
export class BudgetService {
  private apiUrl = 'http://localhost:9090/employee';

  private salaryRanges = {
    junior: { min: 2000, max: 5000 },
    senior: { min: 5000, max: 10000 },
    confirme: { min: 4000, max: 8000 },
    aspa: { min: 1500, max: 3000 }
  };

  
  constructor(private http: HttpClient) { }

  calculateBudget(employees: EmployeeDto[],budgetAnnuel1:number, budgetGlobal: number): BudgetResponse {
   
    let gab: number = budgetGlobal;

    for (const employee of employees) {
      const range = this.salaryRanges[employee.classification];
      if (range) {
        gab=budgetGlobal-budgetAnnuel1;
        gab -= employee.number * range.max; // Calcul du gab
      }
    }

    return { gab };
}

calculateBudget2(employees: EmployeeDto[], budgetGlobal: number): BudgetResponse {
   
  let gab: number = budgetGlobal;

  for (const employee of employees) {
    const range = this.salaryRanges[employee.classification];
    if (range) {
      gab -= employee.number * range.max; // Calcul du gab
    }
  }

  return { gab };
}

saveEmployeesAndBudget(employees: any, budgetGlobal: number, gab: number, idorg: string): Observable<string> {
  return this.http.post<string>(`${this.apiUrl}/save/${budgetGlobal}/${gab}/${idorg}`, employees)
    .pipe(
      catchError(this.handleError<string>('saveEmployeesAndBudget'))
    );
}

private handleError<T>(operation = 'operation', result?: T) {
  return (error: any): Observable<T> => {
    console.error(error); // Log the error to console or send it to an external logging service
    return of(result as T);
  };
}

 
  updateEmployeesAndBudget(updateRecRequest: any, idorg: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/put/${idorg}`, updateRecRequest);
  }
  getEmployeesByDepartment(id: string): Observable<any> {
    const url = `${this.apiUrl}/demandeAff/${id}`;
    return this.http.get<any>(url);
  }
  getEmployeesByDepartmentRem(id: string): Observable<any> {
    const url = `${this.apiUrl}/demandeAffRem/${id}`;
    return this.http.get<any>(url);
  }


  getEmployeesByDepartmentAndDate(id: string): Observable<any> {
    const url = `${this.apiUrl}/demandeAff/${id}`;
    return this.http.get<any>(url);
  }

  deleteEnvelope(idorg: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${idorg}`).pipe(
      tap((response: any) => console.log('Response from deleteEnvelope:', response))
    );
  }
  deleteEmployeeById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/supp/${id}`);
  }

  getManagersByStatus(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/demande`);
  }
  
}
export interface EmployeeRec {
  id:number;
  number: any,
  classification:any,
  minbudget:any;
  maxbudget:any;
  idorg:any;
  budgetGlobal:any;
  gab:any;
  poste:string;
  
}

  export interface UpdateRecRequest {
    employees: EmployeeRec[];
    budgetGlobal: number;
    gab: number;
  }


