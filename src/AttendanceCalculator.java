import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException; 
import java.util.*;

public class AttendanceCalculator {
    private static final double STANDARD_WORK_HOURS = 40.0; 
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static double[] computeMonthlyHoursAndOT(List<AttendanceRecord> records, int empNo, int targetMonth, int targetYear) {
        double totalWorkedHours = 0.0;
        double overtimeHours = 0.0;

        for (AttendanceRecord record : records) {
            // Check if record belongs to employee
            if (!record.getEmpNo().equals(String.valueOf(empNo))) continue;
            
            try {
                // If parsing fails, it skips this specific record instead of crashing
                LocalDate logDate = LocalDate.parse(record.getDate(), DATE_FMT);
                
                if (logDate.getMonthValue() != targetMonth || logDate.getYear() != targetYear) continue;
                if (logDate.getDayOfWeek() == DayOfWeek.SUNDAY) continue;

                totalWorkedHours += record.calculateWorkedHours();
                overtimeHours += record.calculateOvertimeMinutes() / 60.0;
                
            } catch (DateTimeParseException | NullPointerException e) {
                // Log the error to console so you can find the bad row in your CSV
                System.err.println("Calculator skipping bad row: " + record.getDate() + " for Emp: " + empNo);
            }
        }
        return new double[]{ Math.round(totalWorkedHours * 100.0) / 100.0, Math.round(overtimeHours * 100.0) / 100.0 };
    }

    public static Map<String, Double> computeDailyAttendanceMinutes(List<AttendanceRecord> records, int empNo, String logDateStr) {
        Map<String, Double> result = new HashMap<>();
        result.put("Late", 0.0); 
        result.put("Overtime", 0.0); 
        result.put("Undertime", 0.0);

        try {
            LocalDate targetDate = LocalDate.parse(logDateStr, DATE_FMT);

            for (AttendanceRecord record : records) {
                if (!record.getEmpNo().equals(String.valueOf(empNo))) continue;

                try {
                    LocalDate recordDate = LocalDate.parse(record.getDate(), DATE_FMT);
                    if (recordDate.isEqual(targetDate)) {
                        result.put("Late", (double) record.calculateLateMinutes());
                        result.put("Overtime", (double) record.calculateOvertimeMinutes());
                        result.put("Undertime", (double) record.calculateUndertimeMinutes());
                        return result;
                    }
                } catch (DateTimeParseException e) {
                    // Skip malformed record dates during the search
                }
            }
        } catch (DateTimeParseException e) {
            System.err.println("Invalid Target Date requested: " + logDateStr);
        }
        
        return result;
    }
}