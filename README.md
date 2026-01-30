# Budget Application

A full-stack budget tracking application built with:
- **Backend:** Java 21 + Spring Boot 3.4
- **Frontend:** Svelte 5 + Vite
- **Database:** H2 (file-based for persistence)

## Features

- **Dashboard** - Overview of monthly spending with interactive pie chart
- **Expense Tracking** - Add, edit, and delete expenses with date and category
- **Category Management** - Custom categories with configurable budget limits
- **Budget Visualization** - Pie chart showing spending breakdown by category
- **Monthly View** - Filter expenses by month with month selector
- **Budget Warnings** - Visual indicators when approaching (orange) or exceeding (red) category limits
- **Scrollable Expense List** - Date-sorted expenses in a scrollable container

## Prerequisites

- Java 21 or higher
- Maven 3.9+

> **Note:** Node.js is automatically downloaded by Maven during the build process, so you don't need to install it separately.

## Project Structure

```
Budget/
├── src/                          # Spring Boot backend
│   ├── main/
│   │   ├── java/com/budget/
│   │   │   ├── BudgetApplication.java
│   │   │   ├── config/           # CORS configuration
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── model/            # Entity classes
│   │   │   └── repository/       # JPA repositories
│   │   └── resources/
│   │       ├── application.yml
│   │       └── data.sql          # Seed data for default categories
│   └── test/                     # Unit tests
├── frontend/                     # Svelte frontend
│   ├── src/
│   │   ├── App.svelte
│   │   ├── main.js
│   │   ├── components/
│   │   │   ├── Dashboard.svelte      # Main dashboard layout
│   │   │   ├── PieChart.svelte       # Spending visualization
│   │   │   ├── ExpenseList.svelte    # Scrollable expense list
│   │   │   ├── ExpenseForm.svelte    # Add/edit expense form
│   │   │   ├── CategoryCard.svelte   # Category display with progress
│   │   │   └── MonthSelector.svelte  # Month navigation
│   │   └── lib/
│   │       └── api.js            # API client functions
│   ├── package.json
│   └── vite.config.js
├── data/                         # Persistent H2 database files
│   └── budgetdb.mv.db
└── pom.xml
```

## Getting Started

### Run the Application

```bash
# Build and run everything with one command
mvn spring-boot:run
```

This command will:
1. Download Node.js and npm (if not cached)
2. Install frontend dependencies
3. Build the Svelte frontend
4. Copy the build to Spring Boot's static resources
5. Start the Spring Boot server

The app will be available at `http://localhost:8080`

### Build Only (Without Running)

```bash
# Build the complete application
mvn clean package

# Run the built JAR
java -jar target/budget-app-1.0-SNAPSHOT.jar
```

### Run Tests

```bash
# Run all tests
mvn test
```

### Development Mode (Optional - For Frontend Hot Reload)

If you want hot-reload during frontend development, you can run both separately:

```bash
# Terminal 1: Start backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Terminal 2: Start frontend dev server
cd frontend
npm run dev
```

Then access the app at `http://localhost:5173` (Vite dev server with proxy to backend)

## Useful Endpoints

- Application: http://localhost:8080
- Health check: http://localhost:8080/api/health
- H2 Console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:file:./data/budgetdb`)

## API Endpoints

### Health
| Method | Endpoint      | Description       |
|--------|---------------|-------------------|
| GET    | /api/health   | Health check      |

### Categories
| Method | Endpoint            | Description              |
|--------|---------------------|--------------------------|
| GET    | /api/categories     | Get all categories       |
| GET    | /api/categories/:id | Get category by ID       |
| POST   | /api/categories     | Create a new category    |
| PUT    | /api/categories/:id | Update a category        |
| DELETE | /api/categories/:id | Delete a category        |

### Expenses
| Method | Endpoint          | Description                          |
|--------|-------------------|--------------------------------------|
| GET    | /api/expenses     | Get expenses (optional: year, month) |
| GET    | /api/expenses/:id | Get expense by ID                    |
| POST   | /api/expenses     | Create a new expense                 |
| PUT    | /api/expenses/:id | Update an expense                    |
| DELETE | /api/expenses/:id | Delete an expense                    |

### Summary
| Method | Endpoint     | Description                               |
|--------|--------------|-------------------------------------------|
| GET    | /api/summary | Get monthly summary with category breakdown |

## Default Categories

The application comes with the following default categories:
- Groceries
- Rent
- Utilities
- Miscellaneous
- Personal

## Future Enhancements

- [ ] Income tracking
- [ ] Multi-user support
- [ ] Configure for Azure deployment
- [ ] Export reports (CSV/PDF)
- [ ] Recurring expenses

