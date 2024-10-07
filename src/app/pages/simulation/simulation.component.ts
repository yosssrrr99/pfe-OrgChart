import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import {Organisation, OrganisationService } from 'src/app/organisation.service';
import { Component } from '@angular/core';
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
  selector: 'app-simulation',
  templateUrl: './simulation.component.html',
  styleUrls: ['./simulation.component.scss']
  
})
export class SimulationComponent {

  dateFormControl = new FormControl();
  disableDateFieldTab1: boolean = false;
  disableDateFieldTab2: boolean = false;
  isBlinking: boolean = false;
  isClicked: boolean = false;
  gab1: number;
  gab2: number;
  oldBuget1: number;
  oldBuget2: number;
  budgetAnnuel1: number;
  budgetAnnuel2: number;
  idOrg:string='';
  updateBudget:any=5000;
  private _budgetGlobal1: number = 0;
  private _budgetGlobal2: number = 0;
  percentageChangeTab1: any;
  percentageChangeTab2: any;

  percentageChangeResTab1: any;
  percentageChangeResTab2: any;
  organisations: Organisation[] = [];
  selectedOrganisation1: Organisation | null = null;
  selectedOrganisation2: Organisation | null = null;
  departmentId: string = 'P00000653 ';
  organizationName: string = '';
  data:any;
  dataDep:any;
  tab1Data: Employee[] = []; 

  tab2Data: Employee[] = [];
  budgetTab1: number = 20000;
  budgetTab2: number = 20000;
  selectedDate: Date | null = null;
remainingSalary:number = 0;
motifa:"";
empZy3bDT0 = {
 
  typemotif: ''
};
numdoss: number;
message: string;
postes: String[]=[];
motif:String[]=[];
selectedPoste: string;
isLoggedIn:boolean;
  constructor(private organisationService: OrganisationService,private employeeService:EmployeeService,public dialog: MatDialog,private datePipe: DatePipe,private loginService:LoginService,private router:Router,private snackBar:MatSnackBar) {}
  ngOnInit() {
 //   this.checkLoggedInStatus();
 this.fetchOrganisations();
 this.startBlinking();
 this.fetchPostesAndMotif();
  
  }
  private checkLoggedInStatus() {
    this.loginService.isLoggedIn().subscribe(
      (loggedIn: boolean) => {
        this.isLoggedIn = loggedIn;
        if (!this.isLoggedIn) {
          this.router.navigate(['/login']);
        } else {
          this.fetchOrganisations();
          this.startBlinking();
          this.fetchPostesAndMotif();
        }
      },
      (error) => {
        console.error('Erreur lors de la vérification de la connexion:', error);
        // Gérer l'erreur si nécessaire
        this.isLoggedIn = false; // Définir à false en cas d'erreur
        this.router.navigate(['/login']); // Rediriger vers la page de connexion en cas d'erreur
      }
    );
  }
  private fetchPostesAndMotif() {
    this.employeeService.poste().subscribe((data) => {
      this.postes = data;
      console.log(this.postes);
    });

    this.employeeService.motif().subscribe((data) => {
      this.motif = data;
      console.log(this.motif);
    });
  }
  get budgetGlobal1(): number {
    return this._budgetGlobal1;
  }

  set budgetGlobal1(value: number) {
    this._budgetGlobal1 = value;
    this.updateGab1();
  }
  get budgetGlobal2(): number {
    return this._budgetGlobal2;
  }

  set budgetGlobal2(value: number) {
    this._budgetGlobal2 = value;
    this.updateGab2();
  }

  updateGab1() {
    this.gab1 = this.budgetGlobal1 - this.budgetAnnuel1;

  }
  updateGab2() {
    this.gab2 = this.budgetGlobal2 - this.budgetAnnuel2;
  }

  startBlinking() {
    setInterval(() => {
      if (this.gab1 < 0 && !this.isClicked) {
        this.isBlinking = !this.isBlinking;
      } else {
        this.isBlinking = false;
      }
    }, 500); // Intervalle de 500ms pour le clignotement
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


  onSelect1(event: any, listId: string) {
    this.selectedOrganisation1 = event.value;
    if (this.selectedOrganisation1) {
        this.getEmployeesByOrganisation(this.selectedOrganisation1.idorg, 'tab1');
    }
}

onSelect2(event: any, listId: string) {
    this.selectedOrganisation2 = event.value;
    if (this.selectedOrganisation2) {
        this.getEmployeesByOrganisation(this.selectedOrganisation2.idorg, 'tab2');
    }
}
getEmployeesByOrganisation(organisationId: string, listId: string): void {
  this.employeeService.getEmployeesByDepartment(organisationId).subscribe((response: EmployeeAndTotalSalaryResponse) => {
    if (listId === 'tab1') {
      this.tab1Data = response.employees.filter(employee => employee.idorg === this.selectedOrganisation1.idorg);
      this.budgetAnnuel1 = response.totalSalary;
      this.updateGab1();
      this.oldBuget1 = this.budgetGlobal1 - this.budgetAnnuel1;
    } else if (listId === 'tab2') {
      this.tab2Data = response.employees.filter(employee => employee.idorg === this.selectedOrganisation2.idorg);
      this.budgetAnnuel2 = response.totalSalary;
      this.updateGab2();
      this.oldBuget2 = this.budgetGlobal2 - this.budgetAnnuel2;
    }
  });
}

  calculatePercentageChange(oldBudget: number, newBudget: number, containerId: string) {
    const percentageChange = ((newBudget - oldBudget) / oldBudget) * 100;
    console.log('Percentage Change for', containerId, ':', percentageChange);
    if (containerId === 'tab1') {
      this.percentageChangeTab1 = percentageChange; 
    } else if (containerId === 'tab2') {
      this.percentageChangeTab2 = percentageChange; 
    }
  }
 
  

  async onDrop(event: CdkDragDrop<Employee[]>) {
    console.log('onDrop triggered');
    console.log(event);

    // Vérifiez si le budget global est valide (non négatif)
    if (this.budgetGlobal1 <= 0 || this.budgetGlobal2<= 0) {
      this.dialog.open(AlertDialogComponent, {
        width: '25vw',
        height: '20vh',
        data: 'Le budget global ne peut pas être négatif.'
    });
    return;
    }

    if (event.previousContainer === event.container) {
        moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
        const employee = event.previousContainer.data[event.previousIndex];
      
        transferArrayItem(event.previousContainer.data, event.container.data, event.previousIndex, event.currentIndex);

        if (event.container.id === 'tab1') {
            this.gab1 -= await this.calculateSalary(employee.mtsal);
            this.gab2 += await this.calculateSalary(employee.mtsal);
            this.budgetAnnuel1 += await this.calculateSalary(employee.mtsal);
            this.budgetAnnuel2 -= await this.calculateSalary(employee.mtsal);
            employee.selectedDate = this.selectedDate;
            this.numdoss = employee.nudoss;
            //this.empZy3bDT0.idjbo00 = this.selectedPoste;
            //this.empZy3bDT0.idou00 = employee.idorg;
            // copiedEmployee.disableDateField = false; // Activer le champ de date dans le tableau de dépôt 1
            // employee.disableDateField = true; // Désactiver le champ de date dans le tableau de dépôt 2
        } else if (event.container.id === 'tab2') {
            this.gab1 += await this.calculateSalary(employee.mtsal);
            this.gab2 -= await this.calculateSalary(employee.mtsal);
            this.budgetAnnuel1 -= await this.calculateSalary(employee.mtsal);
            this.budgetAnnuel2 += await this.calculateSalary(employee.mtsal);
            employee.selectedDate = this.selectedDate;
            this.numdoss = employee.nudoss;
            // copiedEmployee.disableDateField = true; // Désactiver le champ de date dans le tableau de dépôt 1
            // employee.disableDateField = false; // Activer le champ de date dans le tableau de dépôt 2
        }

        this.percentageChangeResTab1 = await this.calculerPourcentageBudgetDep(this.budgetGlobal1, this.gab1);
        this.percentageChangeTab1 = 100 - this.percentageChangeResTab1;

        this.percentageChangeResTab2 = await this.calculerPourcentageBudgetDep(this.budgetGlobal2, this.gab2);
        this.percentageChangeTab2 = 100 - this.percentageChangeResTab2;

      //  this.empZy3bDT0.dtef00 = this.datePipe.transform(this.selectedDate, 'yyyy-MM-dd') || '';
        this.empZy3bDT0.typemotif = employee.selectedMotif;
      //  this.empZy3bDT0.nbHeure = employee.nbHeure;
      //  this.empZy3bDT0.idjbo00 = employee.poste;
       // this.empZy3bDT0.idou00 = employee.idorg;

        console.log("employe0", employee);
        console.log("employe1", this.empZy3bDT0);
    }
}


  
  async calculerPourcentageBudgetDep(oldBudget: number, newBudget: number): Promise<number> {
    try {
        const pourcentage = await this.employeeService.calculateBudgetPercentageDep(oldBudget, newBudget).toPromise();
        console.log("Pourcentage Dépensé:", pourcentage);
        return pourcentage;
    } catch (error) {
        console.error('Error calculating percentage spent:', error);
        return -1;
    }
}

async calculerPourcentageBudgetRes(oldBudget: number, newBudget: number): Promise<number> {
    try {
        const pourcentage = await this.employeeService.calculateBudgetPercentageRes(oldBudget, newBudget).toPromise();
        console.log("Pourcentage Restant:", pourcentage);
        return pourcentage;
    } catch (error) {
        console.error('Error calculating percentage remaining:', error);
        return -1;
    }
}

  
  // calculateSalary(mtsal: number): Observable<number> {
  //   const currentDate = new Date();
  //   return this.employeeService.calculateRemainingSalary(mtsal, currentDate)
  //     .pipe(
  //       map((response: any) => {
  //         console.log(response);
  //         if (!isNaN(response)) {
  //           return parseFloat(response); // Convertir la réponse en nombre
  //         } else {
  //           throw new Error('Invalid salary format received from backend.');
  //         }
  //       }),
  //       catchError((error: any) => {
  //         console.error('Error calculating remaining salary:', error);
  //         return of(0); // Ou une autre valeur par défaut
  //       })
  //     );
  // }

  onDateChange(event: MatDatepickerInputEvent<Date>) {
    this.selectedDate = event.value;
  }

  async calculateSalary(mtsal: number): Promise<number> {
    const dateToUse = this.selectedDate || new Date(); 
    console.log(this.selectedDate);
    try {
      const remainingSalary = await this.employeeService.calculateRemainingSalary(mtsal, dateToUse).toPromise();
      console.log("Remaining Salary:", remainingSalary);
      return remainingSalary;
    } catch (error) {
      console.error('Error calculating remaining salary:', error);
      return -1; // Or any other default value
    }
  }

  
  

   
    confirmUpdate1(): void {
      //const allEmployees = this.tab1Data.concat(this.tab2Data);
    
      const dialogData = new ConfirmDialogModel('Confirmation', 'Êtes-vous sûr de vouloir confirmer?');
    
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: '25vw',
        height: '30vh',
        data: dialogData
      });
    
      dialogRef.afterClosed().subscribe(dialogResult => {
        if (dialogResult) {
          const newIdOrg = this.selectedOrganisation1 ? this.selectedOrganisation1.idorg : '';
          console.log("idorg", newIdOrg);
    
          // Filtrer les employés avec un numdoss défini
          console.log(this.numdoss);
          this.updateEmployee(newIdOrg,this.numdoss);
          this.snackBar.open('L affectation du l employee est modifiée avec succées', '×', {
            verticalPosition: 'top',
            duration: 10000,
          
            panelClass: ['success'] 
          });
          window.location.reload();
            
          
        }
      });
    }
    // Méthode pour mettre à jour l'employé avec les détails fournis
    updateEmployee(idorg: string, numdoss: number) {
      this.employeeService.loadAndDisplayLastOccurrence(this.empZy3bDT0, idorg, numdoss).subscribe(
        response => {
          // Gérer la réponse ici si nécessaire
          console.log('Update successful:', response);
        },
        error => {
          // Gérer les erreurs ici
          console.error('Error updating employee:', error);
        }
      );
    }
  getImageSrc(blobData: string): string {
    return blobData === '0' ? 'assets/images/others/vide.jpg' : blobData;
  }

}
