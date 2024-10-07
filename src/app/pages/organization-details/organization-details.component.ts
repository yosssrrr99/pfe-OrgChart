import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TreeNode } from 'primeng/api';
import { ApiService } from 'src/app/api.service';

@Component({
  selector: 'app-organization-details',
  templateUrl: './organization-details.component.html',
  styleUrls: ['./organization-details.component.scss']
})
export class OrganizationDetailsComponent implements OnInit {
  nodes: TreeNode[] = [];

  

constructor(private apiService: ApiService) { }

ngOnInit() {
  this.apiService.data$.subscribe(data => {
    if (data) {
      this.nodes = data;
      console.log('Received data:', this.nodes); // Debugging output
    } else {
      console.error('No data received');
    }
  });
}



  getImageSrc() {
    return 'assets/images/others/vide.jpg'; // Example, adjust as needed
  }
}
