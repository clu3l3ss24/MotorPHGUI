import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class AttendanceRecord {
    private final String empNo;
    private final String date;
    private String lastName;
    private String firstName;
    private String logIn;
    private String logOut;

    private static final LocalTime OFFICE_START = LocalTime.of(8, 0);
    private static final LocalTime OFFICE_END = LocalTime.of(17, 0);
    private static final LocalTime GRACE_PERIOD = OFFICE_START.plusMinutes(10);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");

    public AttendanceRecord(String empNo, String lastName, String firstName,
                            String date, String logIn, String logOut) {
        this.empNo = empNo;
        this.lastName = lastName;
        this.firstName = firstName;
        this.date = date;
        this.logIn = logIn;
        this.logOut = logOut;
    }

    public long calculateLateMinutes() {
        if (isInvalidTime(logIn)) return 0;
        try {
            LocalTime timeIn = LocalTime.parse(logIn, TIME_FMT);
            return timeIn.isAfter(GRACE_PERIOD) ? Duration.between(OFFICE_START, timeIn).toMinutes() : 0;
        } catch (Exception e) { return 0; }
    }

    public long calculateOvertimeMinutes() {
        if (isInvalidTime(logOut)) return 0;
        try {
            LocalTime timeOut = LocalTime.parse(logOut, TIME_FMT);
            return timeOut.isAfter(OFFICE_END) ? Duration.between(OFFICE_END, timeOut).toMinutes() : 0;
        } catch (Exception e) { return 0; }
    }

    public long calculateUndertimeMinutes() {
        if (isInvalidTime(logOut)) return 0;
        try {
            LocalTime timeOut = LocalTime.parse(logOut, TIME_FMT);
            return timeOut.isBefore(OFFICE_END) ? Duration.between(timeOut, OFFICE_END).toMinutes() : 0;
        } catch (Exception e) { return 0; }
    }

    public double calculateWorkedHours() {
        if (isInvalidTime(logIn) || isInvalidTime(logOut)) return 0.0;
        try {
            LocalTime timeIn = LocalTime.parse(logIn, TIME_FMT);
            LocalTime timeOut = LocalTime.parse(logOut, TIME_FMT);
            return timeOut.isAfter(timeIn) ? Duration.between(timeIn, timeOut).toMinutes() / 60.0 : 0.0;
        } catch (Exception e) { return 0.0; }
    }

    private boolean isInvalidTime(String time) {
        return time == null || time.isEmpty() || time.equalsIgnoreCase("N/A");
    }

    public String getEmpNo() { return empNo; }
    public String getDate() { return date; }
    public String getLastName() { return lastName; }
    public void setLastName(String ln) { this.lastName = ln; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String fn) { this.firstName = fn; }
    public String getLogIn() { return logIn; }
    public void setLogIn(String li) { this.logIn = li; }
    public String getLogOut() { return logOut; }
    public void setLogOut(String lo) { this.logOut = lo; }
}