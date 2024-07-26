import { Component, OnInit, Input, ViewChildren, QueryList } from '@angular/core';
import { MenuService } from '../menu.service';
import { Menu } from '../menu.model';
import { MatMenuTrigger } from '@angular/material/menu';

@Component({
  selector: 'app-vertical-menu',
  templateUrl: './vertical-menu.component.html',
  styleUrls: ['./vertical-menu.component.scss'],
  providers: [ MenuService ]
})
export class VerticalMenuComponent  implements OnInit {
  @Input('menuParentId') menuParentId = 0;
  public menuItems: Array<Menu> = []; 
  @ViewChildren(MatMenuTrigger) triggers!: QueryList<MatMenuTrigger>;

  constructor(public menuService:MenuService) { }

  ngOnInit() {
    this.menuItems = this.menuService.getVerticalMenuItems(); // Remplacez cela par votre propre logique de récupération des items de menu vertical
    this.menuItems = this.menuItems.filter(item => item.parentId == this.menuParentId);
  }

  public closeOthers(trigger: MatMenuTrigger) {
    const currentIndex: number = this.triggers.toArray().findIndex(t => t == trigger);
    this.triggers.forEach((menu, index) => {
      if (index != currentIndex) {
        menu.closeMenu();
      }
    });
  }


}
