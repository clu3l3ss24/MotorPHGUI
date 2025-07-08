/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author ryan
 */

public class User {
    private String empNum;
    private String fullName;
    private String access;
    private String position;

    public User(String empNum, String fullName, String access, String position) {
        this.empNum = empNum;
        this.fullName = fullName;
        this.access = access.trim();
        this.position = position;
    }

    public String getEmpNum() {
        return empNum;
    }

    // Alias for compatibility
    public String getEmployeeNumber() {
        return empNum;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAccess() {
        return access;
    }

    public String getPosition() {
        return position;
    }

    public boolean isHR() {
        return access.toLowerCase().contains("hr");
    }

    public boolean isIT() {
        return access.toLowerCase().contains("it");
    }

    public boolean isSupervisor() {
        return access.toLowerCase().contains("supervisor");
    }

    public boolean isEmployee() {
        return access.equalsIgnoreCase("employee");
    }
}