/**
 * Centralized API calls for the Budget application.
 * Currently using mock data - will connect to backend in next phase.
 */

// TODO: Uncomment when connecting to backend
// const API_BASE = '/api';

/**
 * Mock data for development without backend connection.
 */
const mockCategories = [
  { id: 1, name: 'Groceries', color: '#22c55e', monthlyLimit: 300, description: 'Food and household supplies' },
  { id: 2, name: 'Rent', color: '#3b82f6', monthlyLimit: 1200, description: 'Monthly rent or mortgage' },
  { id: 3, name: 'Utilities', color: '#f59e0b', monthlyLimit: 150, description: 'Electric, water, gas, internet' },
  { id: 4, name: 'Miscellaneous', color: '#6b7280', monthlyLimit: 200, description: 'Other expenses' },
  { id: 5, name: 'Personal', color: '#8b5cf6', monthlyLimit: 250, description: 'Personal care and entertainment' }
];

const mockExpenses = [
  { id: 1, amount: 45.50, description: 'Weekly groceries', date: '2025-12-15', categoryId: 1, categoryName: 'Groceries', categoryColor: '#22c55e' },
  { id: 2, amount: 82.30, description: 'Grocery run', date: '2025-12-22', categoryId: 1, categoryName: 'Groceries', categoryColor: '#22c55e' },
  { id: 3, amount: 1200.00, description: 'December rent', date: '2025-12-01', categoryId: 2, categoryName: 'Rent', categoryColor: '#3b82f6' },
  { id: 4, amount: 95.00, description: 'Electric bill', date: '2025-12-10', categoryId: 3, categoryName: 'Utilities', categoryColor: '#f59e0b' },
  { id: 5, amount: 65.00, description: 'Internet', date: '2025-12-05', categoryId: 3, categoryName: 'Utilities', categoryColor: '#f59e0b' },
  { id: 6, amount: 35.00, description: 'Haircut', date: '2025-12-20', categoryId: 5, categoryName: 'Personal', categoryColor: '#8b5cf6' },
  { id: 7, amount: 120.00, description: 'Christmas gifts', date: '2025-12-23', categoryId: 4, categoryName: 'Miscellaneous', categoryColor: '#6b7280' }
];

/**
 * Generates a mock monthly summary based on mock data.
 * @param {number} year
 * @param {number} month
 * @returns {Object} Monthly summary with category breakdown
 */
function generateMockSummary(year, month) {
  // Filter expenses for the given month
  const monthExpenses = mockExpenses.filter(exp => {
    const expDate = new Date(exp.date);
    return expDate.getFullYear() === year && expDate.getMonth() + 1 === month;
  });

  // Calculate category breakdown
  const categoryBreakdown = mockCategories.map(category => {
    const spent = monthExpenses
      .filter(exp => exp.categoryId === category.id)
      .reduce((sum, exp) => sum + exp.amount, 0);

    const limit = category.monthlyLimit;
    const percentUsed = limit > 0 ? (spent / limit) * 100 : 0;

    let status = 'ok';
    if (percentUsed > 100) {
      status = 'exceeded';
    } else if (percentUsed >= 80) {
      status = 'warning';
    }

    return {
      categoryId: category.id,
      categoryName: category.name,
      categoryColor: category.color,
      spent,
      limit,
      percentUsed,
      status
    };
  });

  const totalSpent = categoryBreakdown.reduce((sum, cat) => sum + cat.spent, 0);
  const totalLimit = categoryBreakdown.reduce((sum, cat) => sum + cat.limit, 0);

  return {
    year,
    month,
    totalSpent,
    totalLimit,
    categoryBreakdown
  };
}

/**
 * Fetches monthly summary data.
 * @param {string} month - Optional month in "YYYY-MM" format
 * @returns {Promise<Object>} Monthly summary
 */
export async function fetchSummary(month = null) {
  // TODO: Replace with actual API call
  // const url = month ? `${API_BASE}/summary?month=${month}` : `${API_BASE}/summary`;
  // const response = await fetch(url);
  // return response.json();

  const now = new Date();
  let year = now.getFullYear();
  let monthNum = now.getMonth() + 1;

  if (month) {
    const [y, m] = month.split('-').map(Number);
    year = y;
    monthNum = m;
  }

  return generateMockSummary(year, monthNum);
}

/**
 * Fetches all categories.
 * @returns {Promise<Array>} List of categories
 */
export async function fetchCategories() {
  // TODO: Replace with actual API call
  // const response = await fetch(`${API_BASE}/categories`);
  // return response.json();

  return [...mockCategories];
}

/**
 * Fetches expenses, optionally filtered by month.
 * @param {string} month - Optional month in "YYYY-MM" format
 * @returns {Promise<Array>} List of expenses
 */
export async function fetchExpenses(month = null) {
  // TODO: Replace with actual API call
  // const url = month ? `${API_BASE}/expenses?month=${month}` : `${API_BASE}/expenses`;
  // const response = await fetch(url);
  // return response.json();

  if (month) {
    const [year, monthNum] = month.split('-').map(Number);
    return mockExpenses.filter(exp => {
      const expDate = new Date(exp.date);
      return expDate.getFullYear() === year && expDate.getMonth() + 1 === monthNum;
    });
  }
  return [...mockExpenses];
}

/**
 * Creates a new expense.
 * @param {Object} expense - Expense data
 * @returns {Promise<Object>} Created expense
 */
export async function createExpense(expense) {
  // TODO: Replace with actual API call
  // const response = await fetch(`${API_BASE}/expenses`, {
  //   method: 'POST',
  //   headers: { 'Content-Type': 'application/json' },
  //   body: JSON.stringify(expense)
  // });
  // return response.json();

  const category = mockCategories.find(c => c.id === expense.categoryId);
  const newExpense = {
    id: mockExpenses.length + 1,
    ...expense,
    categoryName: category?.name || 'Unknown',
    categoryColor: category?.color || '#6b7280'
  };
  mockExpenses.push(newExpense);
  return newExpense;
}

/**
 * Deletes an expense by ID.
 * @param {number} id - Expense ID
 * @returns {Promise<void>}
 */
export async function deleteExpense(id) {
  // TODO: Replace with actual API call
  // await fetch(`${API_BASE}/expenses/${id}`, { method: 'DELETE' });

  const index = mockExpenses.findIndex(exp => exp.id === id);
  if (index > -1) {
    mockExpenses.splice(index, 1);
  }
}

