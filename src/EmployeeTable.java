import java.util.List; 
import javax.swing.JTable;  
import javax.swing.table.DefaultTableModel; 
import javax.swing.table.DefaultTableCellRenderer; 
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane; 
import java.util.Vector;  
import java.util.Arrays;  
import java.awt.Dimension;



public class EmployeeTable extends javax.swing.JFrame {
    private String selectedEmpNum; 

    private AddEmployee addemp = new AddEmployee(); 
    private ViewEmpInfo viewinfo = new ViewEmpInfo(); 

    private static EmployeeTable instance;


    /**
     * Standard constructor — used by HR, ADMIN, etc.
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

        configureTableModel();
        loadEmployeeData();
        adjustTableSettings();
    }

    /**
     * Constructor for SUPPORT role — only loads supervised employees
     */
    public EmployeeTable(String supervisorId, String labelText) {
    instance = this;
    initComponents();
    jLabelEmpInfo.setText(labelText);   
    applyRoleRestrictions();           
    
    //  Set jButtonView label based on view mode
    if ("Employee Attendance".equalsIgnoreCase(labelText)) {
        jButtonView.setText("View Attendance");
        jButtonView.setVisible(true);
    } else if ("Employee Payslip".equalsIgnoreCase(labelText)) {
        jButtonView.setText("View Payslip");
        jButtonView.setVisible(true);
    }

    //  Ensure window closes cleanly
    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent e) {
            instance = null;
        }
    });

    configureTableModel();  // ?Initialize table columns

    // ️ Load employee data based on label + role
    String role = User.getLoggedInUser().getRole().trim();

    if ("Employee Payslip".equalsIgnoreCase(labelText)) {
        loadEmployeeData(); // All employees shown in Payslip mode

    } else if ("Employee Attendance".equalsIgnoreCase(labelText)) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            loadEmployeeData(); // ADMIN sees full list for Attendance
        } else {
            loadSupervisedEmployees(supervisorId); // SUPPORT sees filtered list
        }

    } else {
        loadSupervisedEmployees(supervisorId); // Fallback for any other label
    }

    adjustTableSettings(); // Final formatting and column alignment
}




   
    private void applyRoleRestrictions() {
    String role = User.getLoggedInUser().getRole();
    String viewLabel = jLabelEmpInfo.getText().trim();

    // Restrict SUPPORT always, and ADMIN only in Payslip & Attendance modes
    if ("SUPPORT".equalsIgnoreCase(role) || 
        ("ADMIN".equalsIgnoreCase(role) && 
         ("Employee Payslip".equalsIgnoreCase(viewLabel) ||
          "Employee Attendance".equalsIgnoreCase(viewLabel)))) {

        Dimension originalSize = new Dimension(120, 35);

        if (jButtonAdd != null) {
            jButtonAdd.setText("");
            jButtonAdd.setEnabled(false);
            jButtonAdd.setPreferredSize(originalSize);
            jButtonAdd.setMinimumSize(originalSize);
            jButtonAdd.setMaximumSize(originalSize);
            jButtonAdd.setContentAreaFilled(false);
            jButtonAdd.setBorderPainted(false);
        }

        if (jButtonUpdate != null) {
            jButtonUpdate.setText("");
            jButtonUpdate.setEnabled(false);
            jButtonUpdate.setPreferredSize(originalSize);
            jButtonUpdate.setMinimumSize(originalSize);
            jButtonUpdate.setMaximumSize(originalSize);
            jButtonUpdate.setContentAreaFilled(false);
            jButtonUpdate.setBorderPainted(false);
        }

        if (jButtonDelete != null) {
            jButtonDelete.setText("");
            jButtonDelete.setEnabled(false);
            jButtonDelete.setPreferredSize(originalSize);
            jButtonDelete.setMinimumSize(originalSize);
            jButtonDelete.setMaximumSize(originalSize);
            jButtonDelete.setContentAreaFilled(false);
            jButtonDelete.setBorderPainted(false);
        }
    }
}





    /**
     * Get current table instance.
     */
    public static EmployeeTable getInstance() {
        return instance;
    }

    
    public String getSelectedEmpNum() { 
        return selectedEmpNum; 
    }
    /**
     * Table column config.
     */
    private void configureTableModel() {
        DefaultTableModel model = new DefaultTableModel(
            new Vector<>(Arrays.asList(
                "Employee Number", "Last Name", "First Name", "Phone Number", "Status", "Position", "Immediate Supervisor"
            )),
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jTableEmpTable.setModel(model);
    }

    /**
     * Loads full employee list — used by HR, ADMIN, etc.
     */
    public void loadEmployeeData() {
        DefaultTableModel model = (DefaultTableModel) jTableEmpTable.getModel();
        model.setRowCount(0);

        List<Employee> employees = EmployeeFileHandler.loadEmployees();

        if (employees == null || employees.isEmpty()) {
            System.out.println("No employees found! Check CSV formatting or reload process.");
            JOptionPane.showMessageDialog(this, "Error: Employee data failed to load!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            for (Employee emp : employees) {
                model.addRow(new Object[]{ 
                    emp.getEmployeeNumber(), emp.getLastName(), emp.getFirstName(), 
                    emp.getPhoneNumber(), emp.getStatus(), emp.getPosition(), emp.getSupervisor()
                });
            }
        }
    }

    /**
     *  Loads only employees supervised by the specified user
     */
    private void loadSupervisedEmployees(String supervisorId) {
    DefaultTableModel model = (DefaultTableModel) jTableEmpTable.getModel();
    model.setRowCount(0);

    List<Employee> employees = EmployeeFileHandler.loadEmployees();

    if (employees == null || employees.isEmpty()) {
        System.out.println("No employees found! Check CSV formatting or reload process.");
        JOptionPane.showMessageDialog(this, "Error: Employee data failed to load!", "Error", JOptionPane.ERROR_MESSAGE);
    } else {
        // Build full supervisor name in "LastName  FirstName" format
        String currentSupervisorName = User.getLoggedInUser().getLastName().trim() + "  " + User.getLoggedInUser().getFirstName().trim();

        for (Employee emp : employees) {
            String listedSupervisor = emp.getSupervisor() != null ? emp.getSupervisor().trim() : "";
            String employeeFullName = emp.getLastName().trim() + "  " + emp.getFirstName().trim();

            if (listedSupervisor.equalsIgnoreCase(currentSupervisorName) ||
                employeeFullName.equalsIgnoreCase(currentSupervisorName)) {

                model.addRow(new Object[]{ 
                    emp.getEmployeeNumber(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    emp.getPhoneNumber(),
                    emp.getStatus(),
                    emp.getPosition(),
                    emp.getSupervisor()
                });
            }
        }
    }
}

    

    private String formatIDLive(String input) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            formatted.append(input.charAt(i));
            if ((i + 1) % 3 == 0 && i + 1 < input.length()) {
                formatted.append("-");
            }
        }
        return formatted.toString();
    }

    /**
     * Reloads full employee table
     */
    public void refreshEmployeeTable() {
        if (instance != null) {
            DefaultTableModel model = (DefaultTableModel) jTableEmpTable.getModel();
            model.setRowCount(0);

            List<Employee> employees = EmployeeFileHandler.loadEmployees();

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
     * Align and resize final column
     */
        private void adjustTableSettings() {
        jTableEmpTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jTableEmpTable.getColumnModel().getColumn(6).setPreferredWidth(300);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
        jTableEmpTable.getColumnModel().getColumn(6).setCellRenderer(renderer);
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
        jLabel1 = new javax.swing.JLabel();

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

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 8)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("CP2 H1102 SY24-25 Team Petix - C.Oreta, S.Singh, R.Sisles, J.Singh, D.Sumatra");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
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
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButtonView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButtonUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                                    .addComponent(jButtonDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(38, 38, 38))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(jButtonView, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(17, Short.MAX_VALUE)
                        .addComponent(jLabelEmpInfo)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addComponent(jLabel1)
                .addContainerGap())
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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
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
        addemp.show();
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonViewActionPerformed
                        
    int selectedRow = jTableEmpTable.getSelectedRow(); // Get selected row index

    if (selectedRow != -1) {
        String empNumber = jTableEmpTable.getValueAt(selectedRow, 0).toString().trim();
        
        // Store the selected employee number in the class field
        selectedEmpNum = empNumber;

        String viewMode = jLabelEmpInfo.getText().trim();

        try {
            if ("Employee Attendance".equalsIgnoreCase(viewMode)) {
                // SUPPORT view → open Attendance screen
                Attendance attendanceWindow = new Attendance(empNumber);
                attendanceWindow.setVisible(true);
                attendanceWindow.setLocationRelativeTo(null);

            } else if ("Employee Payslip".equalsIgnoreCase(viewMode)) {
                // ADMIN/SUPPORT → open Payslip screen
                Payslip payslipWindow = new Payslip(empNumber);
                payslipWindow.setVisible(true);
                payslipWindow.setLocationRelativeTo(null);

            } else {
                // Default view → open EditEmpInfo in read-only mode
                int empNumInt = Integer.parseInt(empNumber); // Validate number format
                EditEmpInfo editEmpInfoWindow = new EditEmpInfo(empNumInt, true); 
                editEmpInfoWindow.setVisible(true);
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

    // Store the selected employee number in the class field
    selectedEmpNum = empNumStr;

    try {
        int empNum = Integer.parseInt(empNumStr);

        // Open the Edit Employee window in Editable Mode
        SwingUtilities.invokeLater(() -> {
            EditEmpInfo editWindow = new EditEmpInfo(empNum, false);  
            editWindow.setVisible(true);
        });

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

    // Store the selected employee number in the class field
    selectedEmpNum = String.valueOf(empNumToDelete);

    // Call EmployeeFileHandler to remove employee from CSV
    EmployeeFileHandler.deleteEmployee(empNumToDelete);
    EmployeeFileHandler.deleteEmployeeLogin(empNumToDelete);

    // Remove employee from JTable visually
    DefaultTableModel model = (DefaultTableModel) jTableEmpTable.getModel();
    model.removeRow(selectedRow); // Delete row from table view

    // Notify the user that deletion was successful
    JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
    }//GEN-LAST:event_jButtonDeleteActionPerformed

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
                new EmployeeTable().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonUpdate;
    private javax.swing.JButton jButtonView;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelEmpInfo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableEmpTable;
    // End of variables declaration//GEN-END:variables
}
