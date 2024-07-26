import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';

import { FormsModule } from '@angular/forms';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { RemunerationComponent } from './remuneration.component';


export const routes: Routes = [
  { path: '', component: RemunerationComponent, pathMatch: 'full' }
];

@NgModule({
  declarations: [RemunerationComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedModule,
    FormsModule,
    DragDropModule
  ]
})
export class RemunerationModule { }
