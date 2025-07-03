/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This class holds the login and identity information for a user.
 * It helps us check their role and access level across the system.
 * @author ryan
 */
public class User {
    private String employeeNumber;
    private String fullName;
    private String accessLevel;
    private String position;
    private String supervisor;

    // Constructor: this builds a User object from one line of the CSV
    public User(String employeeNumber, String fullName, String accessLevel, String position, String supervisor) {
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.accessLevel = accessLevel;
        this.position = position;
        this.supervisor = supervisor;
    }

    // Getters: other classes will use these to get user info
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public String getPosition() {
        return position;
    }

    public String getSupervisor() {
        return supervisor;
    }

    // Helper methods to easily check what kind of user this is
    public boolean isHR() {
        return accessLevel.equalsIgnoreCase("HR");
    }

    public boolean isIT() {
        return accessLevel.equalsIgnoreCase("IT");
    }

    public boolean isSupervisor() {
        return accessLevel.equalsIgnoreCase("Supervisor");
    }

    public boolean isEmployee() {
        return accessLevel.equalsIgnoreCase("Employee");
    }
}