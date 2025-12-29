<script>
  /**
   * MonthSelector component for navigating between months.
   * Displays current month with previous/next navigation arrows.
   */

  /** @type {{ selectedMonth: string, onMonthChange: (month: string) => void }} */
  let { selectedMonth = $bindable(), onMonthChange = () => {} } = $props();

  const monthNames = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];

  /**
   * Parses the selected month string into year and month components.
   */
  function parseMonth(monthStr) {
    const [year, month] = monthStr.split('-').map(Number);
    return { year, month };
  }

  /**
   * Formats year and month into "YYYY-MM" string.
   */
  function formatMonth(year, month) {
    return `${year}-${String(month).padStart(2, '0')}`;
  }

  /**
   * Gets the display name for the current month.
   */
  function getDisplayName() {
    const { year, month } = parseMonth(selectedMonth);
    return `${monthNames[month - 1]} ${year}`;
  }

  /**
   * Navigates to the previous month.
   */
  function previousMonth() {
    const { year, month } = parseMonth(selectedMonth);
    let newMonth = month - 1;
    let newYear = year;
    if (newMonth < 1) {
      newMonth = 12;
      newYear -= 1;
    }
    const newValue = formatMonth(newYear, newMonth);
    selectedMonth = newValue;
    onMonthChange(newValue);
  }

  /**
   * Navigates to the next month.
   */
  function nextMonth() {
    const { year, month } = parseMonth(selectedMonth);
    let newMonth = month + 1;
    let newYear = year;
    if (newMonth > 12) {
      newMonth = 1;
      newYear += 1;
    }
    const newValue = formatMonth(newYear, newMonth);
    selectedMonth = newValue;
    onMonthChange(newValue);
  }

  /**
   * Checks if we're at the current month (disable next button).
   */
  function isCurrentMonth() {
    const now = new Date();
    const current = formatMonth(now.getFullYear(), now.getMonth() + 1);
    return selectedMonth === current;
  }
</script>

<div class="month-selector">
  <button class="nav-btn" onclick={previousMonth} aria-label="Previous month">
    ◀
  </button>

  <span class="month-display">
    {getDisplayName()}
  </span>

  <button
    class="nav-btn"
    onclick={nextMonth}
    disabled={isCurrentMonth()}
    aria-label="Next month"
  >
    ▶
  </button>
</div>

<style>
  .month-selector {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .nav-btn {
    background: #f3f4f6;
    border: 1px solid #d1d5db;
    border-radius: 6px;
    padding: 0.5rem 0.75rem;
    cursor: pointer;
    font-size: 0.875rem;
    transition: all 0.2s ease;
  }

  .nav-btn:hover:not(:disabled) {
    background: #e5e7eb;
  }

  .nav-btn:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }

  .month-display {
    font-size: 1.25rem;
    font-weight: 600;
    min-width: 160px;
    text-align: center;
  }

  @media (prefers-color-scheme: dark) {
    .nav-btn {
      background: #374151;
      border-color: #4b5563;
      color: #f9fafb;
    }

    .nav-btn:hover:not(:disabled) {
      background: #4b5563;
    }
  }
</style>

