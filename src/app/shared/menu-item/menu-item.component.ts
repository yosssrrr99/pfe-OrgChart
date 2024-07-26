import { HttpClient } from '@angular/common/http';
import { Component, Input, OnInit, SimpleChange } from '@angular/core';
import { error } from 'console';
import { response } from 'express';


interface Document {
  file: string;
  userName: string;
}


@Component({
  selector: 'app-menu-item',
  templateUrl: './menu-item.component.html',
  styleUrls: ['./menu-item.component.scss']
})
export class MenuItemComponent implements OnInit {


  documentList:Document[]=[]
 
  constructor(private httpclient:HttpClient) { }

  ngOnInit(): void {
  
    this.httpclient.get<Document[]>("http://localhost:9095/Cool/user?name=yosr").subscribe(response=>{
      this.documentList=response;
      console.log(this.documentList);
    },error=>{
      console.log("error occured when fetching document")
    });

  }

}
