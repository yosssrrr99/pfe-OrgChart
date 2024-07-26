import { Component, ElementRef, ViewChild } from '@angular/core';
import { TreeNode } from 'primeng/api';
import datat from '../../data.json';
import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
@Component({
  selector: 'app-operational-unit',
  templateUrl: './operational-unit.component.html',
  styleUrls: ['./operational-unit.component.scss']
})
export class OperationalUnitComponent {
  @ViewChild('overlayContainer') overlayContainer!: ElementRef;

  selectedNodes!: TreeNode[];
  selectedNode: TreeNode | null = null;
  searchEmployee: any;
  data: TreeNode[] = [datat];

  constructor() {
    const num_levels = 3;

    //fct recursive pour créer noeuds mtaa arbre
    const createTreeNodes = (nodes: any[], level: number): TreeNode[] => {
      if (level >= num_levels) {
        return [];
      }

      return nodes.map(node => ({
        expanded: true,
        type: 'person',
        styleClass: 'myClass',
        data: node.data,
        children: createTreeNodes(node.children || [], level + 1)
      }));
    };

    // Création des nœuds de l'arbre à partir des données JSON (data.json)
    this.data = createTreeNodes([datat], 0);

    console.log(this.data);

    this.truncateNames(this.data);
  }

  //Fixer longueur taa nom l 13 caractéres
  truncateNames(nodes: TreeNode[]): void {
    const maxLength = 13;
    nodes.forEach(node => {
      if (node.data && node.data.name && node.data.name.length > maxLength) {
        node.data.name = node.data.name.substring(0, maxLength) + '...';
      }
      if (node.children) {
        this.truncateNames(node.children);
      }
    });
  }


  //fct pour ajuster position de l'overlay 
  updateOverlayPosition(): void {
    // Mettre à jour la position de l'overlay
    const overlayElement = this.overlayContainer.nativeElement as HTMLElement;
    console.log(this.selectedNodePosition)
    overlayElement.style.left = this.selectedNodePosition.left + 'px';
    overlayElement.style.top = this.selectedNodePosition.top + 'px';
  }


  // Declaration des var pour le suivi de l'état de l'overlay 
  selectedNodePosition: { left: number, top: number } = { left: 0, top: 0 };
  showOverlayFlag: boolean = false;
  overlayPosition: { left: number, top: number } = { left: 0, top: 0 };



  showOverlay(event: MouseEvent, node: any) {
    // Close the previously opened overlay
    if (this.selectedNode && this.selectedNode !== node) {
      this.hideOverlay();
    }
  
    // Set the new selected node
    this.selectedNode = node;
    this.showOverlayFlag = true;
  
    // Set the position of the overlay relative to the mouse position
    this.overlayPosition = { left: event.clientX, top: event.clientY };
  
    // Check if the node has children and if they haven't been loaded yet
    if (node.children && node.children.length === 0) {
      // Assuming you have a method to fetch children data, let's call it fetchChildrenData
      // Replace fetchChildrenData with your actual method to load children data
      this.fetchChildrenData(node).then((children: any[]) => {
        // Add children to the clicked node
        node.children = children.map(child => ({
          expanded: false, // Set to false initially as they are just added
          type: 'person',
          styleClass: 'myClass',
          data: child.data,
          children: [] // Make children empty for the newly added children
        }));
      });
    }
  }
  

  hideOverlay() {
    this.showOverlayFlag = false;
    this.selectedNode = null; // Reset the selected node when hiding the overlay
  }
  
  // Vérifier si le nœud a des enfants et s'ils n'ont pas encore été chargés
  fetchChildrenData(node: any): Promise<any[]> {
   
    return new Promise<any[]>(resolve => {
      // Simulating fetching data with a timeout
      setTimeout(() => {
        const childrenData = [
          {
            data: {
              image: 'https://primefaces.org/cdn/primeng/images/demo/avatar/xuxuefeng.png',
              name: 'Adam Zoe',
              title: 'Financial Analyst',
              dep: 'Finance',
              email: 'adam@gmail.com',
              tel: '24147852',
            },
            children: [
              {
                expanded: true,
                data: {
                  image: 'https://primefaces.org/cdn/primeng/images/demo/avatar/xuxuefeng.png',
                  name: 'Christopher',
                  title: 'Treasury Analyst',
                  dep: 'Finance',
                  email: 'christopher@yahoo.fr',
                  tel: '50000123',
                },
                children: []
              },
              {
                expanded: true,
                type: 'person',
                styleClass: 'myClass',
                data: {
                  image: 'https://primefaces.org/cdn/primeng/images/demo/avatar/elwinsharvill.png',
                  name: 'Amanda',
                  title: 'Finance Manager',
                  dep: 'Finance',
                  email: 'amandasharvil@gmail.com',
                  tel: '96000123',
                },
              }
            ]
          },
          {
            data: {
              image: 'https://www.ibconcepts.com/wp-content/uploads/2021/08/IBC-Colored-Circles_Joe.png',
              name: 'Don Joes',
              title: 'Finance Manager',
              dep: 'Finance Manager',
              email: 'shaw@gmail.com',
              tel: '24147852',
            },
            children: []
          }
        ];
        
        resolve(childrenData);
      }, 1000); // Adjust the timeout as per your actual data fetching requirements
    });
  }
  
 
  isMatch(node: TreeNode, searchTerm: string): boolean {
    // If no searchTerm is provided, return true to include all nodes
    if (!searchTerm || searchTerm.trim() === '') {
      return true;
    }
    // If the current node matches the search term, return true
    if (JSON.stringify(node.data).toLowerCase().includes(searchTerm.toLowerCase())) {
      return true;
    }

    // Recursively search through the children of the current node
    if (node.children) {
      for (const child of node.children) {
        if (this.isMatch(child, searchTerm)) {
          return true;
        }
      }
    }

    // If no match found in the current node or its children, return false
    return false;
  }


  onDrop(event: CdkDragDrop<TreeNode[]>) {
    // Récupérez l'élément glissé et sa position actuelle
    const draggedNode = event.item.data;
    const currentIndex = this.data.indexOf(draggedNode);
  
    // Si l'élément est déposé à l'intérieur de la liste
    if (event.container === event.previousContainer) {
      // Réorganisez l'élément dans la liste
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      // Sinon, transférez l'élément d'une liste à une autre
      transferArrayItem(event.previousContainer.data, event.container.data, event.previousIndex, event.currentIndex);
    }
  }

}
