import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BudgetService, EmployeeRec } from 'src/app/budget.service';
import { Employee,EmployeeService } from 'src/app/employee-service';

@Component({
  selector: 'app-historique',
  templateUrl: './historique.component.html',
  styleUrls: ['./historique.component.scss']
})
export class HistoriqueComponent {

  employee:EmployeeRec[]=[]
  minBudget:any;
  maxBudget:any;
  updateBudget:any=5000;
  data: Employee[] = [];
  departmentId: string = '123456';
  organizationName: string = '';
  budgetGlobal1:number;
  gab1:number;
    constructor(private budgetService:BudgetService,private employeeService:EmployeeService,public router:Router){}
  
    ngOnInit(): void {
   
      this.getEmployeesByIdOrg();
    }
  
    getEmployeesByIdOrg(): void {
      this.budgetService.getEmployeesByDepartment(this.departmentId).subscribe((employees: any[]) => {

        this.employee=employees;
        // Assuming you expect a single object with budgetGlobal and gab properties
        const firstEmployee = employees[0]; // Assuming you expect a single object
    
       
    
        // Assign budgetGlobal1 and gab1 from the first employee
        if (firstEmployee) {
          this.budgetGlobal1 = firstEmployee.budgetGlobal;
          this.organizationName=firstEmployee.idorg;
       this.gab1=firstEmployee.gab;
        }
    
     
      });
    }
    
      
    public updateEnvelope() {
      // Utilisation du Router pour naviguer vers le composant de mise à jour de l'enveloppe avec l'ID en paramètre
      this.router.navigate(['/update']);
   
    }

         
    updateStatus(id:number) {
       // ID de l'employé à mettre à jour (remplacer par votre logique)
      this.employeeService.updateEmployeeStatus(id).subscribe(() => {
        console.log('Statut mis à jour avec succès.');
      });
    }

   
    
 
}
