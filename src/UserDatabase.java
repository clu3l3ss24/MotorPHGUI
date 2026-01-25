import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.util.*;

/**
 * Handles all storage and retrieval logic for employee login credentials.
 * Implements encapsulation by hiding CSV column indices and file paths.
 */
public class UserDatabase {
    private static final String LOGIN_FILE = "src/data/employee_logins.csv";
    private static final int COL_EMP_ID = 0;
    private static final int COL_LAST_NAME = 1;
    private static final int COL_FIRST_NAME = 2;
    private static final int COL_USERNAME = 5;
    private static final int COL_PASSWORD = 6;
    private static final int COL_SEC_QUESTION = 7;
    private static final int COL_SEC_ANSWER = 8;
    private static final int COL_ROLE = 9;

    /**
     * Authenticates user and returns a User object if successful.
     */
    public static User authenticate(String username, String password) {
        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            String[] row;
            reader.readNext(); // Skip header

            while ((row = reader.readNext()) != null) {
                if (row.length > COL_PASSWORD) {
                    if (row[COL_USERNAME].trim().equals(username) && 
                        row[COL_PASSWORD].trim().equals(password)) {
                        return mapRowToUser(row);
                    }
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Fetches the security question for the given Employee ID.
     */
    public static String getSecurityQuestion(String empId) {
        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            String[] row;
            reader.readNext(); // Skip header
            while ((row = reader.readNext()) != null) {
                if (row.length > COL_SEC_QUESTION && row[COL_EMP_ID].trim().equals(empId)) {
                    return row[COL_SEC_QUESTION].trim();
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error reading question: " + e.getMessage());
        }
        return null;
    }

    /**
     * Verifies the answer provided for the security question.
     */
    public static boolean verifySecurityAnswer(String empId, String inputAnswer) {
        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            String[] row;
            reader.readNext(); // Skip header
            while ((row = reader.readNext()) != null) {
                if (row.length > COL_SEC_ANSWER && row[COL_EMP_ID].trim().equals(empId)) {
                    return row[COL_SEC_ANSWER].trim().equalsIgnoreCase(inputAnswer.trim());
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error verifying answer: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates the password for a specific employee ID.
     */
    public static boolean updatePassword(String empId, String newPassword) {
        List<String[]> allRows = readAllRows();
        boolean found = false;

        for (String[] row : allRows) {
            if (row.length > COL_PASSWORD && row[COL_EMP_ID].trim().equals(empId)) {
                row[COL_PASSWORD] = newPassword;
                found = true;
                break;
            }
        }

        if (found) {
            writeAllRows(allRows);
        }
        return found;
    }

    private static User mapRowToUser(String[] row) {
        return new User(
            row[COL_EMP_ID].trim(),
            row[COL_FIRST_NAME].trim(),
            row[COL_LAST_NAME].trim(),
            row[COL_USERNAME].trim(),
            row[COL_PASSWORD].trim(),
            row[COL_ROLE].trim()
        );
    }

    private static List<String[]> readAllRows() {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_FILE))) {
            rows.addAll(reader.readAll());
        } catch (IOException | CsvException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
        return rows;
    }

    private static void writeAllRows(List<String[]> rows) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(LOGIN_FILE))) {
            writer.writeAll(rows);
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }
}