/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author PC
 */
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;

public class ResetPassword extends JFrame {
    private JTextField empNumField, securityField, newPasswordField;
    private JButton submitButton;

    private final String csvFile = "src/data/MotorPHlogin.csv";

    public ResetPassword() {
        setTitle("Reset Password");
        setSize(1000, 622);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Employee Number:"));
        empNumField = new JTextField();
        add(empNumField);

        add(new JLabel("What is your favorite animal?"));
        securityField = new JTextField();
        add(securityField);

        add(new JLabel("New Password:"));
        newPasswordField = new JTextField();
        add(newPasswordField);

        submitButton = new JButton("Reset Password");
        submitButton.addActionListener(e -> handlePasswordReset());
        add(submitButton);
    }

    private void handlePasswordReset() {
        String empNum = empNumField.getText().trim();
        String secAnswer = securityField.getText().trim().toLowerCase();
        String newPass = newPasswordField.getText().trim();

        if (empNum.isEmpty() || secAnswer.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        try {
            File file = new File(csvFile);
            CSVReader reader = new CSVReader(new FileReader(file));
            List<String[]> allRows = reader.readAll();
            reader.close();

            boolean found = false;
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);
                if (row[0].equals(empNum)) {
                    if (row[8].trim().equalsIgnoreCase(secAnswer)) {
                        row[7] = newPass; // Update password
                        found = true;
                        break;
                    } else {
                        JOptionPane.showMessageDialog(this, "Security answer incorrect.");
                        return;
                    }
                }
            }

            if (found) {
                CSVWriter writer = new CSVWriter(new FileWriter(file));
                writer.writeAll(allRows);
                writer.close();
                JOptionPane.showMessageDialog(this, "Password successfully reset.");
                this.dispose();
                
                new LoginScreen1().setVisible(true); // ⬅ Return to login screen
                
            } else {
                JOptionPane.showMessageDialog(this, "Employee number not found.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResetPassword().setVisible(true));
    }
}
    

