import { Component, OnInit } from '@angular/core';
import { Notification, SimulationRecService } from '../simulation-rec.service';

@Component({
  selector: 'app-notification-manager',
  templateUrl: './notification-manager.component.html',
  styleUrls: ['./notification-manager.component.scss']
})
export class NotificationManagerComponent implements OnInit {
  notifications: Notification[] = [];
  managerId = 'TALAN1PR'; // Update with the actual manager ID

  constructor(private employeeRec: SimulationRecService) { }

  ngOnInit(): void {
    this.getNotifications();
  }

  getNotifications(): void {
    this.employeeRec.getNotifications(this.managerId).subscribe((notifications) => {
      this.notifications = notifications;
    });
  }

 
 
}
