import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { Organisation, OrganisationService } from 'src/app/organisation.service';
import { ChangeDetectorRef, Component } from '@angular/core';
import { Employee, EmployeeAndTotalSalaryResponse, EmployeeService, EmployeeZy3b, Poste } from 'src/app/employee-service';
import { ConfirmDialogComponent, ConfirmDialogModel } from 'src/app/shared/confirm-dialog/confirm-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { FormControl } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { LoginService } from 'src/app/login.service';
import { Router } from '@angular/router';
import { AlertDialogComponent } from 'src/app/shared/alert-dialog/alert-dialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-remuneration',
  templateUrl: './remuneration.component.html',
  styleUrls: ['./remuneration.component.scss']
})
export class RemunerationComponent {
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
  tab1Data: Employee[] = [];
  tab2Data: Employee[] = [];
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
  numdoss: number = 0;
  message: string = '';
  postes: Poste[] = [];
  motif: String[] = [];
  selectedPoste: string = '';
  isLoggedIn: boolean = false;
  initialSalary: number = 0;
  salaryDifference: number = 0;
  pourcentage:number;
  initialSalaries: { [employeeId: string]: number } = {};

  constructor(
    private organisationService: OrganisationService,
    private employeeService: EmployeeService,
    public dialog: MatDialog,
    private datePipe: DatePipe,
    private loginService: LoginService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private snackBar:MatSnackBar
  ) { }

  ngOnInit() {
    this.fetchOrganisations();
    this.startBlinking();
    this.getEmployeesByOrganisation("F0001", 'tab1');
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
  
 
  initializeEmployees(employees: any[]) {
    employees.forEach(employee => {
      this.initialSalaries[employee.iduse] = employee.mtsal;
    });
  }

  get budgetGlobal1(): number {
    return this._budgetGlobal1;
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

  startBlinking() {
    setInterval(() => {
      if (this.gab1 < 0 && !this.isClicked) {
        this.isBlinking = !this.isBlinking;
      } else {
        this.isBlinking = false;
      }
    }, 500);
  }

  handleClick() {
    this.isClicked = true;
  }

  fetchOrganisations() {
    this.organisationService.getOrganisations().subscribe(
      data => {
        this.organisations = data;
      },
      error => {
        console.error('Error fetching organisations', error);
      }
    );
  }



  getEmployeesByOrganisation(organisationId: string, listId: string): void {
    this.employeeService.getEmployeesByDepartment(organisationId).subscribe((response: EmployeeAndTotalSalaryResponse) => {
      if (listId === 'tab1') {
        this.tab1Data = response.employees.filter(employee => employee.idorg === "F0001");
        this.budgetAnnuel1 = response.totalSalary;
        this.updateGab1();
        this.oldBuget1 = this.budgetGlobal1 - this.budgetAnnuel1;
        this.initializeEmployees(this.tab1Data);
      }
    });
  }

  calculatePercentageChange(budgetGlobal: number, budgetAnnuel: number): number {
    if (budgetGlobal === 0) return 0;
    return (budgetAnnuel / budgetGlobal) * 100;
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
  confirm() {
    // Vérifiez si chaque employé a un pourcentage valide
    const invalidEntries = this.tab1Data.some(employee => {
      return this.pourcentage === undefined || 
             this.pourcentage === null || 
             isNaN( this.pourcentage) || 
             this.pourcentage < 0;
    });

    // Vérifiez si les salaires sont valides
    const invalidSalaries = this.tab1Data.some(employee => 
      isNaN(employee.mtsal) || employee.mtsal <= 0
    );
    
    if (invalidEntries || invalidSalaries) {
      
      this.dialog.open(AlertDialogComponent, {
        width: '25vw',
        height: '20vh',
        data: 'Veuillez remplir tous les champs ou ajuster les salaires avant de confirmer.'
      });
      return;
    
  }
    const dialogData = new ConfirmDialogModel('Confirmation', 'Êtes-vous sûr de vouloir confirmer?');
  
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '25vw',
      height: '30vh',
      data: dialogData
    });
  
    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        
  
  
        // Préparer les données pour l'envoi
        const data = this.tab1Data.map(employee => ({
          idorg: employee.idorg,
          date: new Date(),
          budgetGloabl: this.budgetGlobal1,
          budgetAnnuel:this.budgetAnnuel1,
          pourcentage:this.pourcentage,
          gab: this.gab1,
          nom: employee.nomuse,
          mtsal: employee.mtsal,
          poste: employee.poste
        }));
  
        // Appeler le service pour enregistrer les données
        this.employeeService.saveEmployeeRem(data, this.budgetGlobal1, this.gab1, this.selectedOrganisation1?.idorg)
          .subscribe(response => {
            console.log('Data saved successfully:', response);
            this.router.navigate(['/history-rem']);
            this.snackBar.open('La demande est ajoutée avec succées', '×', {
              verticalPosition: 'top',
              duration: 5000,
            
              panelClass: ['success'] 
            });
            // Gérer la réponse comme nécessaire
          }, error => {
            console.error('Error saving data:', error);
           
            return;
            // Gérer l'erreur comme nécessaire
          });
      }
    });
  }
  
  
  
  


  getImageSrc(blobData: string): string {
    return blobData === '0' ? 'assets/images/others/vide.jpg' : blobData;
  }
}
