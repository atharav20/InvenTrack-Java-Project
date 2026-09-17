# InvenTrack-Java-Project
# InvenTrack – Inventory & Order Management System

## Overview
InvenTrack project solves the problem statement Today also many small buisnesses manage
stocks and inventory manually using notebooks or spreadsheet, which makes very difficult
for them to keep track of products, stock IN and OUT , reorder the stock thats running low
in inventory and this process is also very time consuming. It leads to problem like out of 
stock and overstock. . It allows a user to manage a product catalog,
process stock-in and stock-out orders, and generate low-stock reports — all through
a simple terminal menu. The project applies core Java concepts covered in the
course: object-oriented design, custom exception handling, multithreading with
synchronization, the Collections framework, and file I/O.

## Features
- **Product Management** – add, list, update, and delete products
- **Stock Transaction Processing** – record stock-in/stock-out orders with
  automatic quantity updates and thread-safe handling of concurrent orders
- **Reporting** – view a low-stock alert report in the console, and export the
  full inventory to a CSV file
- Custom exception handling for invalid input, missing products, and
  insufficient stock
- Data is persisted to a local CSV file (`products_db.csv`), so the project
  runs with zero external setup — no database server or driver required

## Technologies Used
- Java 17+ (standard library only — `java.io`, `java.util`, `java.util.concurrent`)
- No external dependencies

## Project Structure
```
InvenTrack/
├── src/
│   ├── model/
│   │   └── Product.java
│   ├── service/
│   │   ├── ProductService.java     (Module 1: Product CRUD)
│   │   ├── OrderService.java       (Module 2: Stock in/out, thread-safe)
│   │   └── ReportService.java      (Module 3: Reporting)
│   ├── util/
│   │   └── InventoryException.java
│   ├── test/
│   │   └── InventoryServiceTest.java
│   └── Main.java
├── README.md
├── statement.md
└── config.properties
```
## Setup & Installation
No external libraries or database installation are required — this project
uses only the Java standard library.

1. Install a JDK (Java 17 or later) if you don't already have one.
2. Clone or download this repository.
3. Open the project folder in your IDE (or use a terminal).

## Running the Project

### Compile
From the project root:
```
javac -d bin -sourcepath src src/Main.java src/test/InventoryServiceTest.java
```

On Windows (PowerShell), you can also compile explicitly:
```
javac -d bin -sourcepath src src\Main.java src\test\InventoryServiceTest.java
```

### Run the application
```
java -cp bin Main
```

### Run the test suite
```
java -cp bin test.InventoryServiceTest
```

**Important:** run these commands from the project root directory, so the program can find `products_db.csv` and `orders_db.csv` at the expected relative paths. These files are created automatically on first use — no manual setup needed.

## Using the CLI
Once running, you'll see a menu:
1.Add 2.List 3.Order 4.Report 5.Export 6.Exit
- **1 (Add)** – enter product name, category, quantity, price, and reorder level
- **2 (List)** – view all products currently stored
- **3 (Order)** – enter a product id, quantity, and type (`IN` or `OUT`) to
  record a stock transaction
- **4 (Report)** – view products at or below their reorder level
- **5 (Export)** – export the full inventory to a CSV file at a path you specify
- **6 (Exit)** – quit the application

## Testing
`InventoryServiceTest.java` runs a set of scenarios covering:
- Adding a valid product
- Rejecting a product with negative quantity
- Rejecting a stock-out order that exceeds available quantity
- Concurrent stock-out orders on the same product (via `ExecutorService`),
  verifying the internal lock prevents quantity from going negative

Run it with the command in the "Running the Project" section above and check
the console output for `PASS`/`FAIL` results.

## Non-Functional Requirements
- **Performance** – file reads/writes are kept minimal per operation
- **Reliability** – all file operations are wrapped in try-catch with
  meaningful error messages
- **Usability** – simple numbered menu, clear prompts, human-readable error
  messages instead of stack traces
- **Maintainability** – code is organized into model/service/util/test
  packages, each with a single clear responsibility

## Known Limitations
- Data is stored in a flat CSV file rather than a relational database
- Single-user CLI application; not designed for networked/multi-client access

## Author
[Atharav Balaji Khonde] — 25BAI10734
