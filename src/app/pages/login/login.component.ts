import { Component, OnInit } from '@angular/core';
import { UntypedFormGroup, UntypedFormBuilder, Validators} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router'; 
import { AppSettings, Settings } from 'src/app/app.settings';
import { LoginService } from 'src/app/login.service';
import { AlertDialogComponent } from 'src/app/shared/alert-dialog/alert-dialog.component';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
   username!:"";
   password!:"";
  public loginForm!: UntypedFormGroup;
  public hide = true;
  public bgImage:any;
  public settings: Settings;
  constructor(public fb: UntypedFormBuilder, public router:Router, private sanitizer:DomSanitizer, public appSettings:AppSettings,private loginService:LoginService,private dialog:MatDialog) { 
    this.settings = this.appSettings.settings; 
  }

  ngOnInit(): void {
    this.bgImage = this.sanitizer.bypassSecurityTrustStyle('url(assets/images/others/bud.jpg)');
    this.loginForm = this.fb.group({
      username: [null, Validators.compose([Validators.required, Validators.minLength(6)])],
      password: [null, Validators.compose([Validators.required, Validators.minLength(6)])],
      rememberMe: false
    });

  }


  login() {
    const request = {
      username: this.username,
      password: this.password
    };

    this.loginService.logina(request).subscribe({
      next: (response) => {
        console.log('Login successful, token:', response.token);
        console.log('Role:', response.role);
        // Stocker le token dans le stockage local ou gérer la redirection
        localStorage.setItem('authToken', response.token);
        this.router.navigate(['']); // Redirection après connexion réussie
      },
      error: (err) => {
        console.error('Login failed:', err);
        this.dialog.open(AlertDialogComponent, {
          width: '25vw',
          height: '20vh',
          data: 'Identifiant ou mot de passe incorrect !'
      });
        // Afficher une erreur de conneqxion si nécessaire
      }
    });
  }
  }


