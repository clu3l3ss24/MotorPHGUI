// Required imports for managing employee data, file operations, and UI functionality.
import java.util.List;  // Enables handling of multiple employee records.
import javax.swing.JTable;  // Enables tabular display of employee records.
import javax.swing.table.DefaultTableModel;  // Manages the data structure of the JTable.
import javax.swing.table.DefaultTableCellRenderer;  // Controls the visual appearance of table cells.
import javax.swing.SwingUtilities;  // Ensures smooth UI rendering.
import javax.swing.JOptionPane;  // Displays pop-up error messages for user feedback.
import java.util.Vector;  // Facilitates structured data handling.
import java.util.Arrays;  // Provides array-based utilities.

/**
 * `EmployeeTable` class displays and manages employee records in a table format.
 * Supports real-time updates and structured data retrieval from a CSV file.
 */
public class EmployeeTable extends javax.swing.JFrame {
    private User loggedInUser;
    private boolean payslipMode = false;
    private boolean attendanceMode = false;
    private String selectedEmpNum;  // Stores the Employee Number selected in the table.

    // Instances for handling employee-related actions.
    AddEmployee addemp = new AddEmployee();  // Instance for adding new employees.
    ViewEmpInfo viewinfo = new ViewEmpInfo();  // Instance for viewing employee details.

    // Tracks the active instance of the Employee Table for easy access.
    public static EmployeeTable instance;
    
    /**
     * Constructor - Creates and initializes the EmployeeTable UI.
     * Configures table settings and loads employee records.
     */
    public EmployeeTable() {
    instance = this;
    initComponents();
    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent e) {
            instance = null;
        }
    });
    
}

public EmployeeTable(User user) {
    this.loggedInUser = user;
    initComponents();
    loadFilteredEmployees();
    this.setLocationRelativeTo(null);
}

public EmployeeTable(User user, boolean payslipMode) {
    this.loggedInUser = user;
    this.payslipMode = payslipMode;
    instance = this;
    initComponents();

    if (payslipMode) {
        setTitle("Employee Payslip");
        jLabelEmpInfo.setText("Employee Payslip");
        jButtonAdd.setVisible(false);
        jButtonUpdate.setVisible(false);
        jButtonDelete.setVisible(false);
    } else {
        setTitle("Employee Information");

        if (loggedInUser != null) {
            String access = loggedInUser.getAccess().toLowerCase();

            // Hide all action buttons if NOT HR or IT
            if (!access.contains("hr") && !access.contains("it")) {
                jButtonAdd.setVisible(false);
                jButtonUpdate.setVisible(false);
                jButtonDelete.setVisible(false);
            }
        }
    }

    configureTableModel();
    loadFilteredEmployees();
    adjustTableSettings();
    this.setLocationRelativeTo(null);
    System.out.println("Logged in as: " + loggedInUser.getAccess() + " (" + loggedInUser.getFullName() + ")");
}

public void enableAttendanceMode() {
    attendanceMode = true;

    setTitle("Employee Attendance");
    jLabelEmpInfo.setText("Employee Attendance");
    jButtonView.setText("View Attendance");

    // Hide Update and Delete for everyone
    jButtonAdd.setVisible(false);
    jButtonUpdate.setVisible(false);
    jButtonDelete.setVisible(false);

    // Supervisor logic removed — no more partial visibility

    loadFilteredEmployees(); // Show correct filtered data
}

    /**
     * Provides access to the active instance of EmployeeTable.
     * Allows other components to trigger table refresh actions.
     *
     * @return The active EmployeeTable instance.
     */
    public static EmployeeTable getInstance() {
        return instance;
    }

    /**
     * Configures the table model with predefined column headers.
     * Ensures proper column structure without including Birthday.
     */
    private void configureTableModel() {
        DefaultTableModel model = new DefaultTableModel(
            new Vector<>(Arrays.asList(
                "Employee Number", "Last Name", "First Name", "Phone Number", "Status", "Position", "Immediate Supervisor"  // Excluding Birthday
            )),
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Prevent direct editing of table cells.
            }
        };

        jTableEmpTable.setModel(model);
    }

    /**
     * Loads employee data from the CSV file and populates the table dynamically.
     */
    
    private void loadFilteredEmployees() {
        DefaultTableModel model = (DefaultTableModel) jTableEmpTable.getModel();
        model.setRowCount(0);  // Clear table

        List<Employee> employees = EmployeeFileHandler.loadEmployees();

        if (loggedInUser == null) return;

        for (Employee emp : employees) {
            boolean shouldShow = false;

            if (payslipMode) {
                if (loggedInUser.isIT()) {
                    shouldShow = true;
                } else {
                    shouldShow = String.valueOf(emp.getEmployeeNumber()).equals(loggedInUser.getEmpNum());
                }
            } else if (attendanceMode) {
                if (loggedInUser.isIT()) {
                    shouldShow = true;
                } else if (loggedInUser.isHR() || loggedInUser.isEmployee()) {
                    shouldShow = String.valueOf(emp.getEmployeeNumber()).equals(loggedInUser.getEmpNum());
                } else if (loggedInUser.isSupervisor()) {
                    boolean isSelf = String.valueOf(emp.getEmployeeNumber()).equals(loggedInUser.getEmpNum());
                    boolean isSubordinate = emp.getSupervisor() != null && emp.getSupervisor().equalsIgnoreCase(loggedInUser.getFullName());
                    shouldShow = isSelf || isSubordinate;
                }
            } else {
                // Existing logic for regular view
                if (loggedInUser.isHR() || loggedInUser.isIT()) {
                    shouldShow = true;
                } else if (loggedInUser.isSupervisor()) {
                    boolean isSelf = String.valueOf(emp.getEmployeeNumber()).equals(loggedInUser.getEmpNum());
                    boolean isSubordinate = emp.getSupervisor() != null && emp.getSupervisor().equalsIgnoreCase(loggedInUser.getFullName());
                    shouldShow = isSelf || isSubordinate;
                } else if (loggedInUser.isEmployee()) {
                    shouldShow = String.valueOf(emp.getEmployeeNumber()).equals(loggedInUser.getEmpNum());
                }
            }
            
            if (shouldShow) {
                model.addRow(new Object[]{
                    emp.getEmployeeNumber(), emp.getLastName(), emp.getFirstName(),
                    emp.getPhoneNumber(), emp.getStatus(), emp.getPosition(), emp.getSupervisor()
                });
            }
            
        }
    }
            
    private void loadEmployeeData() {
    DefaultTableModel model = (DefaultTableModel) jTableEmpTable.getModel();
    model.setRowCount(0);  // ✅ Clears old table data before reloading

    List<Employee> employees = EmployeeFileHandler.loadEmployees();  // ✅ Load employees from CSV

    if (employees == null || employees.isEmpty()) {  // ✅ Avoid NullPointerException
        System.out.println("No employees found! Check CSV formatting or reload process.");
        JOptionPane.showMessageDialog(this, "Error: Employee data failed to load!", "Error", JOptionPane.ERROR_MESSAGE);
    } else {
        for (Employee emp : employees) {
            model.addRow(new Object[]{ 
                emp.getEmployeeNumber(), emp.getLastName(), emp.getFirstName(), 
                emp.getPhoneNumber(),  // ✅ Use raw CSV data without extra formatting
                emp.getStatus(), emp.getPosition(), emp.getSupervisor()  
            });
        }
    }
}

    
    private String formatIDLive(String input) {
    StringBuilder formatted = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {  // ✅ Fix loop declaration
        formatted.append(input.charAt(i));
        if ((i + 1) % 3 == 0 && i + 1 < input.length()) {  // ✅ Ensure condition is correctly formed
            formatted.append("-");
        }
    }
    return formatted.toString();
}




    /**
     * Refreshes the EmployeeTable dynamically to reflect the latest employee records.
     * Clears and reloads table data to maintain accuracy.
     */
    public void refreshEmployeeTable() {
    if (instance != null) {
        DefaultTableModel model = (DefaultTableModel) jTableEmpTable.getModel();
        model.setRowCount(0);  // ✅ Clears previous records to prevent duplication

        List<Employee> employees = EmployeeFileHandler.loadEmployees();  // ✅ Reload fresh employee data

        if (employees.isEmpty()) {
            System.err.println("WARNING: Employee list is empty after refresh!");
        } else {
            System.out.println("Employee list successfully refreshed. Total employees: " + employees.size());
        }

        for (Employee emp : employees) {
            model.addRow(new Object[]{ 
                emp.getEmployeeNumber(), emp.getLastName(), emp.getFirstName(), 
                emp.getPhoneNumber(), emp.getStatus(), emp.getPosition(), emp.getSupervisor()
            });
        }
    }
}


    /**
     * Adjusts table settings, including column width and text alignment.
     * Ensures proper readability of employee records for users.
     */
    private void adjustTableSettings() {
        jTableEmpTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  // Prevents auto-resizing for better alignment.
        jTableEmpTable.getColumnModel().getColumn(6).setPreferredWidth(300);  // Expands Immediate Supervisor column.

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);  // Aligns text to the left.
        jTableEmpTable.getColumnModel().getColumn(6).setCellRenderer(renderer);  // Applies text alignment settings.
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButtonAdd = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jButtonView = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableEmpTable = new javax.swing.JTable();
        jButtonUpdate = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jLabelEmpInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(102, 102, 255));

        jPanel1.setBackground(new java.awt.Color(14, 49, 113));
        jPanel1.setPreferredSize(new java.awt.Dimension(1000, 600));

        jButtonAdd.setText("Add New Employee");
        jButtonAdd.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jButtonExit.setBackground(new java.awt.Color(153, 0, 0));
        jButtonExit.setForeground(new java.awt.Color(255, 255, 255));
        jButtonExit.setText("Exit");
        jButtonExit.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        jButtonView.setText("View");
        jButtonView.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonViewActionPerformed(evt);
            }
        });

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTableEmpTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Employee Number", "Last Name", "First Name", "Phone Number", "Status", "Position", "Immediate Supervisor"
            }
        ));
        jScrollPane1.setViewportView(jTableEmpTable);

        jButtonUpdate.setText("Update");
        jButtonUpdate.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateActionPerformed(evt);
            }
        });

        jButtonDelete.setText("Delete");
        jButtonDelete.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        jLabelEmpInfo.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabelEmpInfo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelEmpInfo.setText("Employee Information");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelEmpInfo)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 755, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButtonView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                            .addComponent(jButtonDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(52, 52, 52))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(124, 124, 124)
                .addComponent(jButtonView, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(jLabelEmpInfo)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 914, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        // TODO add your handling code here:
        dispose ();
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        // TODO add your handling code here:
        AddEmployee addForm = new AddEmployee(this);
        addForm.setVisible(true);
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonViewActionPerformed
    int selectedRow = jTableEmpTable.getSelectedRow();

    if (selectedRow != -1) {
        try {
            int empNum = Integer.parseInt(jTableEmpTable.getValueAt(selectedRow, 0).toString().trim());

            if (payslipMode) {
                new Payslip(String.valueOf(empNum)).setVisible(true);
}           else if (attendanceMode) {
                new Attendance(empNum, loggedInUser).setVisible(true);
}           else {
                ViewEmpInfo viewEmpInfoWindow = new ViewEmpInfo(empNum);
                viewEmpInfoWindow.setVisible(true);
            }      
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Invalid Employee Number format!", "Data Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("ERROR: Failed to parse Employee Number - " + e.getMessage());
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select an employee to view!", "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jButtonViewActionPerformed

    private void jButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateActionPerformed
    int selectedRow = jTableEmpTable.getSelectedRow(); // Get the selected row index

    if (selectedRow == -1) { // Ensure a row is selected before proceeding
        JOptionPane.showMessageDialog(this, "Please select an employee to update!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Retrieve Employee Number safely
    String empNumStr = jTableEmpTable.getValueAt(selectedRow, 0).toString().trim();
    try {
        int empNum = Integer.parseInt(empNumStr);
        
        if (attendanceMode && !loggedInUser.isIT()) {
            Employee emp = EmployeeFileHandler.getEmployeeByNumber(empNum);
            boolean isSelf = emp.getEmployeeNumber() == Integer.parseInt(loggedInUser.getEmpNum());
            boolean isSubordinate = emp.getSupervisor() != null && emp.getSupervisor().equalsIgnoreCase(loggedInUser.getFullName());

            if (loggedInUser.isHR() || loggedInUser.isEmployee()) {
                JOptionPane.showMessageDialog(this, "You do not have permission to edit attendance.");
                return;
            } else if (loggedInUser.isSupervisor()) {
                if (isSelf) {
                    JOptionPane.showMessageDialog(this, "Supervisors cannot edit their own attendance.");
                    return;
                } else if (!isSubordinate) {
                    JOptionPane.showMessageDialog(this, "You can only edit attendance of your subordinates.");
                    return;
                }
            }
        }
        
        if (attendanceMode) {
            // Open UpdateAttendance instead of EditEmpInfo
            SwingUtilities.invokeLater(() -> {
            });
        } else {
            // Open regular Edit Employee window
            SwingUtilities.invokeLater(() -> {
                EditEmpInfo editWindow = new EditEmpInfo(empNum, false, loggedInUser);
                editWindow.setVisible(true);
            });
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Error: Invalid Employee Number format!", "Data Error", JOptionPane.ERROR_MESSAGE);
        System.err.println("ERROR: Failed to parse Employee Number - " + e.getMessage());
    }
    }//GEN-LAST:event_jButtonUpdateActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
    // Handles the deletion of an employee when the "Delete" button is clicked

    int selectedRow = jTableEmpTable.getSelectedRow(); // Get the selected row index

    if (selectedRow == -1) { // Ensure a row is selected before proceeding
        JOptionPane.showMessageDialog(this, "Please select an employee to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        return; // Stop execution if no row is selected
    }

    // Ask for confirmation before deleting the employee
    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
        return; // Cancel deletion if the user selects "No"
    }

    // Retrieve Employee Number of the selected row
    int empNumToDelete = Integer.parseInt(jTableEmpTable.getValueAt(selectedRow, 0).toString());

    // Call EmployeeFileHandler to remove employee from CSV
    EmployeeFileHandler.deleteEmployee(empNumToDelete);

    // Remove employee from JTable visually
    DefaultTableModel model = (DefaultTableModel) jTableEmpTable.getModel();
    model.removeRow(selectedRow); // Delete row from table view

    // Notify the user that deletion was successful
    JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    /**
    * Hides and adjusts buttons if in payslip mode.
    * Used by the constructor to apply limited view settings.
    */
    private void applyPayslipMode() {
        if (payslipMode) {
            jButtonAdd.setVisible(false);
            jButtonUpdate.setVisible(false);
            jButtonDelete.setVisible(false);
            jButtonExit.setVisible(false);
            jButtonView.setText("View Payslip");
            jLabelEmpInfo.setText("Employee Payslip");
            setTitle("Employee Payslip");
        }
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EmployeeTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EmployeeTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EmployeeTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EmployeeTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonUpdate;
    private javax.swing.JButton jButtonView;
    private javax.swing.JLabel jLabelEmpInfo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableEmpTable;
    // End of variables declaration//GEN-END:variables
}