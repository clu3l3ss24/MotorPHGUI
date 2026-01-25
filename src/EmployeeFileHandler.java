import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.*;
import java.util.*;

/**
 * Handles all file I/O operations for Employee data.
 * Centralizes data access to ensure consistency across the application.
 */
public class EmployeeFileHandler {

    private static final String FILE_PATH = "src/data/employee_info.csv";
    private static final String LOGIN_FILE_PATH = "src/data/employee_logins.csv";

    /**
     * Generates the next available Employee Number.
     */
    public static int generateNextEmpNum() {
        List<Employee> employees = loadEmployees();
        if (employees.isEmpty()) {
            return 10001;
        }
        return employees.stream()
                .mapToInt(Employee::getEmployeeNumber)
                .max()
                .orElse(10000) + 1;
    }

    /**
     * Loads all records and converts them to Employee Objects.
     */
    public static List<Employee> loadEmployees() {
        List<Employee> employees = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            List<String[]> records = reader.readAll();
            boolean skipHeader = true;

            for (String[] rowData : records) {
                if (skipHeader) { skipHeader = false; continue; }
                if (rowData.length < 20) continue;

                try {
                    employees.add(new Employee(
                        Integer.parseInt(rowData[0].trim()), 
                        rowData[1].trim(), rowData[2].trim(), rowData[3].trim(), 
                        rowData[4].trim(), rowData[5].trim(), rowData[6].trim(), 
                        rowData[7].trim(), rowData[8].trim(), rowData[9].trim(),
                        rowData[10].trim(), rowData[11].trim(),
                        parseDouble(rowData[12]), parseDouble(rowData[13]), 
                        parseDouble(rowData[14]), parseDouble(rowData[15]), 
                        parseDouble(rowData[16]), parseDouble(rowData[17]), 
                        parseDouble(rowData[18]), rowData[19].trim()
                    ));
                } catch (NumberFormatException e) {
                    // Skip malformed rows
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error loading employees: " + e.getMessage());
        }
        return employees;
    }
    
    // Add this to EmployeeFileHandler.java
    public static void addEmployee(Employee emp) {
    List<Employee> employees = loadEmployees();
    employees.add(emp);
    saveEmployees(employees); // This re-saves the whole list with the new person
    }
    
    /**
 * Saves the entire list of employees back to the CSV.
 * This satisfies the call made in addEmployee.
 */
    public static void saveEmployees(List<Employee> employees) {
    try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
        // 1. Write the Header first
        writer.writeNext(new String[]{
            "Employee #", "Last Name", "First Name", "Phone Number", "Status", 
            "Position", "Supervisor", "Address", "SSS #", "PhilHealth #", 
            "TIN #", "Pag-ibig #", "Basic Salary", "Rice Subsidy", 
            "Phone Allowance", "Clothing Allowance", "Gross Semi-monthly Rate", 
            "Hourly Rate", "Withholding Tax", "Birthday"
        });

        // 2. Write each employee record
        for (Employee emp : employees) {
            writer.writeNext(formatEmployeeData(emp));
        }
    } catch (IOException e) {
        System.err.println("Error saving employees: " + e.getMessage());
    }
    }

    /**
     * Finds a specific employee by their number.
     */
    public static Optional<Employee> getEmployee(int empNumber) {
        return loadEmployees().stream()
                .filter(e -> e.getEmployeeNumber() == empNumber)
                .findFirst();
    }

    /**
     * Save a new employee record.
     */
    public static boolean saveEmployee(Employee employee) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH, true))) {
            writer.writeNext(formatEmployeeData(employee));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Update an existing employee record.
     */
    public static void updateEmployee(Employee updatedEmployee) {
        List<Employee> employees = loadEmployees();
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmployeeNumber() == updatedEmployee.getEmployeeNumber()) {
                employees.set(i, updatedEmployee);
                break;
            }
        }
        writeEmployeeListToFile(employees);
    }

    /**
     * Delete employee and their associated login credentials.
     */
    public static void deleteEmployee(int empNum) {
        List<Employee> employees = loadEmployees();
        employees.removeIf(emp -> emp.getEmployeeNumber() == empNum);
        writeEmployeeListToFile(employees);
        deleteEmployeeLogin(empNum);
    }

    /**
     * Deletes login credentials associated with an employee number.
     */
    public static void deleteEmployeeLogin(int empNum) {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE_PATH))) {
            String[] header = reader.readNext();
            if (header != null) rows.add(header);
            
            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length > 0 && !row[0].trim().equals(String.valueOf(empNum))) {
                    rows.add(row);
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(LOGIN_FILE_PATH))) {
            writer.writeAll(rows);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * UI HELPER: Safely parses numeric input from text fields.
     * Prevents crashes if user leaves field empty or includes currency symbols.
     */
    public static double parseDoubleSafe(String value, double currentValue) {
        if (value == null || value.trim().isEmpty()) return currentValue;
        try {
            // Remove currency symbols and commas before parsing
            String cleanValue = value.trim().replaceAll("[₱,]", "");
            return Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            return currentValue;
        }
    }

    // --- Private Helpers ---

    private static void writeEmployeeListToFile(List<Employee> employees) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            writer.writeNext(new String[]{
                "EmpNum", "LastName", "FirstName", "PhoneNumber", "Status", "Position", "Supervisor", "Address",
                "SSS", "PHILHEALTH", "TIN", "PAGIBIG", "Basic Salary", "Rice Subsidy", "Phone Allowance",
                "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate", "Withholding Tax", "Birthday"
            });
            for (Employee emp : employees) {
                writer.writeNext(formatEmployeeData(emp));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] formatEmployeeData(Employee employee) {
        return new String[]{
            String.valueOf(employee.getEmployeeNumber()), employee.getLastName(), employee.getFirstName(),
            employee.getPhoneNumber(), employee.getStatus(), employee.getPosition(), employee.getSupervisor(), 
            employee.getAddress(), employee.getSssNumber(), employee.getPhilHealthNumber(), employee.getTinNumber(), 
            employee.getPagIbigNumber(), String.valueOf(employee.getBasicSalary()), String.valueOf(employee.getRiceSubsidy()), 
            String.valueOf(employee.getPhoneAllowance()), String.valueOf(employee.getClothingAllowance()), 
            String.valueOf(employee.getGrossSemiMonthlyRate()), String.valueOf(employee.getHourlyRate()), 
            String.valueOf(employee.getWithholdingTax()), employee.getBirthday()
        };
    }

    private static double parseDouble(String value) {
        try {
            return (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("N/A")) ? 0.0 : Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}