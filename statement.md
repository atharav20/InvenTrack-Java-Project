# Problem Statement

Small businesses and shopkeepers often manage their stock manually using
notebooks or spreadsheets, which makes it difficult to track quantities
accurately, notice when items are running low, or keep a record of stock
movements over time. This leads to stockouts, overstocking, and lost sales.

InvenTrack addresses this by providing a simple, terminal-based inventory
management system that lets a user add and track products, record stock-in
and stock-out transactions, and get alerted when stock falls below a
defined reorder threshold — without needing any external database setup.

# Scope

InvenTrack is a single-user, command-line application covering:
- Product catalog management (add, view, update, delete)
- Recording stock-in and stock-out transactions against existing products
- Generating a low-stock report and exporting the full inventory to a file

It does not cover multi-user access, a graphical interface, supplier
management, or networked/remote access — these are out of scope for this
project.

# Target Users

- Small shop owners or stockroom managers who need a lightweight way to
  track inventory without investing in a full point-of-sale system
- Anyone needing a simple, local, offline inventory tracker

# High-Level Features

- **Product Management** – create, view, update, and delete product records
  (name, category, quantity, price, reorder level)
- **Stock Transaction Processing** – record IN (restock) and OUT (sale/usage)
  orders, with automatic quantity adjustment and validation to prevent
  stock from going negative
- **Reporting** – view products at or below their reorder level directly in
  the console, and export a complete inventory snapshot to a CSV file
- **Concurrency Safety** – stock transactions are processed safely even when
  multiple orders are placed at the same time, using a lock to prevent
  race conditions on shared product data
- **Error Handling** – invalid input, missing products, and insufficient
  stock are handled gracefully with clear error messages instead of
  crashes
