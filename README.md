# Budget Application

A full-stack budget tracking application built with:
- **Backend:** Java 21 + Spring Boot 3.4
- **Frontend:** Svelte 5 + Vite
- **Database:** H2 (in-memory for development)

## Prerequisites

- Java 21 or higher
- Maven 3.9+

> **Note:** Node.js is automatically downloaded by Maven during the build process, so you don't need to install it separately.

## Project Structure

```
Budget/
├── src/                    # Spring Boot backend
│   ├── main/
│   │   ├── java/com/budget/
│   │   │   ├── BudgetApplication.java
│   │   │   ├── config/
│   │   │   └── controller/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── frontend/               # Svelte frontend
│   ├── src/
│   │   ├── App.svelte
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
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
- H2 Console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:budgetdb`)

## API Endpoints

| Method | Endpoint      | Description       |
|--------|---------------|-------------------|
| GET    | /api/health   | Health check      |

## Future Enhancements

- [ ] Add budget entity and CRUD operations
- [ ] Add expense tracking
- [ ] Add category management
- [ ] Add charts and reports
- [ ] Configure for Azure deployment

