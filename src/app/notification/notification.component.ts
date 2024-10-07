import { Component, OnInit, ViewChild } from '@angular/core';
import { MatMenuTrigger } from '@angular/material/menu';
import { Router } from '@angular/router';

import { NotificationService } from 'src/app/notification.service';
import { BudgetService } from '../budget.service';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss']
})
export class NotificationComponent implements OnInit {
  notifications: any[] = [];
  notificationsRem: any[] = [];
  pendingRequestsCount: number = 0;
  pendingRequestsCountRem: number = 0;

  @ViewChild(MatMenuTrigger) menuTrigger: MatMenuTrigger;

  constructor(
    private notificationService: NotificationService,
    private router: Router,
    private budgetService: BudgetService
  ) { }

  ngOnInit(): void {
    this.loadMessages();
    this.loadMessagesRem();
  }

  loadMessages(): void {
    this.notificationService.getMessages().subscribe(
      (data: string[]) => {
        this.notifications = data;
        console.log(data);
        this.pendingRequestsCount = data.length;
      },
      error => {
        console.error('Error fetching messages', error);
      }
    );
  }

  loadMessagesRem(): void {
    this.notificationService.getMessagesRem().subscribe(
      (data: string[]) => {
        this.notificationsRem = data;
        console.log(data);
        this.pendingRequestsCountRem = data.length;
      },
      error => {
        console.error('Error fetching messagesRem', error);
      }
    );
  }

  toggleNotifications(): void {
    if (this.menuTrigger.menuOpen) {
      this.menuTrigger.closeMenu();
    } else {
      this.menuTrigger.openMenu();
    }
  }

  handleNotificationClick(notification: string, type: string): void {
    if (type === 'notifications') {
      this.notificationService.clearMessage(notification).subscribe(
        () => {
          this.notifications = this.notifications.filter(msg => msg !== notification);
          this.pendingRequestsCount = this.notifications.length;
          this.router.navigate(['/validate']);
        },
        error => {
          console.error('Error clearing message', error);
        }
      );
    } else if (type === 'statusRem') {
      this.notificationService.clearMessageRem(notification).subscribe(
        () => {
          this.notificationsRem = this.notificationsRem.filter(msg => msg !== notification);
          this.pendingRequestsCountRem = this.notificationsRem.length;
          this.router.navigate(['/validate-rem']);
        },
        error => {
          console.error('Error clearing message', error);
        }
      );
    }
  }

  clearAllMessages(type: string): void {
    if (type === 'notifications') {
      this.notificationService.clearAllMessages().subscribe(
        () => {
          this.notifications = [];
          this.pendingRequestsCount = 0;
        },
        error => {
          console.error('Error clearing all messages', error);
        }
      );
    } else if (type === 'statusRem') {
      this.notificationService.clearAllMessagesRem().subscribe(
        () => {
          this.notificationsRem = [];
          this.pendingRequestsCountRem = 0;
        },
        error => {
          console.error('Error clearing all messages', error);
        }
      );
    }
  }
}
