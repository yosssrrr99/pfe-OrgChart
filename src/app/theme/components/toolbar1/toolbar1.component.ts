import { Component, OnInit, Output, EventEmitter, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';

import { AppService } from 'src/app/app.service'; 
import { LoginService } from 'src/app/login.service';
import { NotificationService } from 'src/app/notification.service';
import { CartOverviewComponent } from 'src/app/shared/cart-overview/cart-overview.component'; 
import { ReservationDialogComponent } from 'src/app/shared/reservation-dialog/reservation-dialog.component';
import { Notification, SimulationRecService } from 'src/app/simulation-rec.service';

@Component({
  selector: 'app-toolbar1',
  templateUrl: './toolbar1.component.html', 
  styleUrls: ['./toolbar1.component.scss']
})
export class Toolbar1Component implements OnInit,OnDestroy {
  @Output() onMenuIconClick: EventEmitter<any> = new EventEmitter<any>(); 
  constructor(public appService:AppService,private loginService:LoginService,private router:Router,private notificationService:NotificationService,private simulationRec:SimulationRecService,   private cdr: ChangeDetectorRef) { }
  userRole:String="";
  pendingRequestsCount:number=0;
  notifications:string[]=[];
  notificationsManager: Notification[] = [];
  unreadNotificationsCount = 0;
  intervalId:number;

  notificationsRemm: any[] = [];
  pendingRequestsCountRemm: number = 0;
  ngOnInit() { 
    this.fetchUserRole();
    this.loadMessagesRemm();
    this.simulationRec.getNotifications("123456").subscribe((notifications: Notification[]) => {
      this.notificationsManager = notifications;
    });
    this.intervalId=window.setInterval(()=>{
      this.refreshData();
    },10000);
    
   
  }


  loadMessagesRemm(): void {
    this.notificationService.getMessagesRemm().subscribe(
      (data: string[]) => {
        this.notificationsRemm = data;
        console.log(data);
        this.pendingRequestsCountRemm = data.length;
      },
      error => {
        console.error('Error fetching messages', error);
      }
    );
  }


  fetchUnreadNotificationsCount(idManager: string): void {
    this.simulationRec.getUnreadNotificationCount(idManager).subscribe(
      count => {
        this.unreadNotificationsCount = count;
       
      },
      error => {
        console.error('Error fetching unread notifications count:', error);
      }
    );
  }
  ngOnDestroy(): void {
    if(this.intervalId){
      clearInterval(this.intervalId);
    }
  }
  refreshData(): void {
    console.log('refresh data');
    this.loadStatusRecMessages();
    
  }

 
  

  

  public sidenavToggle(){
    this.onMenuIconClick.emit();
  }
  public openCart(){ 
    this.appService.openCart(CartOverviewComponent)
  }
  public reservation(){ 
    this.appService.makeReservation(ReservationDialogComponent, null, true);   
  }

  loadStatusRecMessages(): void {
    this.notificationService.getStatusRecMessages().subscribe(
      (data: string[]) => {
        this.notifications = data;
        this.unreadNotificationsCount = data.length;
     //   this.router.navigate(['/history'])
      },
      error => {
        console.error('Error fetching statusRec messages', error);
      }
    );
  }

  handleNotificationClick(notification: string, type: string): void {
    if (type === 'statusRec') {
      this.notificationService.clearMessageRec(notification).subscribe(
        () => {
          this.notifications = this.notifications.filter(msg => msg !== notification);
          this.pendingRequestsCount = this.notifications.length;
          this.router.navigate(['/history']);
        },
        error => {
          console.error('Error clearing message', error);
        }
      );
    } else if (type === 'statusRemm') {
      this.notificationService.clearMessageRemm(notification).subscribe(
        () => {
          this.notificationsRemm = this.notificationsRemm.filter(msg => msg !== notification);
          this.pendingRequestsCountRemm = this.notificationsRemm.length;
          this.router.navigate(['/history-rem']);
        },
        error => {
          console.error('Error clearing message', error);
        }
      );
    }
  }


  clearAllMessages(type: string): void {
    if (type === 'statusRec') {
      this.notificationService.clearAllMessagesRec().subscribe(
        () => {
          this.notifications = [];
          this.pendingRequestsCount = 0;
        },
        error => {
          console.error('Error clearing all messages', error);
        }
      );
    } else if (type === 'statusRemm') {
      this.notificationService.clearAllMessagesRemm().subscribe(
        () => {
          this.notificationsRemm = [];
          this.pendingRequestsCountRemm = 0;
        },
        error => {
          console.error('Error clearing all messages', error);
        }
      );
    }
  }



  navigateToRequests(): void {
    this.router.navigate(['/validate']);
  }

  logout(): void {
    this.loginService.logout()
    this.router.navigate(['/login']); 
      
  }

  
  fetchUserRole(): void {
    this.loginService.getRole().subscribe(
      (role: string) => {
        this.userRole = role; // Store the retrieved role in the component's variable
        
        console.log('Rôle de l\'utilisateur:', this.userRole);
        // You can now use this.userRole in your Angular application
      },
      (error) => {
        console.error('Erreur lors de la récupération du rôle de l\'utilisateur:', error);
      }
    );
  }
  
}