// Need SQLLITE Drivers from 
// https://github.com/xerial/sqlite-jdbc?tab=readme-ov-file#download
// Put in same folder as this source code file

//Compiled with javac DBTest.java
// Executed with java -classpath ".:sqlite-jdbc-3.50.3.0.jar" --enable-native-access=ALL-UNNAMED DBTest

import java.io.*;
import java.sql.*;
import java.util.*;

public class DBTest {
    /**
     * Get a connection to the database
     * 
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection con;
        // Database path - if it's new database, it will be created in the project
        // folder
        con = DriverManager.getConnection("jdbc:sqlite:SQLiteTest1.db");
        return con;
    }

    /**
     * Creates the Customer table on the database
     * 
     * @param createTableSmt Statement to create the table
     * @param con            Connection to the database
     * @throws SQLException
     */
    private static void createCustomerTable(Statement createTableSmt, Connection con) throws SQLException {
        System.out.println("Building the CUSTOMER table");
        createTableSmt = con.createStatement();
        createTableSmt.executeUpdate(
                "CREATE TABLE Customer(" + "TUID INTEGER PRIMARY KEY AUTOINCREMENT, " + "Name VARCHAR(60));");
        // Files will be added later via File IO
    }

    /**
     * Populates the Customer table based on customer names
     * 
     * @param customers ArrayList of customer names
     */
    private static void populateCustomerTable(ArrayList<String> customers) {
        try (Connection con = getConnection();
                PreparedStatement prep = con.prepareStatement("INSERT INTO Customer(Name) VALUES(?);")) {
            for (String customer : customers) {
                prep.setString(1, customer);
                prep.executeUpdate();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the Customer table contents
     * 
     * @param state Statement to execute the query
     * @throws SQLException
     */
    private static void displayCustomerTable(Statement state) throws SQLException {
        System.out.println("\nCustomers:");
        ResultSet res = state.executeQuery("SELECT TUID, Name FROM Customer;");
        while (res.next()) {
            System.out.println("TUID: " + res.getInt("TUID") + " -- Name: " + res.getString("Name"));
        }
    }

    /**
     * Creates the Mechanics table on the database
     * 
     * @param createTableSmt Statement to create the table
     * @param con            Connection to the database
     * @param prep           PreparedStatement for inserting records
     * @throws SQLException
     */
    private static void createMechanicsTable(Statement createTableSmt, Connection con, PreparedStatement prep)
            throws SQLException {
        System.out.println("Building the MECHANICS table");
        createTableSmt = con.createStatement();
        createTableSmt.executeUpdate("CREATE TABLE Mechanics(" + "TUID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "Mechanic_Name VARCHAR(60), " + "Hourly_Payrate DECIMAL(10, 2));");
        // Add default mechanics record
        System.out.println("Add record 1 to MECHANICS table");
        prep = con.prepareStatement("INSERT INTO Mechanics VALUES(?, ?, ?);");
        prep.setString(1, null); // TUID will auto-increment
        prep.setString(2, "Sue");
        prep.setBigDecimal(3, new java.math.BigDecimal("10.00"));
        prep.execute();
        System.out.println("Add record 2 to MECHANICS table");
        prep.setString(1, null); // TUID will auto-increment
        prep.setString(2, "Steve");
        prep.setBigDecimal(3, new java.math.BigDecimal("9.00"));
        prep.execute();
    }

    /**
     * Displays the Mechanics table contents
     * 
     * @param state Statement to execute the query
     * @throws SQLException
     */
    private static void displayMechanicsTable(Statement state) throws SQLException {
        System.out.println("\nMechanics:");
        ResultSet res = state.executeQuery("SELECT TUID, Mechanic_Name, Hourly_Payrate FROM Mechanics;");
        while (res.next()) {
            System.out.println("TUID: " + res.getInt("TUID") + " -- Name: " + res.getString("Mechanic_Name")
                    + " -- Payrate: $" + res.getBigDecimal("Hourly_Payrate"));
        }
    }

    /**
     * Creates the Bays table on the database
     * 
     * @param createTableSmt Statement to create the table
     * @param con            Connection to the database
     * @param prep           PreparedStatement for inserting records
     * @throws SQLException
     */
    private static void createBaysTable(Statement createTableSmt, Connection con, PreparedStatement prep)
            throws SQLException {
        System.out.println("Building the BAYS table");
        createTableSmt = con.createStatement();
        createTableSmt.executeUpdate("CREATE TABLE Bays(" + "TUID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "Mechanic_TUID INTEGER, " + "FOREIGN KEY (Mechanic_TUID) REFERENCES Mechanics(TUID));");
        // Add default bays record
        System.out.println("Add record 1 to BAYS table");
        prep = con.prepareStatement("INSERT INTO Bays VALUES(?, ?);");
        prep.setString(1, null); // TUID will auto-increment
        // Attach to mechanic 1
        prep.setInt(2, 1);
        prep.execute();
        System.out.println("Add record 2 to BAYS table");
        prep.setString(1, null); // TUID will auto-increment
        // Attach to mechanic 2
        prep.setInt(2, 2);
        prep.execute();
    }

    /**
     * Displays the Bays table contents
     * 
     * @param state Statement to execute the query
     * @throws SQLException
     */
    private static void displayBaysTable(Statement state) throws SQLException {
        System.out.println("\nBays:");
        ResultSet res = state.executeQuery("SELECT TUID, Mechanic_TUID FROM Bays;");
        while (res.next()) {
            System.out.println("TUID: " + res.getInt("TUID") + " -- Mechanic TUID: " + res.getInt("Mechanic_TUID"));
        }
    }

    /**
     * Creates the Vehicles table on the database
     * 
     * @param createTableSmt Statement to create the table
     * @param con            Connection to the database
     * @throws SQLException
     */
    private static void createVehiclesTable(Statement createTableSmt, Connection con) throws SQLException {
        System.out.println("Building the VEHICLES table");
        createTableSmt = con.createStatement();
        createTableSmt.executeUpdate("CREATE TABLE Vehicles(" + "TUID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "Customer_TUID INTEGER, " + "Vehicle_Description VARCHAR(60), "
                + "FOREIGN KEY (Customer_TUID) REFERENCES Customer(TUID));");
        // Files will be added later via File IO
    }

    /**
     * Populates the Vehicles table based on customer name and vehicle description
     * 
     * @param vehicles ArrayList of vehicle entries in the format
     *                 "CustomerName\tVehicleDescription"
     */
    private static void populateVehiclesTable(ArrayList<String> vehicles) {
        // Populate vehicles table based on customer TUID and vehicle description
        try (Connection con = getConnection();
                PreparedStatement prep = con
                        .prepareStatement("INSERT INTO Vehicles(Customer_TUID, Vehicle_Description) VALUES(?, ?);")) {
            for (String vehicle : vehicles) {
                // Vehicle format is "CustomerName VehicleName"
                String[] parts = vehicle.split("\t");
                if (parts.length == 2) {
                    String customerName = parts[0].trim();
                    String vehicleDesc = parts[1].trim();
                    // Get the Customer_TUID based on the customer name
                    PreparedStatement custPrep = con.prepareStatement("SELECT TUID FROM Customer WHERE Name = ?;");
                    custPrep.setString(1, customerName);
                    ResultSet custRes = custPrep.executeQuery();
                    if (custRes.next()) {
                        int customerTUID = custRes.getInt("TUID");
                        // Insert the vehicle record
                        prep.setInt(1, customerTUID);
                        prep.setString(2, vehicleDesc);
                        prep.executeUpdate();
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the Vehicles table contents
     * 
     * @param state Statement to execute the query
     * @throws SQLException
     */
    private static void displayVehiclesTable(Statement state) throws SQLException {
        System.out.println("\nVehicles:");
        ResultSet res = state.executeQuery("SELECT TUID, Customer_TUID, Vehicle_Description FROM Vehicles;");
        while (res.next()) {
            System.out.println("TUID: " + res.getInt("TUID") + " -- Customer TUID: " + res.getInt("Customer_TUID")
                    + " -- Description: " + res.getString("Vehicle_Description"));
        }
    }

    /**
     * Creates the Services table on the database
     * 
     * @param createTableSmt Statement to create the table
     * @param con            Connection to the database
     * @param prep           PreparedStatement for inserting records
     * @throws SQLException
     */
    private static void createServicesTable(Statement createTableSmt, Connection con, PreparedStatement prep)
            throws SQLException {
        System.out.println("Building the SERVICES table");
        createTableSmt = con.createStatement();
        createTableSmt.executeUpdate("CREATE TABLE Services(" + "TUID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "Service_Name VARCHAR(60), " + "Service_Time INTEGER); ");
        // Add default services record
        System.out.println("Add record 1 to SERVICES table");
        prep = con.prepareStatement("INSERT INTO Services VALUES(?, ?, ?);");
        prep.setString(1, null); // TUID will auto-increment
        prep.setString(2, "Oil Change");
        prep.setInt(3, 30); // Service time in minutes
        prep.execute();
        System.out.println("Add record 2 to SERVICES table");
        prep.setString(1, null); // TUID will auto-increment
        prep.setString(2, "Tire Replacement");
        prep.setInt(3, 60); // Service time in minutes
        prep.execute();
        System.out.println("Add record 3 to SERVICES table");
        prep.setString(1, null); // TUID will auto-increment
        prep.setString(2, "Brakes");
        prep.setInt(3, 180); // Service time in minutes
        prep.execute();
    }

    /**
     * Displays the Services table contents
     * 
     * @param state Statement to execute the query
     * @throws SQLException
     */
    private static void displayServicesTable(Statement state) throws SQLException {
        System.out.println("\nServices:");
        ResultSet res = state.executeQuery("SELECT TUID, Service_Name, Service_Time FROM Services;");
        while (res.next()) {
            System.out.println("TUID: " + res.getInt("TUID") + " -- Service: " + res.getString("Service_Name")
                    + " -- Time: " + res.getInt("Service_Time") + " min");
        }
    }

    /**
     * Creates the Schedule table on the database
     * 
     * @param createTableSmt Statement to create the table
     * @param con            Connection to the database
     * @throws SQLException
     */
    private static void createScheduleTable(Statement createTableSmt, Connection con) throws SQLException {
        System.out.println("Building the SCHEDULE table");
        createTableSmt = con.createStatement();
        createTableSmt.executeUpdate("CREATE TABLE Schedule(" + "TUID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "Vehicle_TUID INTEGER, " + "Service_TUID INTEGER, " + "Bay_TUID INTEGER, "
                + "Appointment_Time TIMESTAMP, " + "FOREIGN KEY (Vehicle_TUID) REFERENCES Vehicles(TUID), "
                + "FOREIGN KEY (Service_TUID) REFERENCES Services(TUID), "
                + "FOREIGN KEY (Bay_TUID) REFERENCES Bays(TUID));");
        // Don't add any records, since they will be added later based on the customer
        // info inserted via File IO
    }

    /**
     * Displays the Schedule table contents
     * 
     * @param state Statement to execute the query
     * @throws SQLException
     */
    private static void displayScheduleTable(Statement state) throws SQLException {
        System.out.println("\nSchedule:");
        ResultSet res = state
                .executeQuery("SELECT TUID, Vehicle_TUID, Service_TUID, Bay_TUID, Appointment_Time FROM Schedule;");
        while (res.next()) {
            System.out.println("TUID: " + res.getInt("TUID") + " -- Vehicle TUID: " + res.getInt("Vehicle_TUID")
                    + " -- Service TUID: " + res.getInt("Service_TUID") + " -- Bay TUID: " + res.getInt("Bay_TUID")
                    + " -- Appointment Time: " + res.getString("Appointment_Time"));
        }
    }

    /**
     * Builds the database if it doesn't already exist
     * 
     * @param DBExists Boolean indicating if the database already exists
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static void buildDatabase(boolean DBExists) throws ClassNotFoundException, SQLException {
        Connection con;
        Statement checkTableSmt, createTableSmt = null;
        ResultSet res;
        PreparedStatement prep = null;

        if (!DBExists) {
            con = getConnection();
            // Check for database table existence and if it's not there, create it and add 2
            // records
            checkTableSmt = con.createStatement();
            // Check for tables and create if they don't exist
            res = checkTableSmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='customer'");
            if (!res.next()) {
                // Create the Customer table
                createCustomerTable(createTableSmt, con);
                // Create the Mechanics table
                createMechanicsTable(createTableSmt, con, prep);
                // Create the Bays table
                createBaysTable(createTableSmt, con, prep);

                // Create the Vehicles table
                createVehiclesTable(createTableSmt, con);

                // Create the Services table
                createServicesTable(createTableSmt, con, prep);

                // Create the Schedule table
                createScheduleTable(createTableSmt, con);

            }
        }
    }

    public static void displayAllData() throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        Statement state = con.createStatement();

        // Display Customers
        displayCustomerTable(state);

        // Display Mechanics
        displayMechanicsTable(state);

        // Display Vehicles
        displayVehiclesTable(state);

        // Display Bays
        displayBaysTable(state);

        // Display Services
        displayServicesTable(state);

        // Display Schedule
        displayScheduleTable(state);

        state.close();
        con.close();
    }

    private static void clearDatabase() throws ClassNotFoundException, SQLException {
        Connection con = getConnection();
        Statement state = con.createStatement();
        state.executeUpdate("DROP TABLE IF EXISTS Schedule;");
        state.executeUpdate("DROP TABLE IF EXISTS Vehicles;");
        state.executeUpdate("DROP TABLE IF EXISTS Services;");
        state.executeUpdate("DROP TABLE IF EXISTS Bays;");
        state.executeUpdate("DROP TABLE IF EXISTS Mechanics;");
        state.executeUpdate("DROP TABLE IF EXISTS Customer;");
        state.close();
        con.close();
    }

    private static void readIOFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the input file name: ");
        String fileName = scanner.nextLine();
        scanner.close();
        File file = new File(fileName);
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> customers = new ArrayList<>();
        ArrayList<String> vehicles = new ArrayList<>();
        ArrayList<String> services = new ArrayList<>();
        for (String line : lines) {
            if (line.charAt(0) == 'C') {
                customers.add(line.substring(2).trim());
            } else if (line.charAt(0) == 'V') {
                vehicles.add(line.substring(2).trim());
            } else if (line.charAt(0) == 'S') {
                services.add(line.substring(2).trim());
            }
        }

        populateCustomerTable(customers);
        populateVehiclesTable(vehicles);

    }

    public static void main(String args[]) {
        boolean DBExists = false; // IMPLEMENT LATER TO CHECK IF DB FILE EXISTS
        try {
            getConnection();
            buildDatabase(DBExists);
            displayAllData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        readIOFile();
    }
}