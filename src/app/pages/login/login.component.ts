import { Component, OnInit } from '@angular/core';
import { UntypedFormGroup, UntypedFormBuilder, Validators} from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router'; 
import { AppSettings, Settings } from 'src/app/app.settings';
import { LoginService } from 'src/app/login.service';

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
  constructor(public fb: UntypedFormBuilder, public router:Router, private sanitizer:DomSanitizer, public appSettings:AppSettings,private loginService:LoginService) { 
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
   this.loginService.login(this.username, this.password).subscribe(
      response => {
        console.log('Login successful:', response);
        this.router.navigate(['/org']);
      }
     
    );
  }

}
