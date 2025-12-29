-- Seed default categories (only insert if not exists)
MERGE INTO categories (id, name, monthly_limit, color, description) KEY (name) VALUES
(1, 'Groceries', 0, '#22c55e', 'Food and household supplies'),
(2, 'Rent', 0, '#3b82f6', 'Monthly rent or mortgage'),
(3, 'Utilities', 0, '#f59e0b', 'Electric, water, gas, internet'),
(4, 'Miscellaneous', 0, '#6b7280', 'Other expenses'),
(5, 'Personal', 0, '#8b5cf6', 'Personal care and entertainment');

