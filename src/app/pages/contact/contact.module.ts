import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { GoogleMapsModule } from '@angular/google-maps';  
import { ContactComponent } from './contact.component';
import { BudgetChartComponent } from 'src/app/budget-chart/budget-chart.component';

export const routes: Routes = [
  { path: '', component: ContactComponent, pathMatch: 'full'  }
];

@NgModule({
  declarations: [ContactComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedModule, 
    GoogleMapsModule
  ]
})
export class ContactModule { }
