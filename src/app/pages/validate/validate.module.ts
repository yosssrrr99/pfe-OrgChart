import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';

import { FormsModule } from '@angular/forms';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { ValidateComponent } from './validate.component';



export const routes: Routes = [
  { path: '', component: ValidateComponent, pathMatch: 'full' }
];

@NgModule({
  declarations: [ValidateComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedModule,
    FormsModule,
    DragDropModule
  ]
})
export class ValidateModule { }
