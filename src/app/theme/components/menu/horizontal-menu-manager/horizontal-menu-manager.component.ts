import { Component, Input, OnInit, QueryList, ViewChildren } from '@angular/core';
import { MenuService } from '../menu.service';
import { MatMenuTrigger } from '@angular/material/menu';
import { Menu } from '../menu.model';

@Component({
  selector: 'app-horizontal-menu-manager',
  templateUrl: './horizontal-menu-manager.component.html',
  styleUrls: ['./horizontal-menu-manager.component.scss']
})
export class HorizontalMenuManagerComponent implements OnInit {
  @Input('menuParentId') menuParentId = 0;
  public menuItems: Array<Menu> = []; 
  @ViewChildren(MatMenuTrigger) triggers!: QueryList<MatMenuTrigger>;

  constructor(public menuService:MenuService) { }

  ngOnInit() {
   // this.menuItems = this.menuService.getgetHorizontalMenuItemsManger();
    this.menuItems = this.menuItems.filter(item => item.parentId == this.menuParentId); 
  }

  public closeOthers(trigger:MatMenuTrigger){ 
    const currentIndex: number = this.triggers.toArray().findIndex(t => t == trigger); 
    this.triggers.forEach((menu, index) => {
      if(index != currentIndex){
        menu.closeMenu();
      }
    });
  }

}
