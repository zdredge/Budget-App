<script>
  /**
   * ExpenseForm component for adding new expenses.
   * Displays a form with amount, description, date, and category selection.
   */

  /** @type {{ categories: Array, onSubmit: (expense: Object) => void, onCancel: () => void }} */
  let { categories = [], onSubmit = () => {}, onCancel = () => {} } = $props();

  let amount = $state('');
  let description = $state('');
  let date = $state(new Date().toISOString().split('T')[0]);
  let categoryId = $state('');
  let error = $state('');

  /**
   * Validates and submits the expense form.
   */
  function handleSubmit(event) {
    event.preventDefault();
    error = '';

    // Validation
    if (!amount || parseFloat(amount) <= 0) {
      error = 'Please enter a valid amount';
      return;
    }
    if (!categoryId) {
      error = 'Please select a category';
      return;
    }
    if (!date) {
      error = 'Please select a date';
      return;
    }

    const expense = {
      amount: parseFloat(amount),
      description: description.trim(),
      date,
      categoryId: parseInt(categoryId)
    };

    onSubmit(expense);

    // Reset form
    amount = '';
    description = '';
    date = new Date().toISOString().split('T')[0];
    categoryId = '';
  }
  /**
   * Handles keyboard events on the overlay to close on Escape.
   */
  function handleKeydown(event) {
    if (event.key === 'Escape') {
      onCancel();
    }
  }
</script>

<div
  class="expense-form-overlay"
  onclick={onCancel}
  onkeydown={handleKeydown}
  role="dialog"
  aria-modal="true"
  tabindex="-1"
>
  <div class="expense-form" onclick={(e) => e.stopPropagation()} onkeydown={(e) => e.stopPropagation()} role="presentation">
    <div class="form-header">
      <h2>Add Expense</h2>
      <button class="close-btn" onclick={onCancel} aria-label="Close">×</button>
    </div>

    <form onsubmit={handleSubmit}>
      {#if error}
        <div class="error-message">{error}</div>
      {/if}

      <div class="form-group">
        <label for="amount">Amount</label>
        <div class="amount-input">
          <span class="currency-symbol">$</span>
          <input
            type="number"
            id="amount"
            bind:value={amount}
            placeholder="0.00"
            step="0.01"
            min="0"
          />
        </div>
      </div>

      <div class="form-group">
        <label for="description">Description</label>
        <input
          type="text"
          id="description"
          bind:value={description}
          placeholder="What did you spend on?"
        />
      </div>

      <div class="form-group">
        <label for="date">Date</label>
        <input
          type="date"
          id="date"
          bind:value={date}
        />
      </div>

      <div class="form-group">
        <label for="category">Category</label>
        <select id="category" bind:value={categoryId}>
          <option value="">Select a category</option>
          {#each categories as category}
            <option value={category.id}>
              {category.name}
            </option>
          {/each}
        </select>
      </div>

      <div class="form-actions">
        <button type="button" class="btn-cancel" onclick={onCancel}>Cancel</button>
        <button type="submit" class="btn-submit">Add Expense</button>
      </div>
    </form>
  </div>
</div>

<style>
  .expense-form-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }

  .expense-form {
    background: #ffffff;
    border-radius: 16px;
    padding: 1.5rem;
    width: 100%;
    max-width: 400px;
    margin: 1rem;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  }

  .form-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
  }

  .form-header h2 {
    margin: 0;
    font-size: 1.25rem;
  }

  .close-btn {
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    color: #6b7280;
    padding: 0.25rem;
    line-height: 1;
  }

  .close-btn:hover {
    color: #1f2937;
  }

  .error-message {
    background: #fef2f2;
    color: #dc2626;
    padding: 0.75rem;
    border-radius: 8px;
    margin-bottom: 1rem;
    font-size: 0.875rem;
  }

  .form-group {
    margin-bottom: 1rem;
  }

  .form-group label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 500;
    font-size: 0.875rem;
    color: #374151;
  }

  .form-group input,
  .form-group select {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #d1d5db;
    border-radius: 8px;
    font-size: 1rem;
    transition: border-color 0.2s ease;
  }

  .form-group input:focus,
  .form-group select:focus {
    outline: none;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  }

  .amount-input {
    position: relative;
  }

  .currency-symbol {
    position: absolute;
    left: 0.75rem;
    top: 50%;
    transform: translateY(-50%);
    color: #6b7280;
  }

  .amount-input input {
    padding-left: 1.75rem;
  }

  .form-actions {
    display: flex;
    gap: 0.75rem;
    margin-top: 1.5rem;
  }

  .btn-cancel,
  .btn-submit {
    flex: 1;
    padding: 0.75rem 1rem;
    border-radius: 8px;
    font-size: 1rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .btn-cancel {
    background: #f3f4f6;
    border: 1px solid #d1d5db;
    color: #374151;
  }

  .btn-cancel:hover {
    background: #e5e7eb;
  }

  .btn-submit {
    background: #3b82f6;
    border: none;
    color: white;
  }

  .btn-submit:hover {
    background: #2563eb;
  }

  @media (prefers-color-scheme: dark) {
    .expense-form {
      background: #1f2937;
    }

    .form-header h2 {
      color: #f9fafb;
    }

    .close-btn {
      color: #9ca3af;
    }

    .close-btn:hover {
      color: #f9fafb;
    }

    .error-message {
      background: #450a0a;
      color: #fca5a5;
    }

    .form-group label {
      color: #d1d5db;
    }

    .form-group input,
    .form-group select {
      background: #374151;
      border-color: #4b5563;
      color: #f9fafb;
    }

    .currency-symbol {
      color: #9ca3af;
    }

    .btn-cancel {
      background: #374151;
      border-color: #4b5563;
      color: #d1d5db;
    }

    .btn-cancel:hover {
      background: #4b5563;
    }
  }
</style>

