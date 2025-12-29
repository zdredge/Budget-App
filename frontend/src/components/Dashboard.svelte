<script>
  /**
   * Dashboard component - Main view of the budget application.
   * Displays pie chart, category cards, expense list, and expense form.
   */

  import MonthSelector from './MonthSelector.svelte';
  import PieChart from './PieChart.svelte';
  import CategoryCard from './CategoryCard.svelte';
  import ExpenseList from './ExpenseList.svelte';
  import ExpenseForm from './ExpenseForm.svelte';
  import { fetchSummary, fetchCategories, fetchExpenses, createExpense, deleteExpense } from '../lib/api.js';

  // Current month in YYYY-MM format
  let currentMonth = $state(getCurrentMonth());

  // Data state
  let summary = $state(null);
  let categories = $state([]);
  let expenses = $state([]);
  let loading = $state(true);
  let showExpenseForm = $state(false);

  // Element references for height matching
  let chartColumnEl = $state(null);
  let expenseContainerHeight = $state('auto');

  /**
   * Updates the expense container height to match the chart column.
   */
  function updateExpenseContainerHeight() {
    if (chartColumnEl) {
      const summaryCardHeight = chartColumnEl.querySelector('.summary-card')?.offsetHeight || 0;
      expenseContainerHeight = `${summaryCardHeight}px`;
    }
  }

  // Update height after data loads
  $effect(() => {
    if (summary && chartColumnEl) {
      // Use setTimeout to ensure DOM has updated
      setTimeout(updateExpenseContainerHeight, 0);
    }
  });

  /**
   * Gets the current month in YYYY-MM format.
   */
  function getCurrentMonth() {
    const now = new Date();
    return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
  }

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
   * Loads all data for the current month.
   */
  async function loadData() {
    loading = true;
    try {
      const [summaryData, categoriesData, expensesData] = await Promise.all([
        fetchSummary(currentMonth),
        fetchCategories(),
        fetchExpenses(currentMonth)
      ]);
      summary = summaryData;
      categories = categoriesData;
      expenses = expensesData;
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      loading = false;
    }
  }

  /**
   * Handles month change from MonthSelector.
   */
  function handleMonthChange(month) {
    currentMonth = month;
    loadData();
  }

  /**
   * Handles new expense submission.
   */
  async function handleExpenseSubmit(expense) {
    try {
      await createExpense(expense);
      showExpenseForm = false;
      await loadData();
    } catch (error) {
      console.error('Failed to create expense:', error);
    }
  }

  /**
   * Handles expense deletion.
   */
  async function handleExpenseDelete(id) {
    try {
      await deleteExpense(id);
      await loadData();
    } catch (error) {
      console.error('Failed to delete expense:', error);
    }
  }

  /**
   * Gets the overall budget status.
   */
  function getOverallStatus() {
    if (!summary) return 'ok';
    const percentUsed = summary.totalLimit > 0
      ? (summary.totalSpent / summary.totalLimit) * 100
      : 0;
    if (percentUsed > 100) return 'exceeded';
    if (percentUsed >= 80) return 'warning';
    return 'ok';
  }

  // Load data on mount
  $effect(() => {
    loadData();
  });
</script>

<div class="dashboard">
  <!-- Header -->
  <header class="dashboard-header">
    <div class="header-left">
      <h1>💰 Budget</h1>
    </div>
    <MonthSelector bind:selectedMonth={currentMonth} onMonthChange={handleMonthChange} />
  </header>

  {#if loading}
    <div class="loading">
      <div class="spinner"></div>
      <p>Loading...</p>
    </div>
  {:else if summary}
    <!-- Main Content -->
    <div class="dashboard-content">
      <!-- Top Row: Pie Chart & Expense List -->
      <div class="top-row">
        <!-- Left: Pie Chart & Summary -->
        <div class="chart-column" bind:this={chartColumnEl}>
          <h2>Overall Spending</h2>
          <div class="summary-card">
            <PieChart
              categoryBreakdown={summary.categoryBreakdown}
              totalSpent={summary.totalSpent}
              totalLimit={summary.totalLimit}
            />

            <div class="budget-summary" class:warning={getOverallStatus() === 'warning'} class:exceeded={getOverallStatus() === 'exceeded'}>
              <div class="summary-row">
                <span>Total Spent</span>
                <span class="amount">{formatCurrency(summary.totalSpent)}</span>
              </div>
              <div class="summary-row">
                <span>Total Budget</span>
                <span class="amount">{formatCurrency(summary.totalLimit)}</span>
              </div>
              <div class="summary-row remaining">
                <span>Remaining</span>
                <span class="amount" class:negative={summary.totalSpent > summary.totalLimit}>
                  {formatCurrency(summary.totalLimit - summary.totalSpent)}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Right: Expense List -->
        <div class="expenses-column">
          <h2>Recent Expenses</h2>
          <div class="expense-list-container" style="height: {expenseContainerHeight};">
            <ExpenseList {expenses} onDelete={handleExpenseDelete} />
          </div>
        </div>
      </div>

      <!-- Bottom Row: Categories (full width) -->
      <div class="categories-section">
        <h2>Categories</h2>
        <div class="category-grid">
          {#each summary.categoryBreakdown as category}
            <CategoryCard {category} />
          {/each}
        </div>
      </div>
    </div>
  {/if}

  <!-- Floating Add Button -->
  <button class="fab" onclick={() => showExpenseForm = true} aria-label="Add expense">
    +
  </button>

  <!-- Expense Form Modal -->
  {#if showExpenseForm}
    <ExpenseForm
      {categories}
      onSubmit={handleExpenseSubmit}
      onCancel={() => showExpenseForm = false}
    />
  {/if}
</div>

<style>
  .dashboard {
    min-height: 100vh;
    padding: 1rem;
    max-width: 1200px;
    margin: 0 auto;
  }

  .dashboard-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
    flex-wrap: wrap;
    gap: 1rem;
  }

  .header-left h1 {
    margin: 0;
    font-size: 1.75rem;
  }

  .loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 4rem;
    color: #6b7280;
  }

  .spinner {
    width: 40px;
    height: 40px;
    border: 3px solid #e5e7eb;
    border-top-color: #3b82f6;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    to { transform: rotate(360deg); }
  }

  .dashboard-content {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .top-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
    align-items: start;
  }

  .chart-column {
    display: flex;
    flex-direction: column;
  }

  .expenses-column {
    display: flex;
    flex-direction: column;
  }

  .chart-column h2,
  .expenses-column h2,
  .categories-section h2 {
    margin: 0 0 1rem 0;
    font-size: 1.125rem;
    color: #374151;
  }

  .expense-list-container {
    overflow: hidden;
  }


  .categories-section {
    width: 100%;
  }

  .summary-card {
    background: #ffffff;
    border-radius: 16px;
    padding: 1.5rem;
    border: 1px solid #e5e7eb;
  }

  .budget-summary {
    margin-top: 1rem;
    padding-top: 1rem;
    border-top: 1px solid #e5e7eb;
  }

  .budget-summary.warning {
    background: #fffbeb;
    margin: 1rem -1.5rem -1.5rem;
    padding: 1rem 1.5rem;
    border-radius: 0 0 16px 16px;
  }

  .budget-summary.exceeded {
    background: #fef2f2;
    margin: 1rem -1.5rem -1.5rem;
    padding: 1rem 1.5rem;
    border-radius: 0 0 16px 16px;
  }

  .summary-row {
    display: flex;
    justify-content: space-between;
    padding: 0.5rem 0;
    font-size: 0.875rem;
  }

  .summary-row.remaining {
    border-top: 1px solid #e5e7eb;
    margin-top: 0.5rem;
    padding-top: 1rem;
    font-weight: 600;
  }

  .summary-row .amount {
    font-weight: 600;
  }

  .summary-row .amount.negative {
    color: #ef4444;
  }

  .category-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 1rem;
  }

  .fab {
    position: fixed;
    bottom: 2rem;
    right: 2rem;
    width: 56px;
    height: 56px;
    border-radius: 50%;
    background: #3b82f6;
    color: white;
    border: none;
    font-size: 2rem;
    cursor: pointer;
    box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .fab:hover {
    background: #2563eb;
    transform: scale(1.1);
  }

  @media (max-width: 768px) {
    .top-row {
      grid-template-columns: 1fr;
    }

    .dashboard-header {
      flex-direction: column;
      align-items: flex-start;
    }

    .category-grid {
      grid-template-columns: 1fr;
    }
  }

  @media (prefers-color-scheme: dark) {
    .summary-card {
      background: #1f2937;
      border-color: #374151;
    }

    .budget-summary {
      border-top-color: #374151;
    }

    .budget-summary.warning {
      background: #451a03;
    }

    .budget-summary.exceeded {
      background: #450a0a;
    }

    .summary-row.remaining {
      border-top-color: #374151;
    }

    .chart-column h2,
    .expenses-column h2,
    .categories-section h2 {
      color: #f3f4f6;
    }
  }
</style>

