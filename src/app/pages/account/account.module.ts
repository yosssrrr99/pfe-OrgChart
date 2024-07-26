import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { AccountComponent } from './account.component';
import { ProfileComponent } from './profile/profile.component';
import { PasswordChangeComponent } from './password-change/password-change.component';



export const routes: Routes = [
  { 
    path: '', 
    component: AccountComponent, children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'profile', component: ProfileComponent },
      { path: 'password-change', component: PasswordChangeComponent },

      
    ]
  }
];


@NgModule({
  declarations: [
    AccountComponent,
    ProfileComponent,
    PasswordChangeComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedModule,
  ]
})
export class AccountModule { }
