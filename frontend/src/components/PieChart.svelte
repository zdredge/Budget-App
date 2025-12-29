<script>
  /**
   * PieChart component for visualizing spending by category.
   * Uses Chart.js to render a doughnut chart with category labels.
   */

  import { onMount } from 'svelte';
  import Chart from 'chart.js/auto';

  /** @type {{ categoryBreakdown: Array, totalSpent: number, totalLimit: number }} */
  let { categoryBreakdown = [], totalSpent = 0, totalLimit = 0 } = $props();

  let canvas;
  let chart;

  /**
   * Formats a number as currency.
   */
  function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }

  /**
   * Creates or updates the chart with current data.
   */
  function updateChart() {
    if (!canvas) return;

    const ctx = canvas.getContext('2d');

    // Filter categories that have spending
    const activeCategories = categoryBreakdown.filter(cat => cat.spent > 0);

    const data = {
      labels: activeCategories.map(cat => cat.categoryName),
      datasets: [{
        data: activeCategories.map(cat => cat.spent),
        backgroundColor: activeCategories.map(cat => cat.categoryColor),
        borderColor: '#ffffff',
        borderWidth: 2,
        hoverBorderWidth: 3
      }]
    };

    const options = {
      responsive: true,
      maintainAspectRatio: true,
      cutout: '60%',
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            padding: 16,
            usePointStyle: true,
            pointStyle: 'circle',
            font: {
              size: 12
            }
          }
        },
        tooltip: {
          callbacks: {
            label: function(context) {
              const value = context.parsed;
              const total = context.dataset.data.reduce((a, b) => a + b, 0);
              const percentage = ((value / total) * 100).toFixed(1);
              return `${context.label}: ${formatCurrency(value)} (${percentage}%)`;
            }
          }
        }
      }
    };

    if (chart) {
      chart.data = data;
      chart.update();
    } else {
      chart = new Chart(ctx, {
        type: 'doughnut',
        data,
        options
      });
    }
  }

  onMount(() => {
    updateChart();
    return () => {
      if (chart) {
        chart.destroy();
      }
    };
  });

  // Update chart when data changes
  $effect(() => {
    if (categoryBreakdown) {
      updateChart();
    }
  });
</script>

<div class="pie-chart-container">
  <div class="chart-wrapper">
    <canvas bind:this={canvas}></canvas>
    <div class="center-text">
      <span class="total-spent">{formatCurrency(totalSpent)}</span>
      <span class="total-label">of {formatCurrency(totalLimit)}</span>
    </div>
  </div>
</div>

<style>
  .pie-chart-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 1rem;
  }

  .chart-wrapper {
    position: relative;
    width: 100%;
    max-width: 300px;
  }

  .center-text {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    pointer-events: none;
  }

  .total-spent {
    display: block;
    font-size: 1.5rem;
    font-weight: 700;
    color: #1f2937;
  }

  .total-label {
    display: block;
    font-size: 0.75rem;
    color: #6b7280;
  }

  @media (prefers-color-scheme: dark) {
    .total-spent {
      color: #f9fafb;
    }

    .total-label {
      color: #9ca3af;
    }
  }
</style>

