import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { BudgetService, EmployeeRec } from 'src/app/budget.service';
import { Employee, EmployeeRem, EmployeeService } from 'src/app/employee-service';
import { ConfirmDialogComponent, ConfirmDialogModel } from 'src/app/shared/confirm-dialog/confirm-dialog.component';
import { SimulationRecService } from 'src/app/simulation-rec.service';

@Component({
  selector: 'app-validate-rem',
  templateUrl: './validate-rem.component.html',
  styleUrls: ['./validate-rem.component.scss']
})
export class ValidateRemComponent implements OnInit {
  gab1: number;
  employee: EmployeeRem[] = [];

  minBudget: any;
  maxBudget: any;
  updateBudget: any = 5000;
  data: Employee[] = [];
  departmentId: string = '123456';
  organizationName: string = '';
  budgetSpent: any;
  budgetRemaining: any;
  budgetGlobal1:number;
  id: number;
  filteredEmployees: EmployeeRem[] = [];
  searchTerm: string = '';
  managers: String[] = [];
  selectedManagerId: string = '';
  intervalId:number;

  constructor(
    private budgetService: BudgetService,
    private employeeService: EmployeeService,
    public dialog: MatDialog,
    private simulationService: SimulationRecService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
  
    


    this.getEmployeesByIdOrg();
    
   
  }
 
  

  getEmployeesByIdOrg(): void {
    this.budgetService.getEmployeesByDepartmentRem("123456").subscribe((employees: EmployeeRem[]) => {
      this.employee = employees;
      console.log(employees);
      this.organizationName = this.employee[0].idorg;
      this.budgetGlobal1=this.employee[0].budgetGloabl;
      this.gab1=this.employee[0].gab;
      console.log(employees);
    });
  }



  getImageSrc(): string {
    return  'assets/images/others/vide.jpg' ;
  }

  updateStatus(): void {
    const dialogData = new ConfirmDialogModel('Confirmation', 'Êtes-vous sûr de refuser la demande?');

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '25vw',
      height: '30vh',
      data: dialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.simulationService.setStatusRem("123456").subscribe(
          () => {
            console.log('Statut mis à jour avec succès');
            this.getEmployeesByIdOrg();
          //  this.refreshData(); 
          },
          error => {
            console.error('Erreur lors de la mise à jour du statut', error);
          }
        );
      } else {
        console.log('annuler.');
      }
    });
  }

  updateStatusR(): void {
    const dialogData = new ConfirmDialogModel('Confirmation', 'Êtes-vous sûr de refuser la demande?');

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '25vw',
      height: '30vh',
      data: dialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.simulationService.setStatusRefuserRem("123456").subscribe(
          () => {
            console.log('Statut mis à jour avec succès');
            this.getEmployeesByIdOrg();
          //  this.refreshData(); 
          },
          error => {
            console.error('Erreur lors de la mise à jour du statut', error);
          }
        );
      } else {
        console.log('annuler.');
      }
    });
  }
}
