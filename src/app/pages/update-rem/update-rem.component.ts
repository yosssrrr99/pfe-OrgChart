import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { BudgetService } from 'src/app/budget.service';
import { EmployeeRem, EmployeeService } from 'src/app/employee-service';
import { Organisation } from 'src/app/organisation.service';
import { AlertDialogComponent } from 'src/app/shared/alert-dialog/alert-dialog.component';
import { ConfirmDialogComponent, ConfirmDialogModel } from 'src/app/shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-update-rem',
  templateUrl: './update-rem.component.html',
  styleUrls: ['./update-rem.component.scss']
})
export class UpdateRemComponent implements OnInit {
  dateFormControl = new FormControl();
  disableDateFieldTab1: boolean = false;
  disableDateFieldTab2: boolean = false;
  isBlinking: boolean = false;
  isClicked: boolean = false;
  gab1: number = 0;
  gab2: number = 0;
  oldBuget1: number = 0;
  oldBuget2: number = 0;
  budgetAnnuel1: number = 0;
  budgetAnnuel2: number = 0;
  idOrg: string = '';
  updateBudget: any = 5000;
  private _budgetGlobal1: number = 0;
  private _budgetGlobal2: number = 0;
  percentageChangeTab1: number = 0;
  percentageChangeTab2: number = 0;
  percentageChangeResTab1: number = 0;
  percentageChangeResTab2: number = 0;
  organisations: Organisation[] = [];
  selectedOrganisation1: Organisation | null = null;
  selectedOrganisation2: Organisation | null = null;
  departmentId: string = 'P00000653';
  organizationName: string = '';
  data: any;
  dataDep: any;
  tab1Data: EmployeeRem[] = [];
  budgetTab1: number = 20000;
  budgetTab2: number = 20000;
  selectedDate: Date | null = null;
  remainingSalary: number = 0;
  getIdManger:string="123456"
  motifa: "";
  empZy3bDT0 = {
    dtef00: '',
    idou00: '',
    idjbo00: '',
    typemotif: '',
    nbHeure: 0
  };
  selectedPoste: string = '';
  isLoggedIn: boolean = false;
  initialSalary: number = 0;
  salaryDifference: number = 0;
  pourcentage:number;
  initialSalaries: { [employeeId: string]: number } = {};

  constructor(
    private employeeService: EmployeeService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
    public router: Router,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute
  ) {}
  ngOnInit() {
      this.fetchRemunerationRequest("123456");
      this.updatePercentages();
    
  }
  get budgetGlobal1(): number {
    return this._budgetGlobal1;
  }
 
  updateSalary(event: any, employee: any) {
    const percentage = parseFloat(event.target.value);
  this.pourcentage=percentage;
    if (isNaN(percentage) || percentage < 0) {
      // Afficher un message d'erreur
      alert('Veuillez entrer un pourcentage valide.');
      return;
    }
  
    if (percentage === 0) {
      employee.mtsal = this.initialSalaries[employee.iduse];
    } else {
      const newSalary = this.initialSalaries[employee.iduse] + (this.initialSalaries[employee.iduse] * percentage) / 100;
      const salaryDifference = newSalary - employee.mtsal;
      employee.mtsal = newSalary;
      this.budgetAnnuel1 += salaryDifference;
    }
  
    this.updateGab1();
    this.percentageChangeTab1 = this.calculatePercentageChange(this.budgetGlobal1, this.gab1);
    this.percentageChangeResTab1 = 100 - this.percentageChangeTab1;
    this.cdr.detectChanges();
  }
  async calculateSalary(mtsal: number): Promise<number> {
    const dateToUse = this.selectedDate || new Date();
    try {
      const remainingSalary = await this.employeeService.calculateRemainingSalary(mtsal, dateToUse).toPromise();
      return remainingSalary;
    } catch (error) {
      console.error('Error calculating remaining salary:', error);
      return -1;
    }
  }

  set budgetGlobal1(value: number) {
    this._budgetGlobal1 = value;
    this.updateGab1();
  }

  updateGab1() {
    this.gab1 = this.budgetGlobal1 - this.budgetAnnuel1;
    this.updatePercentages();
  }

  updatePercentages() {
    this.percentageChangeTab1 = this.calculatePercentageChange(this.budgetGlobal1, this.budgetAnnuel1);
    this.percentageChangeResTab1 = 100 - this.percentageChangeTab1;
  }

  calculatePercentageChanges(budgetGlobal: number,gab:number): number {
    if (budgetGlobal === 0) return 0;
    return (gab / budgetGlobal) * 100;
  }
  calculatePercentageChange(budgetGlobal: number, budgetAnnuel: number): number {
    if (budgetGlobal === 0) return 0;
    return (budgetAnnuel / budgetGlobal) * 100;
  }
  initializeEmployees(employees: any[]) {
    employees.forEach(employee => {
      this.initialSalaries[employee.iduse] = employee.mtsal;
    });
  }
  fetchRemunerationRequest(id: string) {
    this.employeeService.getRemunerationRequestById(id).subscribe(data => {
      this.tab1Data = data.map(emp => ({
        id: emp.id,
        nom: emp.nom,
        mtsal: emp.mtsal,
        poste:emp.poste,

      }));
      console.log(data);
      const firstEmployee = data[0]; // Assuming you expect a single object
      if (firstEmployee) {
        this.budgetGlobal1 = firstEmployee.budgetGloabl;
        this.organizationName = firstEmployee.idorg;
        this.budgetAnnuel1=firstEmployee.budgetAnnuel;
        this.gab1 = firstEmployee.gab; 
      }
      console.log(this.gab1);
      this.initializeEmployees(this.tab1Data);
      this.updateGab1();
    });
  }


  updateRemunerationRequest() {
  
    // Prepare data for the update
    const data = this.tab1Data.map(employee => ({
      idorg: this.organizationName, // assuming this is the correct value
      date: new Date().toISOString(),
      budgetGlobal: this.budgetGlobal1,
      budgetAnnuel: this.budgetAnnuel1,
      gab: this.gab1,
      pourcentage:this.pourcentage,
      employees: [
          {
              idorg: employee.idorg,
              date: new Date().toISOString(),
              budgetGlobal: this.budgetGlobal1,
              gab: this.gab1,
              nom: employee.nom,
              mtsal: employee.mtsal,
              budgetAnnuel: this.budgetAnnuel1,
              pourcentage:this.pourcentage
          }
      ]
  }));
    // Call the service to update the data
    this.employeeService.updateRemunerationRequest("123456", data)
      .subscribe(response => {
        console.log('Data updated successfully:', response);
        this.router.navigate(['/history-rem']);
        this.snackBar.open('La demande est mise à jour avec succès', '×', {
          verticalPosition: 'top',
          duration: 5000,
          panelClass: ['success']
        });
        // Handle response as needed
      }, error => {
        console.error('Error updating data:', error);
        this.dialog.open(AlertDialogComponent, {
          width: '25vw',
          height: '20vh',
          data: 'Erreur lors de la mise à jour de la demande. Veuillez réessayer.'
        });
      });
  }

  deleteEmployee() {
    const dialogData = new ConfirmDialogModel('Confirmation', 'Êtes-vous sûr de vouloir annuler cette enveloppe?');

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '25vw',
      height: '30vh',
      data: dialogData
    });
    const dep:string="123456";

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const idorg = dep.trim();
        this.employeeService.deleteEmployee(idorg).subscribe(
          response => {
            this.snackBar.open('La ligne est supprimée avec succées !', '×', { panelClass: 'success', duration: 5000 });
            this.router.navigate(['/remuneration']);
          },
          error => {
            console.error('Error deleting envelope:', error);
            const errorMessage = error.error ? error.error : "Une erreur s'est produite lors de la suppression de l'enveloppe.";
            this.snackBar.open(errorMessage, '×', { panelClass: 'success', duration: 5000 });
          }
        );
      }
    });
  }



  
  getImageSrc(): string {
    return  'assets/images/others/vide.jpg' ;
  }
}