import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { EmployeeRem, EmployeeService } from 'src/app/employee-service';

@Component({
  selector: 'app-historique-rem',
  templateUrl: './historique-rem.component.html',
  styleUrls: ['./historique-rem.component.scss']
})
export class HistoriqueRemComponent {

  employee:EmployeeRem[]=[]
  minBudget:any;
  maxBudget:any;
  updateBudget:any=5000;
  departmentId: string = '123456';
  organizationName: string = '';
  budgetGlobal1:number;
  gab1:number;
    constructor(private employeeService:EmployeeService,public router:Router){}
  
    ngOnInit(): void {
   
      this.getEmployeesByIdOrg();
    }
  
    getEmployeesByIdOrg(): void {
      this.employeeService.getRemunerationRequestById(this.departmentId).subscribe((employees: any[]) => {

        this.employee=employees;
        // Assuming you expect a single object with budgetGlobal and gab properties
        const firstEmployee = employees[0]; // Assuming you expect a single object
    
       
    
        // Assign budgetGlobal1 and gab1 from the first employee
        if (firstEmployee) {
          this.budgetGlobal1 = firstEmployee.budgetGloabl;
          this.organizationName=firstEmployee.idorg;
       this.gab1=firstEmployee.gab;
        }
    
     
      });
    }
    
      
    public updateEnvelope() {
      // Utilisation du Router pour naviguer vers le composant de mise à jour de l'enveloppe avec l'ID en paramètre
      this.router.navigate(['/update-rem']);
   
    }



   
    getImageSrc(blobData: string): string {
      return 'assets/images/others/vide.jpg' ;
    } 
 
}
