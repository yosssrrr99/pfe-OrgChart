import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { TreeModule } from 'primeng/tree'; // Assurez-vous que vous importez TreeModule
import { DragDropModule } from '@angular/cdk/drag-drop';
import { TreeMapModule } from '@swimlane/ngx-charts';
import { OrganizationChartModule } from 'primeng/organizationchart';
import { OrganizationDetailsComponent } from './organization-details.component';

export const routes: Routes = [
  { path: '', component: OrganizationDetailsComponent, pathMatch: 'full' }
];

@NgModule({
  declarations: [OrganizationDetailsComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedModule,
    TreeMapModule,
    OrganizationChartModule,
    TreeModule, // Assurez-vous d'importer TreeModule ici
    DragDropModule
  ]
})
export class OrganizationDetailsModule { }
