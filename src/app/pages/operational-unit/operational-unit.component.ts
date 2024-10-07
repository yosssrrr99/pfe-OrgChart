import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { TreeNode } from 'primeng/api';
import { ApiService } from 'src/app/api.service';

@Component({
  selector: 'app-operational-unit',
  templateUrl: './operational-unit.component.html',
  styleUrls: ['./operational-unit.component.scss']
})
export class OperationalUnitComponent implements OnInit {
  @ViewChild('overlayContainer') overlayContainer!: ElementRef;
  selectedNodes!: TreeNode[];
  selectedNode: TreeNode | null = null;
  searchEmployee: any;
  data: TreeNode[] = [];
  nodes: TreeNode[] = [];
  managerEmployeeNodes: TreeNode[] = []; // For secondary chart
  searchOrganization: string = ''; // Search term for organization ID
  showOverlayFlag: boolean = false;
  overlayPosition: { left: number, top: number } = { left: 0, top: 0 };
  overlayDetails: { surname: string, firstname: string, position: string } | null = null;
  filteredNodes: TreeNode[] = []; // Filtered nodes
  constructor(private apiService: ApiService, private router: Router) { }

  ngOnInit() {
    this.apiService.getHierarchy().subscribe(
      (response: any) => {
        if (response) {
          this.nodes = this.transformData2(response);
          console.log('Nodes:', this.nodes); // Check the nodes array
        } else {
          console.error('Expected data but received:', response);
        }
      },
      error => {
        console.error('Error fetching hierarchy data', error);
      }
    );
  }

  transformData2(rootNode: any): TreeNode[] {
    if (!rootNode || !rootNode.id) {
      console.error('Invalid root node:', rootNode);
      return [];
    }
  
    // Function to recursively create TreeNode objects
    const createNode = (item: any): TreeNode => ({
      expanded: true,
      type: 'person',
      styleClass: 'myClass',
      data: {
        idorg: item.id,
        nameEmployees: item.nameEmployees ? item.nameEmployees.map((employee: string) => {
          // Extract only the name part (e.g., "John Doe")
          const parts = employee.split(',');
          const surname = parts[0]?.trim() || '';
          const firstname = parts[1]?.trim() || '';
          return `${firstname} ${surname}`.trim(); // Only name, no position
        }) : [],
        nameManager: item.nameManager ? item.nameManager.split(',').slice(0, 2).reverse().join(' ').trim() : '', // Extract and reverse for name
        title: '', // Ensure title is empty
        image: 'assets/images/others/vide.jpg'
      },
      children: item.children ? item.children.map(createNode) : []
    });
  
    // Create a TreeNode from the rootNode
    return [createNode(rootNode)];
  }

  transformData(rootNode: any): TreeNode[] {
    if (!rootNode || !rootNode.id) {
      console.error('Invalid root node:', rootNode);
      return [];
    }

    // Function to recursively create TreeNode objects
    const createNode = (item: any): TreeNode => ({
      expanded: true,
      type: 'person',
      styleClass: 'myClass',
      data: {
        idorg: item.id,
        nameEmployees: item.nameEmployees || [],
        nameManager: item.nameManager,
        title: '',
        image: 'assets/images/others/vide.jpg'
      },
      children: item.children ? item.children.map(createNode) : []
    });

    // Create a TreeNode from the rootNode
    return [createNode(rootNode)];
  }

  filterNodes() {
    this.filteredNodes = this.nodes.filter(node =>
      this.isMatch(node, this.searchEmployee)
    );
  }


  
  
  

  showOverlay(event: MouseEvent, node: TreeNode) {
    this.selectedNode = node;
    this.showOverlayFlag = true;
    this.overlayPosition = { left: event.clientX, top: event.clientY };
    
    // Extract employee details for the overlay
    if (node.data.nameEmployees && node.data.nameEmployees.length > 0) {
      const employee = node.data.nameEmployees[0];
      const [surname, firstname, position] = employee.split(',');

      this.overlayDetails = {
        surname: surname.trim() || 'No Surname',
        firstname: firstname.trim() || 'No Firstname',
        position: position.trim() || 'No Position'
      };
    } else {
      this.overlayDetails = null;
    }
  }

  hideOverlay() {
    this.showOverlayFlag = false;
    this.overlayDetails = null;
  }

  getImageSrc() {
    return 'assets/images/others/vide.jpg'; // Example, adjust as needed
  }

  onNodeSelect(event: any) {
    const node = event.node;
    const dataToPass = this.createManagerEmployeeNodes(node);
    this.apiService.setData(dataToPass);
    this.router.navigate(['/organization-details']);
  }

  createManagerEmployeeNodes(node: TreeNode): TreeNode[] {
    if (!node || !node.data) {
      return [];
    }

    const nodes: TreeNode[] = [];

    // Create the organization node with the manager's name
    const organizationNode: TreeNode = {
      expanded: true,
      type: 'person',
      styleClass: 'myClass',
      data: {
        idorg: node.data.idorg,
        nameEmployee: node.data.idorg || 'No ID',
        title: node.data.nameManager || 'No Manager',
        image: 'assets/images/others/vide.jpg'
      },
      children: []
    };

    // Add the organization node to the main nodes
    nodes.push(organizationNode);

    // Create the manager node if there is a manager
    if (node.data.nameManager) {
      const [surname, firstname, position] = node.data.nameManager.split(',');

      const managerNode: TreeNode = {
        expanded: true,
        type: 'person',
        styleClass: 'myClass',
        data: {
          idorg: node.data.idorg,
          nameEmployee: `${surname}`, // Display name and surname
          title: position || 'Manager',
          image: 'assets/images/others/vide.jpg',
          positionEmployee: position || 'Manager', // For overlay
          surnameEmployee: surname || 'No Surname', // For overlay
          firstnameEmployee: firstname || 'No Firstname' // For overlay
        },
        children: []
      };

      // Add the manager node as a child of the organization node
      organizationNode.children.push(managerNode);

      // Create employee nodes if there are employees
      if (node.data.nameEmployees && node.data.nameEmployees.length > 0) {
        const employeeNodes: TreeNode[] = node.data.nameEmployees.map((employee: string) => {
          const [surname, firstname, position] = employee.split(',');

          return {
            expanded: true,
            type: 'person',
            styleClass: 'employee-node',
            data: {
              idorg: node.data.idorg,
              nameEmployee: `${surname}`, // Display name and surname
              title: position || 'Employee',
              image: 'assets/images/others/vide.jpg',
              positionEmployee: position || 'Employee', // For overlay
              surnameEmployee: surname || 'No Surname', // For overlay
              firstnameEmployee: firstname || 'No Firstname' // For overlay
            },
            children: []
          };
        });

        // Add employee nodes as children of the manager node
        managerNode.children = employeeNodes;
      }
    }

    return nodes;
  }


  isMatch(node: TreeNode, searchTerm: string): boolean {
    if (!searchTerm || searchTerm.trim() === '') {
      return true;
    }
    if (node.data.idorg.toLowerCase().includes(searchTerm.toLowerCase())) {
      return true;
    }
     if (node.data.nameManager.toLowerCase().includes(searchTerm.toLowerCase())) {
      return true;
    }
    if (node.children) {
      for (const child of node.children) {
        if (this.isMatch(child, searchTerm)) {
          return true;
        }
      }
    }
    return false;
  }
}
