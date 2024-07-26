import { Component, Input, OnInit } from '@angular/core';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-budget-chart',
  templateUrl: './budget-chart.component.html',
})
export class BudgetChartComponent implements OnInit {
  @Input() budgetSpent: number;
  @Input() budgetRemaining: number; // Déclarez la propriété d'entrée budgetRemaining

  ngOnInit() {
    // Vérifiez si budgetSpent et budgetRemaining ont des valeurs
    if (this.budgetSpent !== undefined && this.budgetRemaining !== undefined) {
      const ctx = document.getElementById('budgetChart') as HTMLCanvasElement;
      const myChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: ['Spent', 'Remaining'],
          datasets: [{
            label: 'Budget',
            data: [this.budgetSpent, this.budgetRemaining],
            backgroundColor: [
              'blue', // Couleur des dépenses
              'rgba(54, 162, 235, 0.5)' // Couleur du budget restant
            ],
            borderColor: [
              'rgba(255, 99, 132, 1)',
              'rgba(54, 162, 235, 1)'
            ],
            borderWidth: 1
          }]
        },
      
        options: {
          responsive: true,
          maintainAspectRatio: false, 
          plugins: {
            legend: {
              position: 'top',
            },
            title: {
              display: true,
              text: 'Budget Overview'
            }
          }
        }
      });
    }
  }
}
