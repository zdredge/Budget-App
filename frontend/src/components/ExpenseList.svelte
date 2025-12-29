<script>
  /**
   * ExpenseList component displaying a list of expenses for the selected month.
   * Shows expense details with category, amount, date, and delete option.
   */

  /** @type {{ expenses: Array, onDelete: (id: number) => void }} */
  let { expenses = [], onDelete = () => {} } = $props();

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
   * Formats a date string for display.
   */
  function formatDate(dateStr) {
    const date = new Date(dateStr + 'T00:00:00');
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric'
    });
  }

  /**
   * Groups expenses by date, sorted descending.
   */
  function groupExpensesByDate() {
    const grouped = {};

    expenses.forEach(expense => {
      if (!grouped[expense.date]) {
        grouped[expense.date] = [];
      }
      grouped[expense.date].push(expense);
    });

    // Sort dates descending
    return Object.keys(grouped)
      .sort((a, b) => new Date(b) - new Date(a))
      .map(date => ({
        date,
        expenses: grouped[date]
      }));
  }
</script>

<div class="expense-list">
  <h3>Recent Expenses</h3>

  {#if expenses.length === 0}
    <div class="empty-state">
      <span class="empty-icon">📝</span>
      <p>No expenses yet this month</p>
      <p class="empty-hint">Click the + button to add your first expense</p>
    </div>
  {:else}
    {#each groupExpensesByDate() as group}
      <div class="date-group">
        <div class="date-header">{formatDate(group.date)}</div>

        {#each group.expenses as expense}
          <div class="expense-item">
            <div class="expense-left">
              <span class="category-dot" style="background-color: {expense.categoryColor}"></span>
              <div class="expense-details">
                <span class="expense-description">
                  {expense.description || expense.categoryName}
                </span>
                <span class="expense-category">{expense.categoryName}</span>
              </div>
            </div>

            <div class="expense-right">
              <span class="expense-amount">{formatCurrency(expense.amount)}</span>
              <button
                class="delete-btn"
                onclick={() => onDelete(expense.id)}
                aria-label="Delete expense"
              >
                ×
              </button>
            </div>
          </div>
        {/each}
      </div>
    {/each}
  {/if}
</div>

<style>
  .expense-list {
    background: #ffffff;
    border-radius: 12px;
    padding: 1rem;
    border: 1px solid #e5e7eb;
  }

  .expense-list h3 {
    margin: 0 0 1rem 0;
    font-size: 1rem;
    color: #374151;
  }

  .empty-state {
    text-align: center;
    padding: 2rem 1rem;
    color: #6b7280;
  }

  .empty-icon {
    font-size: 2.5rem;
    display: block;
    margin-bottom: 0.5rem;
  }

  .empty-state p {
    margin: 0.25rem 0;
  }

  .empty-hint {
    font-size: 0.875rem;
    color: #9ca3af;
  }

  .date-group {
    margin-bottom: 1rem;
  }

  .date-group:last-child {
    margin-bottom: 0;
  }

  .date-header {
    font-size: 0.75rem;
    font-weight: 600;
    color: #6b7280;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: 0.5rem;
    padding-bottom: 0.25rem;
    border-bottom: 1px solid #f3f4f6;
  }

  .expense-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.75rem 0;
    border-bottom: 1px solid #f3f4f6;
  }

  .expense-item:last-child {
    border-bottom: none;
  }

  .expense-left {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .category-dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    flex-shrink: 0;
  }

  .expense-details {
    display: flex;
    flex-direction: column;
  }

  .expense-description {
    font-weight: 500;
    font-size: 0.875rem;
  }

  .expense-category {
    font-size: 0.75rem;
    color: #6b7280;
  }

  .expense-right {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .expense-amount {
    font-weight: 600;
    font-size: 0.875rem;
  }

  .delete-btn {
    background: none;
    border: none;
    color: #9ca3af;
    font-size: 1.25rem;
    cursor: pointer;
    padding: 0.25rem;
    line-height: 1;
    border-radius: 4px;
    transition: all 0.2s ease;
  }

  .delete-btn:hover {
    color: #ef4444;
    background: #fef2f2;
  }

  @media (prefers-color-scheme: dark) {
    .expense-list {
      background: #1f2937;
      border-color: #374151;
    }

    .expense-list h3 {
      color: #f3f4f6;
    }

    .empty-state {
      color: #9ca3af;
    }

    .empty-hint {
      color: #6b7280;
    }

    .date-header {
      color: #9ca3af;
      border-bottom-color: #374151;
    }

    .expense-item {
      border-bottom-color: #374151;
    }

    .expense-description {
      color: #f3f4f6;
    }

    .expense-category {
      color: #9ca3af;
    }

    .expense-amount {
      color: #f3f4f6;
    }

    .delete-btn:hover {
      background: #450a0a;
    }
  }
</style>

