import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms'; 
import { MatSnackBar } from '@angular/material/snack-bar';
import { ChartData, ChartOptions } from 'chart.js';
import { AppService } from 'src/app/app.service';
import { AppSettings, Settings } from 'src/app/app.settings';
import { emailValidator } from 'src/app/theme/utils/app-validators';

@Component({
    selector: 'app-contact',
    templateUrl: './contact.component.html',
    styleUrls: ['./contact.component.scss']
})
export class ContactComponent implements OnInit {
    budgetSpent:any;
    budgetRemaining:any;
    contactForm!: UntypedFormGroup; 
    center: google.maps.LatLngLiteral = { lat: 40.678178, lng: -73.944158};
    zoom: number = 12;
    markerOptions: google.maps.MarkerOptions = { draggable: false };
    markerPositions: google.maps.LatLngLiteral[] = [
        { lat: 40.678178, lng: -73.944158 }
    ];
    mapStyles:any = [
        {
            "featureType": "all",
            "elementType": "labels.text.fill",
            "stylers": [
                {
                    "saturation": 36
                },
                {
                    "color": "#000000"
                },
                {
                    "lightness": 40
                }
            ]
        },
        {
            "featureType": "all",
            "elementType": "labels.text.stroke",
            "stylers": [
                {
                    "visibility": "on"
                },
                {
                    "color": "#000000"
                },
                {
                    "lightness": 16
                }
            ]
        },
        {
            "featureType": "all",
            "elementType": "labels.icon",
            "stylers": [
                {
                    "visibility": "off"
                }
            ]
        },
        {
            "featureType": "administrative",
            "elementType": "geometry.fill",
            "stylers": [
                {
                    "color": "#000000"
                },
                {
                    "lightness": 20
                }
            ]
        },
        {
            "featureType": "administrative",
            "elementType": "geometry.stroke",
            "stylers": [
                {
                    "color": "#000000"
                },
                {
                    "lightness": 17
                },
                {
                    "weight": 1.2
                }
            ]
        },
        {
            "featureType": "administrative",
            "elementType": "labels.text.fill",
            "stylers": [
                {
                    "color": "#8b9198"
                }
            ]
        },
        {
            "featureType": "landscape",
            "elementType": "geometry",
            "stylers": [
                {
                    "color": "#000000"
                },
                {
                    "lightness": 20
                }
            ]
        },
        {
            "featureType": "landscape",
            "elementType": "geometry.fill",
            "stylers": [
                {
                    "color": "#323336"
                }
            ]
        },
        {
            "featureType": "landscape.man_made",
            "elementType": "geometry.stroke",
            "stylers": [
                {
                    "color": "#414954"
                }
            ]
        },
        {
            "featureType": "poi",
            "elementType": "geometry",
            "stylers": [
                {
                    "color": "#000000"
                },
                {
                    "lightness": 21
                }
            ]
        },
        {
            "featureType": "poi",
            "elementType": "geometry.fill",
            "stylers": [
                {
                    "color": "#2e2f31"
                }
            ]
        },
        {
            "featureType": "road",
            "elementType": "labels.text.fill",
            "stylers": [
                {
                    "color": "#7a7c80"
                }
            ]
        },
        {
            "featureType": "road.highway",
            "elementType": "geometry.fill",
            "stylers": [
                {
                    "color": "#242427"
                },
                {
                    "lightness": 17
                }
            ]
        },
        {
            "featureType": "road.highway",
            "elementType": "geometry.stroke",
            "stylers": [
                {
                    "color": "#202022"
                },
                {
                    "lightness": 29
                },
                {
                    "weight": 0.2
                }
            ]
        },
        {
            "featureType": "road.arterial",
            "elementType": "geometry",
            "stylers": [
                {
                    "color": "#000000"
                },
                {
                    "lightness": 18
                }
            ]
        },
        {
            "featureType": "road.arterial",
            "elementType": "geometry.fill",
            "stylers": [
                {
                    "color": "#393a3f"
                }
            ]
        },
        {
            "featureType": "road.arterial",
            "elementType": "geometry.stroke",
            "stylers": [
                {
                    "color": "#202022"
                }
            ]
        },
        {
            "featureType": "road.local",
            "elementType": "geometry",
            "stylers": [
                {
                    "color": "#000000"
                },
                {
                    "lightness": 16
                }
            ]
        },
        {
            "featureType": "road.local",
            "elementType": "geometry.fill",
            "stylers": [
                {
                    "color": "#393a3f"
                }
            ]
        },
        {
            "featureType": "road.local",
            "elementType": "geometry.stroke",
            "stylers": [
                {
                    "color": "#202022"
                }
            ]
        },
        {
            "featureType": "transit",
            "elementType": "geometry",
            "stylers": [
                {
                    "color": "#000000"
                },
                {
                    "lightness": 19
                }
            ]
        },
        {
            "featureType": "water",
            "elementType": "geometry",
            "stylers": [
                {
                    "color": "#000000"
                },
                {
                    "lightness": 17
                }
            ]
        },
        {
            "featureType": "water",
            "elementType": "geometry.fill",
            "stylers": [
                {
                    "color": "#202124"
                }
            ]
        }
    ]; 
    mapOptions: google.maps.MapOptions = {
        styles: this.appSettings.settings.theme == 'orange-dark' ? this.mapStyles : null,
        fullscreenControl: true, 
        mapTypeControl: true
    }    

    public settings: Settings;
    constructor(public appSettings:AppSettings, 
                public formBuilder: UntypedFormBuilder, 
                public appService:AppService,
                public snackBar: MatSnackBar ) { 
        this.settings = this.appSettings.settings; 
    }
  
  
      public pieChartData: ChartData<'pie'> = {
        labels: ['Junior', 'Senior', 'Confirme', 'Aspa'],
        datasets: [{
            data: [25, 25, 25, 25], // Example initial data
            backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0']
        }]
    };
    public pieChartOptions: ChartOptions<'pie'> = {
        responsive: true,
    };

    ngOnInit() {
        this.budgetSpent = "budget Spent"
        this.budgetRemaining="BudgeT REMAINING"

        this.fetchBudgetData();
        
    }



    fetchBudgetData() {
        // Replace with actual logic to fetch data
        const budgetData = { junior: 30, senior: 25, confirme: 20, aspa: 25 };
        this.updatePieChart(budgetData);
    }

    updatePieChart(budgetData: { [key: string]: number }) {
        this.pieChartData.datasets[0].data = [
            budgetData.junior,
            budgetData.senior,
            budgetData.confirme,
            budgetData.aspa
        ];
    }


}
