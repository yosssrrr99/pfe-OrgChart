import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../../shared/shared.module';
import { PipesModule } from '../../../theme/pipes/pipes.module';
import { DocumentmanagerComponent } from './documentmanager.component';



export const routes: Routes = [
  { path: '', component: DocumentmanagerComponent, pathMatch: 'full' },

];

@NgModule({
  declarations: [
      DocumentmanagerComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedModule,
    PipesModule
  ]
})
export class MenuManagerModule { }
