import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { BudgetService, EmployeeRec } from 'src/app/budget.service';
import { Employee, EmployeeService } from 'src/app/employee-service';
import { ConfirmDialogComponent, ConfirmDialogModel } from 'src/app/shared/confirm-dialog/confirm-dialog.component';
import { SimulationRecService } from 'src/app/simulation-rec.service';

@Component({
  selector: 'app-validate',
  templateUrl: './validate.component.html',
  styleUrls: ['./validate.component.scss']
})
export class ValidateComponent implements OnInit,AfterViewInit,OnDestroy {
  gab1: number;
  employee: EmployeeRec[] = [];
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
  filteredEmployees: EmployeeRec[] = [];
  searchTerm: string = '';
  managers: String[] = [];
  selectedManagerId: string = '123456';
  intervalId:number;

  constructor(
    private budgetService: BudgetService,
    private employeeService: EmployeeService,
    public dialog: MatDialog,
    private simulationService: SimulationRecService,
    private route: ActivatedRoute
  ) {}
  ngAfterViewInit(): void {
    this.route.queryParams.subscribe(params => {
      console.log(this.selectedManagerId)
      if (params['managerId']) {
        this.selectedManagerId = params['managerId'];
        this.getEmployeesByIdOrg();
      }
    });  }

  ngOnInit(): void {
    
        this.getEmployeesByIdOrg();
      
  



    this.getEmployees();
    this.budgetService.getManagersByStatus().subscribe(data => {
      this.managers = data

    });

   
  }
  ngOnDestroy(): void {
    if(this.intervalId){
      clearInterval(this.intervalId);
    }
  }
  refreshData(): void {
     window.location.reload();
  }
  

  getEmployeesByIdOrg(): void {
    this.budgetService.getEmployeesByDepartment(this.selectedManagerId).subscribe((employees: EmployeeRec[]) => {
      this.employee = employees;
      this.minBudget = Math.min(...employees.map(emp => emp.minbudget));
      this.maxBudget = Math.max(...employees.map(emp => emp.maxbudget));
      this.organizationName = this.employee[0].idorg;
      this.budgetGlobal1=this.employee[0].budgetGlobal;
      this.gab1=this.employee[0].gab;
      console.log(employees);
    });
  }

  onManagerChange(managerId: string): void {
    this.selectedManagerId = managerId;
    this.getEmployeesByIdOrg();
  }

  getEmployees(): void {
    this.employeeService.getEmployeesByDepartment(this.departmentId).subscribe((employees: Employee[]) => {
      this.data = employees;
      if (this.data[0].nomorg) {
        this.organizationName = this.data[0].nomorg;
      }
    });
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
        this.simulationService.setStatus(this.selectedManagerId).subscribe(
          () => {
            console.log('Statut mis à jour avec succès');
            this.getEmployeesByIdOrg();
            this.refreshData(); 
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
        this.simulationService.setStatusRefuser(this.selectedManagerId).subscribe(
          () => {
            console.log('Statut mis à jour avec succès');
            this.getEmployeesByIdOrg();
            this.refreshData(); 
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
