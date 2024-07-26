import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MediaChange, MediaObserver } from '@angular/flex-layout';
import { MatPaginator } from '@angular/material/paginator';
import { error } from 'console';
import { response } from 'express';
import { Observable } from 'rxjs';
import { Subscription } from 'rxjs/internal/Subscription';
import { filter, map } from 'rxjs/operators';
import { MenuItem, Pagination } from 'src/app/app.models';
import { AppService } from 'src/app/app.service';
import { AppSettings, Settings } from 'src/app/app.settings';
import { DocumentService } from 'src/app/document.service';
import { FileService } from 'src/app/file.service';



interface Document {
  userName: string;
  fileName:string;
  category:string;
}


@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {
  @ViewChild('sidenav') sidenav: any;
  public sidenavOpen:boolean = false;
  public showSidenavToggle:boolean = false;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  public menuItems: MenuItem[] = [];
  public categories:any[] = [];
  public documentsList:any[] = [];
  public viewType: string = 'grid';
  public viewCol: number = 25;
  public count: number = 12;
  public sort: string = '';
  public selectedCategoryId:number = 0;
  public pagination:Pagination = new Pagination(1, this.count, null, 2, 0, 0); 
  public message:string | null = '';
  public watcher: Subscription;
  public settings: Settings;
  selectedFileName: string = '';
  userName: string = '';
  category : string='';
  image: Blob;

  selectedFile:any[]=[];
  filenames: string;
  documentList:Document[]=[]
  fileName: string = '';
 

  constructor(public appSettings:AppSettings, public appService:AppService,public fileService:FileService, public mediaObserver: MediaObserver, private httpClient:HttpClient,private documentService: DocumentService) {
    this.settings = this.appSettings.settings; 
    this.watcher = mediaObserver.asObservable()
    .pipe(filter((changes: MediaChange[]) => changes.length > 0), map((changes: MediaChange[]) => changes[0]))
    .subscribe((change: MediaChange) => {
      if(change.mqAlias == 'xs') {
        this.sidenavOpen = false;
        this.showSidenavToggle = true;
        this.viewCol = 100;
      }
      else if(change.mqAlias == 'sm'){
        this.sidenavOpen = false;
        this.showSidenavToggle = true;
        this.viewCol = 50;
      }
      else if(change.mqAlias == 'md'){
        this.sidenavOpen = false;
        this.showSidenavToggle = false;
        this.viewCol = 33.3;
      }
      else{
        this.sidenavOpen = false;
        this.showSidenavToggle = false;
        this.viewCol = 25;
      }
    });


  }

  ngOnInit(): void {
    
    this.selectCategory("all");
    
   
  }
  public async getDocumentList(): Promise<Document[]> {
    try {
      return await this.httpClient.get<Document[]>("http://localhost:9095/Cool/api/files/all").toPromise();
    } catch (error) {
      throw error;
    }
  }
  

  loadImage(filename: string): void {
    this.documentService.getImage(filename).subscribe(
      data => {
        this.image = data;
        // Faites quelque chose avec l'image, par exemple l'afficher dans une balise <img>
      },
      error => {
        console.error('Erreur lors du chargement de l\'image:', error);
      }
    );
  }
  

 
  
  ngOnDestroy(){ 
    this.watcher.unsubscribe();
  }


  public async selectCategory(category: string): Promise<void> {
    if (category === 'all') {
      // Si la catégorie sélectionnée est "Others" ou "All Documents",
      // récupérez tous les documents sans filtrer par catégorie
      try {
        this.documentsList = await this.getDocumentList();
        console.log('Documents récupérés :', this.documentsList);
      } catch (error) {
        console.error('Une erreur est survenue lors de la récupération des documents :', error);
      }
    } else {
      // Sinon, récupérez les documents pour la catégorie spécifiée
      this.documentService.getDocumentByCategory(category).subscribe(
        (documents) => {
          // Affectez les documents récupérés à la variable
          this.documentsList = documents;
          console.log('Documents récupérés :', this.documentsList);
        },
        (error) => {
          console.error('Une erreur est survenue lors de la récupération des documents :', error);
        }
      );
    }
  }
  

  public onChangeCategory(event: any): void { 
    const selectedCategory = event.value;

    switch (selectedCategory) {
      case 0:
        this.selectCategory('all');
        break;
      case 1:
        this.selectCategory('certificat');
        break;
      case 2:
        this.selectCategory('cv');
        break;
      case 3:
        this.selectCategory('assignment letter');
        break;
      
        default:
          // Gérer les cas non prévus
          console.warn('Catégorie non gérée :', selectedCategory);
          break;
    }
  }


  

  public getMenuItems(){
    this.appService.getMenuItems().subscribe(data => {
      // this.menuItems = this.appService.shuffleArray(data);
      // this.menuItems = data;
      let result = this.filterData(data); 
      if(result.data.length == 0){
        this.menuItems.length = 0;
        this.pagination = new Pagination(1, this.count, null, 2, 0, 0);  
        this.message = 'No Results Found'; 
      } 
      else{
        this.menuItems = result.data; 
        this.pagination = result.pagination;
        this.message = null;
      } 
    })
  }  

  public resetPagination(){ 
    if(this.paginator){
      this.paginator.pageIndex = 0;
    }
    this.pagination = new Pagination(1, this.count, null, null, this.pagination.total, this.pagination.totalPages);
  }

  public filterData(data:any){
    return this.appService.filterData(data, this.selectedCategoryId, this.sort, this.pagination.page, this.pagination.perPage);
  }
  // public filterData(data){
  //   return this.appService.filterData(data, this.searchFields, this.sort, this.pagination.page, this.pagination.perPage);
  // }

  public changeCount(count:number){
    this.count = count;   
    this.menuItems.length = 0;
    this.resetPagination();
    this.getMenuItems();
  }
  public changeSorting(sort:any){    
    this.sort = sort; 
    this.menuItems.length = 0;
    this.getMenuItems();
  }
  public changeViewType(obj:any){ 
    this.viewType = obj.viewType;
    this.viewCol = obj.viewCol; 
  } 


  public onPageChange(e:any){ 
    this.pagination.page = e.pageIndex + 1;
    this.getMenuItems();
    window.scrollTo(0,0);  
  }
  onFileSelected(event:any){
   const file=this.selectedFile = Array.from(event.target.files);
   this.selectedFileName = this.selectedFile.map(file => file.name).join(', ');

    
  }
  save():void{
    this.selectedFile.forEach(file => {
      const formData = new FormData();
      formData.append("files", file);
      formData.append("userName", this.userName);
      formData.append("category", this.category);
      this.httpClient.post("http://localhost:9095/Cool/api/files", formData).subscribe(
        response => {
          console.log("File uploaded successfully:", response);
          this.selectCategory("all");
        },
        error => {
          console.error("Error uploading file:", error);
        }
      );
    });

  }
 
  isImage(fileName: string): boolean {
    const imageExtensions = ['jpg', 'jpeg', 'png', 'gif'];
    const extension = fileName.split('.').pop()?.toLowerCase();
    return extension ? imageExtensions.includes(extension) : false;
}


downloadFile(blobName: string): void {
  this.documentService.downloadFile(blobName).subscribe(
    (blob: Blob) => {
      // Créez un objet URL pour le blob
      const url = window.URL.createObjectURL(blob);
      
      // Créez un élément a pour le téléchargement
      const a = document.createElement('a');
      a.href = url;
      a.download = blobName;
      
      // Ajoutez l'élément a au document
      document.body.appendChild(a);

      
      // Déclenchez le téléchargement
      a.click();
      
    },
    error => {
      console.error('Erreur lors du téléchargement du fichier :', error);
    }
  );
}
deleteFile(filename: string): void {
  this.documentService.deleteFile(filename).subscribe(
    () => {
      this.selectCategory("all");
      console.log('File deleted successfully');
    },
    (error) => {
      console.error('Error deleting file:', error);
    }
  );
}
} 