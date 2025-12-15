# Library Management System (CLI)

Console-first library tool for admins, librarians, and members. Manage catalogue items, loans, notifications, and account finances from a single terminal app. Everything runs in memory with seeded demo data; no external database is required.

## Highlights

- Role-aware CLI: admin menus for catalogue, user, and loan management; librarian and member menus for borrowing, returns, and self-service account tasks.
- Loans with availability checks and due dates by item type (book 28d, CD 21d, journal 7d); fines calculated via strategy pattern and applied to user accounts.
- Account and finance dashboards: balances, transaction history, payments, borrowing eligibility, and stats with automatic suspension or reactivation.
- Search and filter across books, CDs, and journals by id, title, author or artist, ISBN, category, year, and availability.
- Notifications stored per user plus optional Gmail email delivery; a scheduled overdue checker runs every 24 hours (starts 1 minute after launch).
- In-memory repositories seeded with sample catalogue items and users for quick demos.

## Tech Stack

- Java 21, Maven 3.9+
- Testing: JUnit 5, Mockito, Hamcrest; Jacoco (presentation and persistence excluded from coverage)
- Email (optional): Jakarta Mail plus java-dotenv for .env-based Gmail credentials

## Project Layout

- `src/main/java/lms/domain` - rich domain model (User, Account, Loan, Book/CD/Journal, Notification) with validators and fine or search strategies.
- `src/main/java/lms/persistence` - static in-memory repositories implementing domain interfaces and seeded with sample items.
- `src/main/java/lms/application` - services for auth, accounts, catalogue, loans, notifications, search contexts, and scheduled tasks.
- `src/main/java/lms/presentation` - CLI entry (`LibraryApp`) plus admin or user menus, helpers, and logger or factory utilities.
- `src/test/java/lms` - unit tests covering domain entities, validators, strategies, and services.
- `doc/` - generated Javadocs and package-level documentation.

## Default Data

- Users (username / password / role):
  - `ahmad` / `12345678` - Admin
  - `majd04` / `majd123` - Admin
  - `librarian` / `librarian123` - Librarian
  - `user` / `user123` - Member
  - `majd` / `1` - Member
- Catalogue seeds:
  - Books: Clean Code; Effective Java; Design Patterns (with available copy counts).
  - CDs: Thriller; The Dark Side of the Moon; Back in Black; Abbey Road; Rumours.
  - Journals: Nature; Science; The Lancet; Cell; The New England Journal of Medicine.
- State is in memory; restarting the app resets data. A sample overdue loan is preloaded for demo purposes.

## Build and Run

- Prerequisites: Java 21+, Maven 3.9+
- Build and test: `mvn clean test`
- Package: `mvn clean package`
- Run the CLI: `java -cp target/lms-0.0.1-SNAPSHOT.jar lms.presentation.LibraryApp`

## Email Configuration (optional)

- Create a `.env` file in the project root:
  - `GMAIL_USERNAME=your-address@gmail.com`
  - `GMAIL_APP_PASSWORD=your-app-password`
- Without credentials, in-app notifications still work; email sends will fail.

## CLI Quick Tour

- Login with any seeded user; `CLIFactory` selects the correct menu based on role.
- Admin:
  - Catalogue: add, view, search, update, or delete books, CDs, and journals.
  - Users: add, view, update, or delete users.
  - Loans: view active, overdue, returned, and due-soon loans; search by user or item type; create loans for users; force returns.
  - Reports: library stats (items and copies), user stats by role, loan stats and averages.
- Librarian:
  - Browse or search the catalogue; borrow items.
  - Process returns for any user.
  - View personal loans, account and finance dashboards, profile, and notifications.
- Member:
  - Browse or search the catalogue; borrow items when eligible.
  - View active loans.
  - Account and finance dashboards (balance, payments, stats), profile, and notifications. Returns are handled via the librarian menu.

## Testing

- Run `mvn test` to execute the JUnit and Mockito suite (domain, services, validators, search strategies, schedulers).
- Coverage reports generate at `target/site/jacoco/`.

