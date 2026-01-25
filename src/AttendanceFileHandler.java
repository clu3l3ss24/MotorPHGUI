import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class AttendanceFileHandler {
    private static final String FILE_PATH = "src/data/Attendance Record.csv";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");

    public static List<AttendanceRecord> readAllAttendanceRecords() throws IOException, CsvValidationException {
        List<AttendanceRecord> records = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return records;

        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            String[] line;
            boolean skipHeader = true;
            while ((line = reader.readNext()) != null) {
                if (skipHeader) { skipHeader = false; continue; }
                if (line.length < 6) continue;
                records.add(new AttendanceRecord(line[0], line[1], line[2], line[3], line[4], line[5]));
            }
        }
        return records;
    }

    public static boolean updateAttendanceRecords(List<AttendanceRecord> updatedRecords) 
            throws IOException, CsvValidationException {
        List<AttendanceRecord> allRecords = readAllAttendanceRecords();
        boolean changed = false;

        for (AttendanceRecord updated : updatedRecords) {
            for (AttendanceRecord existing : allRecords) {
                // Match by Employee ID and Date
                if (existing.getEmpNo().equals(updated.getEmpNo()) && existing.getDate().equals(updated.getDate())) {
                    existing.setLogIn(updated.getLogIn());
                    existing.setLogOut(updated.getLogOut());
                    changed = true;
                    break;
                }
            }
        }

        if (changed) {
            rewriteFile(allRecords);
        }
        return changed;
    }

    public static List<String[]> getAttendanceRecords(String empNo, LocalDate startDate, LocalDate endDate) {
        List<String[]> filtered = new ArrayList<>();
        try {
            for (AttendanceRecord r : readAllAttendanceRecords()) {
                if (!r.getEmpNo().equals(empNo)) continue;
                
                try {
                    // Skip rows with corrupt date formats
                    LocalDate logDate = LocalDate.parse(r.getDate(), DATE_FMT);
                    if (!logDate.isBefore(startDate) && !logDate.isAfter(endDate)) {
                        filtered.add(new String[]{r.getDate(), r.getLogIn(), r.getLogOut()});
                    }
                } catch (DateTimeParseException e) {
                    System.err.println("Skipping malformed attendance date: " + r.getDate());
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading records: " + e.getMessage());
        }
        return filtered;
    }

    public static void logTimeIn(User user) throws IOException {
        String today = LocalDate.now().format(DATE_FMT);
        String now = LocalTime.now().format(TIME_FMT);
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH, true))) {
            writer.writeNext(new String[]{
                user.getEmployeeId(), user.getLastName(), user.getFirstName(),
                today, now, "N/A"
            });
        }
    }

    public static void logTimeOut(User user) throws IOException, CsvValidationException {
        List<AttendanceRecord> allRecords = readAllAttendanceRecords();
        String today = LocalDate.now().format(DATE_FMT);
        String now = LocalTime.now().format(TIME_FMT);
        boolean found = false;
        for (AttendanceRecord record : allRecords) {
            if (record.getEmpNo().equals(user.getEmployeeId()) && record.getDate().equals(today)) {
                record.setLogOut(now);
                found = true;
                break;
            }
        }
        if (found) rewriteFile(allRecords);
    }

    private static void rewriteFile(List<AttendanceRecord> records) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            writer.writeNext(new String[]{"Employee #", "Last Name", "First Name", "Date", "Log In", "Log Out"});
            for (AttendanceRecord r : records) {
                writer.writeNext(new String[]{
                    r.getEmpNo(), r.getLastName(), r.getFirstName(),
                    r.getDate(), r.getLogIn(), r.getLogOut()
                });
            }
        }
    }
}