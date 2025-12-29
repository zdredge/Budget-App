<script>
  /**
   * CategoryCard component displaying a single category's budget status.
   * Shows category name, spent vs limit, progress bar, and visual status indicators.
   * - Green: Within budget (< 80%)
   * - Orange: Approaching limit (>= 80% and <= 100%)
   * - Red: Exceeded limit (> 100%)
   */

  /** @type {{ category: Object }} */
  let { category } = $props();

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
   * Gets the status color based on budget status.
   */
  function getStatusColor() {
    switch (category.status) {
      case 'exceeded':
        return '#ef4444'; // Red
      case 'warning':
        return '#f59e0b'; // Orange
      default:
        return '#22c55e'; // Green
    }
  }

  /**
   * Gets the status icon based on budget status.
   */
  function getStatusIcon() {
    switch (category.status) {
      case 'exceeded':
        return '🔴';
      case 'warning':
        return '🟠';
      default:
        return '✓';
    }
  }

  /**
   * Calculates the progress bar width (capped at 100% for display).
   */
  function getProgressWidth() {
    return Math.min(category.percentUsed, 100);
  }
</script>

<div class="category-card" class:warning={category.status === 'warning'} class:exceeded={category.status === 'exceeded'}>
  <div class="card-header">
    <div class="category-info">
      <span class="color-dot" style="background-color: {category.categoryColor}"></span>
      <span class="category-name">{category.categoryName}</span>
    </div>
    <span class="status-icon">{getStatusIcon()}</span>
  </div>

  <div class="amounts">
    <span class="spent">{formatCurrency(category.spent)}</span>
    <span class="separator">/</span>
    <span class="limit">{formatCurrency(category.limit)}</span>
  </div>

  <div class="progress-container">
    <div
      class="progress-bar"
      style="width: {getProgressWidth()}%; background-color: {getStatusColor()}"
    ></div>
  </div>

  <div class="percent-display">
    {category.percentUsed.toFixed(0)}% used
  </div>
</div>

<style>
  .category-card {
    background: #ffffff;
    border: 1px solid #e5e7eb;
    border-radius: 12px;
    padding: 1rem;
    transition: all 0.2s ease;
  }

  .category-card:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .category-card.warning {
    border-color: #f59e0b;
    background: #fffbeb;
  }

  .category-card.exceeded {
    border-color: #ef4444;
    background: #fef2f2;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 0.75rem;
  }

  .category-info {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .color-dot {
    width: 12px;
    height: 12px;
    border-radius: 50%;
  }

  .category-name {
    font-weight: 600;
    font-size: 1rem;
  }

  .status-icon {
    font-size: 1rem;
  }

  .amounts {
    display: flex;
    align-items: baseline;
    gap: 0.25rem;
    margin-bottom: 0.5rem;
  }

  .spent {
    font-size: 1.25rem;
    font-weight: 700;
  }

  .separator {
    color: #9ca3af;
  }

  .limit {
    color: #6b7280;
    font-size: 0.875rem;
  }

  .progress-container {
    height: 8px;
    background: #e5e7eb;
    border-radius: 4px;
    overflow: hidden;
    margin-bottom: 0.5rem;
  }

  .progress-bar {
    height: 100%;
    border-radius: 4px;
    transition: width 0.3s ease;
  }

  .percent-display {
    font-size: 0.75rem;
    color: #6b7280;
    text-align: right;
  }

  @media (prefers-color-scheme: dark) {
    .category-card {
      background: #1f2937;
      border-color: #374151;
    }

    .category-card.warning {
      background: #451a03;
      border-color: #f59e0b;
    }

    .category-card.exceeded {
      background: #450a0a;
      border-color: #ef4444;
    }

    .limit {
      color: #9ca3af;
    }

    .progress-container {
      background: #374151;
    }

    .percent-display {
      color: #9ca3af;
    }
  }
</style>

