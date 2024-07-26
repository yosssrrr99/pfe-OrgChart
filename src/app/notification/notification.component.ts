import { Component, OnInit, ViewChild } from '@angular/core';
import { MatMenuTrigger } from '@angular/material/menu';
import { Router } from '@angular/router';

import { NotificationService } from 'src/app/notification.service';
import { BudgetService, EmployeeRec } from '../budget.service';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss']
})
export class NotificationComponent implements OnInit {
  notifications: any[] = [];
  pendingRequestsCount: number = 0;

  @ViewChild(MatMenuTrigger) menuTrigger: MatMenuTrigger;

  constructor(private notificationService: NotificationService, private router: Router, private budgetService: BudgetService) { }

  ngOnInit(): void {
    this.loadNotifications();
    this.getPendingRequestsCount();
  }

  loadNotifications(): void {
    this.notificationService.getManagersByStatus().subscribe(
      (data: any[]) => {
        this.notifications = data;
        console.log(data);
      },
      (error) => {
        console.error('Erreur lors de la récupération des notifications:', error);
      }
    );
  }

  getPendingRequestsCount(): void {
    this.notificationService.getPendingRequestsCount().subscribe(
      (count: number) => {
        this.pendingRequestsCount = count;
      },
      (error) => {
        console.error('Erreur lors de la récupération du nombre de demandes en cours:', error);
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

  navigateToRequest(notification: any): void {
    const managerId = notification; // Utiliser le premier élément du tableau
    this.router.navigate(['/validate'], { queryParams: { managerId: managerId } });
  }
}