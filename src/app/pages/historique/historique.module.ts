import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';

import { FormsModule } from '@angular/forms';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { HistoriqueComponent } from './historique.component';


export const routes: Routes = [
  { path: '', component: HistoriqueComponent, pathMatch: 'full' }
];

@NgModule({
  declarations: [HistoriqueComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedModule,
    FormsModule,
    DragDropModule
  ]
})
export class HistoriqueModule { }
