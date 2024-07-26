import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClient, HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http'; 
import { DatePipe } from '@angular/common';

import { NgProgressModule } from 'ngx-progressbar';
import { NgProgressHttpModule } from 'ngx-progressbar/http'; 
import { GoogleMapsModule } from '@angular/google-maps'; 

import { environment } from 'src/environments/environment';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader'; 
export function HttpLoaderFactory(httpClient: HttpClient) { 
  return new TranslateHttpLoader(httpClient, environment.url +'/assets/i18n/', '.json');
}
import { MatPaginatorIntl } from '@angular/material/paginator';
import { MatPaginatorI18nService } from './theme/utils/mat-paginator-i18n.service';

import { OverlayContainer } from '@angular/cdk/overlay';
import { CustomOverlayContainer } from './theme/utils/custom-overlay-container';
import { AppInterceptor } from './theme/utils/app-interceptor';

import { AppRoutingModule } from './app-routing.module'; 
import { SharedModule } from './shared/shared.module'; 


import { AppComponent } from './app.component';
import { AppSettings } from './app.settings';
import { OrganizationChartModule } from 'primeng/organizationchart';
import { PagesComponent } from './pages/pages.component';
import { NotFoundComponent } from './pages/not-found/not-found.component'; 
import { Toolbar1Component } from './theme/components/toolbar1/toolbar1.component';
import { UserMenuComponent } from './theme/components/user-menu/user-menu.component';   
import { HorizontalMenuComponent } from './theme/components/menu/horizontal-menu/horizontal-menu.component';
import { VerticalMenuComponent } from './theme/components/menu/vertical-menu/vertical-menu.component';
import { FooterComponent } from './theme/components/footer/footer.component'; 
import { LockScreenComponent } from './pages/lock-screen/lock-screen.component';
import { FormsModule } from '@angular/forms';
import { HorizontalMenuManagerComponent } from './theme/components/menu/horizontal-menu-manager/horizontal-menu-manager.component';
import { NotificationComponent } from './notification/notification.component';




@NgModule({
  declarations: [
    AppComponent,
    PagesComponent,
    NotFoundComponent,
    UserMenuComponent,   
    Toolbar1Component,
    HorizontalMenuComponent,
    VerticalMenuComponent,
    HorizontalMenuManagerComponent,
    FooterComponent,
    LockScreenComponent,
    NotificationComponent
  ],
  imports: [
    BrowserModule.withServerTransition({ appId: 'serverApp' }), 
    BrowserAnimationsModule,
    HttpClientModule,
    NgProgressModule,
    FormsModule,
    OrganizationChartModule,
    NgProgressHttpModule,
    GoogleMapsModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    AppRoutingModule,
    SharedModule    
  ],
  providers: [ 
    AppSettings,
    { provide: OverlayContainer, useClass: CustomOverlayContainer },
    { provide: HTTP_INTERCEPTORS, useClass: AppInterceptor, multi: true },
    DatePipe,
    { provide: MatPaginatorIntl, useClass: MatPaginatorI18nService }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
