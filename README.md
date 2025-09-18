# DBTest

This project is a simple Java application using SQLite for a car service shop database. It creates tables for Customers, Mechanics, Bays, Vehicles, Services, and Schedule, and displays their contents.

## Prerequisites

- Java (JDK 8 or higher)
- SQLite JDBC Driver (download from [here](https://github.com/xerial/sqlite-jdbc/releases))

## Setup & Run Instructions

1. Place `DBTest.java` and the downloaded `sqlite-jdbc-<version>.jar` in the same folder.

2. Open a terminal in that folder.

3. Compile the Java code:

   ```bash
   javac DBTest.java
   ```

4. Run the program:
   ```bash
   java -classpath ".:sqlite-jdbc-3.50.3.0.jar" --enable-native-access=ALL-UNNAMED DBTest
   ```

## What Happens When You Run

- On first run, creates all necessary tables in `SQLiteTest1.db`.
- Inserts sample data for Mechanics, Bays, and Services.
- Displays the contents of all tables in the console.

## Troubleshooting

- If you see errors about missing classes, ensure the JDBC jar is in the same folder and the classpath is set correctly.
- If tables do not appear, delete `SQLiteTest1.db` and rerun the program to recreate the database.
