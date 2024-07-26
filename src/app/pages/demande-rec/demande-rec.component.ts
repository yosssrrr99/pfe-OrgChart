
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BudgetService, EmployeeRec } from 'src/app/budget.service';
import { Employee,EmployeeService } from 'src/app/employee-service';

@Component({
  selector: 'app-demande-rec',
  templateUrl: './demande-rec.component.html',
  styleUrls: ['./demande-rec.component.scss']
})
export class DemandeRecComponent {
employee:EmployeeRec[]=[]
minBudget:any;
maxBudget:any;
updateBudget:any=5000;
data: Employee[] = [];
departmentId: string = 'TALAN1PR';
organizationName: string = '';
budgetGlobal1:number;
gab1:number;
  constructor(private budgetService:BudgetService,private employeeService:EmployeeService,public router:Router){}

  ngOnInit(): void {
 
    this.getEmployeesByDepartmentAndDate();
  }

  getEmployeesByDepartmentAndDate(): void {
    this.budgetService.getEmployeesByDepartmentAndDate(this.departmentId).subscribe((employees: any[]) => {

      this.employee=employees;
      // Assuming you expect a single object with budgetGlobal and gab properties
      const firstEmployee = employees[0]; // Assuming you expect a single object

    //  console.log(firstEmployee.typeStatus);
  
     
  
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
