import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

import { BudgetService } from 'src/app/budget.service';
import { Employee } from 'src/app/employee-service';
import { ConfirmDialogComponent, ConfirmDialogModel } from 'src/app/shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-update',
  templateUrl: './update.component.html',
  styleUrls: ['./update.component.scss']
})
export class UpdateComponent {
  updateBudget: any = 5000;
  data: Employee[] = [];
  departmentId: string = '123456';
  organizationName: string = '';
  private _budgetGlobal1: number = 0;
  isBlinking: boolean = false;
  isClicked: boolean = false;

  dynamicRows: any[] = [{ number: 0, class: 'junior' }];


  gab1: number = 0;
  result: any;

  constructor(
    private budgetService: BudgetService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar,
    public router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.startBlinking();
    this.getEmployeesByIdOrg();
  }

  get budgetGlobal1(): number {
    return this._budgetGlobal1;
  }

  set budgetGlobal1(value: number) {
    this._budgetGlobal1 = value;
    this.calculateBudget(); // Appeler calculateBudget lorsque budgetGlobal1 change
  }

  

  handleClick() {
    this.isClicked = true;
  }

  getEmployeesByIdOrg(): void {
    this.budgetService.getEmployeesByDepartment(this.departmentId).subscribe((employees: any[]) => {
      this.dynamicRows = employees.map(emp => ({
        number: emp.number,
        class: emp.classification
      }));

      const firstEmployee = employees[0]; // Assuming you expect a single object
      if (firstEmployee) {
        this.budgetGlobal1 = firstEmployee.budgetGlobal;
        this.organizationName = firstEmployee.idorg;
        this.gab1 = firstEmployee.gab; 
      }
      console.log(this.gab1);

      this.calculateBudget();
    });
  }

  onKeyPress(event: KeyboardEvent, index: number): void {
    if (event.key === 'Enter') {
      this.calculateBudget();
    }
  }

  onInputChange(index: number): void {
    this.calculateBudget();
  }

  onClassChange(selectedClass: string, index: number): void {
    this.dynamicRows[index].class = selectedClass;
    this.calculateBudget();
  }

  addRow(index: number): void {
    this.dynamicRows.splice(index + 1, 0, { number: 0, class: 'junior' });
    this.calculateBudget();
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

  removeRow(index: number): void {
    if (this.dynamicRows.length > 1) {
      this.dynamicRows.splice(index, 1);
      this.calculateBudget();
    }
  }

  onEmployeeChange(): void {
    this.calculateBudget();
  }

  calculateBudget(): void {
    const employees = this.dynamicRows.map(row => ({
      number: row.number,
      classification: row.class
    }));

    this.result = this.budgetService.calculateBudget(employees, this.budgetGlobal1);
 
   this.gab1 = this.result.gab;
   console.log('Calculated gab:', this.result.gab);
  console.log('Updated gab1:', this.gab1);
    this.cdr.detectChanges();
  }

  isFormValid(): boolean {
    return this.budgetGlobal1 !== 0 && this.dynamicRows.some(row => row.number !== 0);
  }

  confirm(): void {
    if (!this.isFormValid()) {
      this.snackBar.open('Veuillez remplir toutes les sélections de classification.', '×', { panelClass: 'warning', duration: 5000 });
      return;
    }
    const dialogData = new ConfirmDialogModel('Modifier', 'Êtes-vous sûr de vouloir modifier?');

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '25vw',
      height: '30vh',
      data: dialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const employees = this.dynamicRows.map(row => ({
          number: row.number,
          classification: row.class
        }));

        const updateRecRequest = {
          employees: employees,
          budgetGlobal: this.budgetGlobal1,
          gab: this.gab1
        };
        console.log(updateRecRequest);

        const idorg = this.departmentId.trim();
        this.budgetService.updateEmployeesAndBudget(updateRecRequest, idorg).subscribe(
          response => {
            this.snackBar.open('Envelope modifiée avec succès!', '×', { panelClass: 'success', duration: 5000 });
            this.router.navigate(['/history']);
          },
          error => {
            console.error('Error updating data:', error);
          }
        );
      }
    });
  }

  getImageSrc(blobData: string): string {
    return blobData === '0' ? 'assets/images/others/vide.jpg' : blobData;
  }

  cancelEnvelope(): void {
    const dialogData = new ConfirmDialogModel('Confirmation', 'Êtes-vous sûr de vouloir annuler cette enveloppe?');

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '25vw',
      height: '30vh',
      data: dialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const idorg = this.departmentId.trim();
        this.budgetService.deleteEnvelope(idorg).subscribe(
          response => {
            this.snackBar.open('Envelope deleted successfully', 'Fermer', { duration: 3000 });
            this.router.navigate(['/history']);
          },
          error => {
            console.error('Error deleting envelope:', error);
            const errorMessage = error.error ? error.error : "Une erreur s'est produite lors de la suppression de l'enveloppe.";
            this.snackBar.open(errorMessage, 'Fermer', { duration: 3000 });
          }
        );
      }
    });
  }
}
