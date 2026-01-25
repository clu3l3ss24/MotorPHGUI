import javax.swing.JOptionPane;
import java.util.List;
import java.util.Optional;
import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent; 

public class EditEmpInfo extends javax.swing.JFrame {

    private int empNumToEdit;
    private Employee employeeData;
    private boolean readOnly;
    private JTextField FirstName;

   public EditEmpInfo(int empNum, boolean readOnly) {
    initComponents(); // NetBeans initializes the UI components here
    this.empNumToEdit = empNum;
    this.readOnly = readOnly;


    loadEmployeeData(empNumToEdit);

    if (readOnly) {
        disableEditing();
    }

    // Apply listeners for real-time formatting
    addKeyListenerToField(SSS);
    addKeyListenerToField(PAGIBIG);
    addKeyListenerToField(PHILHEALTH);
    addKeyListenerToField(TIN);
    addKeyListenerToField(PhoneNum);
}


    private void loadEmployeeData(int empNum) {
    // Delegate searching to the EmployeeFileHandler expert
    EmployeeFileHandler.getEmployee(empNum).ifPresentOrElse(emp -> {
        employeeData = emp;

        // Populate Basic Info
        txtLname.setText(emp.getLastName());
        // Ensure both names are visible in the header
        Name.setText(emp.getLastName() + ", " + emp.getFirstName()); 
        
        // Populate Editable Fields
        Position.setText(emp.getPosition());
        PhoneNum.setText(emp.getPhoneNumber());
        Status.setText(emp.getStatus());
        ImmSup.setText(emp.getSupervisor());
        Address.setText(emp.getAddress());
        Birthday.setText(emp.getBirthday());

        // Populate Government IDs
        SSS.setText(emp.getSssNumber());
        PHILHEALTH.setText(emp.getPhilHealthNumber());
        TIN.setText(emp.getTinNumber());
        PAGIBIG.setText(emp.getPagIbigNumber());

        // Populate Financials (formatted to 2 decimal places)
        Salary.setText(String.format("%.2f", emp.getBasicSalary()));
        Rice.setText(String.format("%.2f", emp.getRiceSubsidy()));
        PhoneAll.setText(String.format("%.2f", emp.getPhoneAllowance()));
        ClothAll.setText(String.format("%.2f", emp.getClothingAllowance()));
        Hourly.setText(String.format("%.2f", emp.getHourlyRate()));
        
    }, () -> {
        JOptionPane.showMessageDialog(this, "Error: Employee not found!", "Error", JOptionPane.ERROR_MESSAGE);
        this.dispose();
    });
}

    /**
     * Disables editing when in read-only mode.
     * Prevents modifications to employee details while viewing.
     */
    private void disableEditing() {
    Name.setEditable(false);
    Position.setEditable(false);
    PhoneNum.setEditable(false);
    Status.setEditable(false);
    ImmSup.setEditable(false);

    // 🔄 Instead of disabling, hide the buttons completely
    Save.setVisible(false);
    Edit.setVisible(false);
}


    /**
     * Toggles field editability when switching between view and edit mode.
     * Enables or disables input fields based on the `editable` parameter.
     *
     * @param editable If true, allows editing of employee details.
     */
    private void setFieldsEditable(boolean editable) {
    // Enable/disable employee details fields
    Name.setEditable(editable);
    Position.setEditable(editable);
    PhoneNum.setEditable(editable);
    Status.setEditable(editable);
    ImmSup.setEditable(editable);
    Address.setEditable(editable);
    Birthday.setEditable(editable);

    // Enable/disable financial fields
    SSS.setEditable(editable);
    PAGIBIG.setEditable(editable);
    PHILHEALTH.setEditable(editable);
    TIN.setEditable(editable);
    Salary.setEditable(editable);
    Rice.setEditable(editable);
    PhoneAll.setEditable(editable);
    ClothAll.setEditable(editable);
    Hourly.setEditable(editable);

    // Toggle Save and Edit buttons
    Save.setEnabled(editable);
    Edit.setEnabled(!editable);
}


    /**
     * Saves updated employee details using `EmployeeFileHandler.updateEmployee()`.
     * Ensures that changes are stored in the CSV file after editing.
     */
   private void saveEmployeeUpdates() {
    if (employeeData == null) {
        JOptionPane.showMessageDialog(this, "Error: No employee data loaded!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    //  Use raw input instead of applying formatIDLive() again
    employeeData.setPhoneNumber(PhoneNum.getText().trim());  

    //  Save updates using EmployeeFileHandler
    EmployeeFileHandler.updateEmployee(employeeData);

    //  Refresh employee table after saving
    if (EmployeeTable.getInstance() != null) {
        EmployeeTable.getInstance().refreshEmployeeTable();
    }

    JOptionPane.showMessageDialog(this, "Employee details updated successfully!");
    dispose();  // Close EditEmpInfo window
}


    
    private void addKeyListenerToField(JTextField field) {
    field.addKeyListener(new KeyAdapter() {
        public void keyReleased(KeyEvent evt) {
            String rawInput = field.getText().replaceAll("[^0-9]", "");  // Remove non-numeric characters
            field.setText(formatIDLive(rawInput));  // Apply hyphen formatting
        }
    });
}
    
    private String formatIDLive(String input) {
    StringBuilder formatted = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
        formatted.append(input.charAt(i));
        if ((i + 1) % 3 == 0 && i + 1 < input.length()) {
            formatted.append("-");
        }
    }
    return formatted.toString().replaceAll("--", "-");  // ✅ Prevent double hyphens
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerLogo = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        Name = new javax.swing.JTextField();
        Position = new javax.swing.JTextField();
        Status = new javax.swing.JTextField();
        textLogo = new javax.swing.JLabel();
        txtLname = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        SSSname = new javax.swing.JLabel();
        PAGIBIGname = new javax.swing.JLabel();
        PHILHEALTHname = new javax.swing.JLabel();
        TINname = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        Save = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        Edit = new javax.swing.JButton();
        PAGIBIG = new javax.swing.JTextField();
        PHILHEALTH = new javax.swing.JTextField();
        TIN = new javax.swing.JTextField();
        SSS = new javax.swing.JFormattedTextField();
        Salary = new javax.swing.JFormattedTextField();
        Hourly = new javax.swing.JFormattedTextField();
        PhoneAll = new javax.swing.JFormattedTextField();
        ClothAll = new javax.swing.JFormattedTextField();
        Rice = new javax.swing.JFormattedTextField();
        ImmSup = new javax.swing.JTextField();
        PhoneNum = new javax.swing.JTextField();
        Birthday = new javax.swing.JTextField();
        Address = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        headerLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoHeader.png"))); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(14, 49, 113));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 176, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 162, Short.MAX_VALUE)
        );

        Name.setEditable(false);
        Name.setBackground(new java.awt.Color(204, 204, 204));
        Name.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        Name.setForeground(new java.awt.Color(51, 51, 51));
        Name.setText("Garcia, Manuel III");
        Name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NameActionPerformed(evt);
            }
        });

        Position.setEditable(false);
        Position.setBackground(new java.awt.Color(204, 204, 204));
        Position.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Position.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Position.setText("Chief Excecutive Officer");
        Position.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PositionActionPerformed(evt);
            }
        });

        Status.setEditable(false);
        Status.setBackground(new java.awt.Color(204, 204, 204));
        Status.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Status.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Status.setText("Regular");
        Status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StatusActionPerformed(evt);
            }
        });

        textLogo.setFont(new java.awt.Font("Kinetika Bold", 0, 55)); // NOI18N
        textLogo.setForeground(new java.awt.Color(255, 255, 255));
        textLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        textLogo.setText("MotorPH");

        txtLname.setBackground(new java.awt.Color(14, 49, 113));
        txtLname.setForeground(new java.awt.Color(14, 49, 113));
        txtLname.setText("jTextField1");
        txtLname.setCaretColor(new java.awt.Color(14, 49, 113));
        txtLname.setDisabledTextColor(new java.awt.Color(14, 49, 113));
        txtLname.setEnabled(false);
        txtLname.setFocusable(false);
        txtLname.setSelectedTextColor(new java.awt.Color(14, 49, 113));
        txtLname.setSelectionColor(new java.awt.Color(14, 49, 113));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 95, Short.MAX_VALUE)
                .addComponent(Status, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(105, 105, 105))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(Position, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(txtLname, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textLogo)
                .addGap(51, 51, 51))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Name, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Position, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(textLogo)
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(txtLname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39))))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setToolTipText("");
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        SSSname.setText("SSS:");
        jPanel3.add(SSSname, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 84, 123, -1));

        PAGIBIGname.setText("PAGIBIG:");
        jPanel3.add(PAGIBIGname, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 114, 123, -1));

        PHILHEALTHname.setText("PHILHEALTH:");
        jPanel3.add(PHILHEALTHname, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 144, 123, -1));

        TINname.setText("TIN:");
        jPanel3.add(TINname, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 174, 99, -1));
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(305, 171, 123, -1));

        jLabel11.setText("Basic Salary:");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(317, 81, 123, 24));

        jLabel14.setText("Phone Number:");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 320, 146, -1));

        jLabel15.setText("Birthday:");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 350, 145, -1));

        jLabel16.setText("Address:");
        jPanel3.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 380, 145, -1));

        jLabel17.setText("Immediate Supervisor:");
        jPanel3.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 290, -1, -1));

        jLabel18.setText("Rice Subsidy:");
        jPanel3.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(317, 204, 144, -1));

        jLabel19.setText("Phone Allowance:");
        jPanel3.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(317, 144, 123, -1));

        jLabel20.setText("Hourly Rate:");
        jPanel3.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(317, 114, 123, -1));

        jLabel21.setText("Clothing Allowance:");
        jPanel3.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(317, 174, 144, -1));

        Save.setBackground(new java.awt.Color(14, 49, 113));
        Save.setForeground(new java.awt.Color(255, 255, 255));
        Save.setText("Save");
        Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveActionPerformed(evt);
            }
        });
        jPanel3.add(Save, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 510, 120, -1));

        jButton2.setBackground(new java.awt.Color(153, 0, 51));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Exit");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 510, 85, -1));

        Edit.setBackground(new java.awt.Color(14, 49, 113));
        Edit.setForeground(new java.awt.Color(255, 255, 255));
        Edit.setText("Edit");
        Edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditActionPerformed(evt);
            }
        });
        jPanel3.add(Edit, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 510, 120, -1));

        PAGIBIG.setEditable(false);
        PAGIBIG.setText("691295330870");
        PAGIBIG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PAGIBIGActionPerformed(evt);
            }
        });
        jPanel3.add(PAGIBIG, new org.netbeans.lib.awtextra.AbsoluteConstraints(176, 111, 123, -1));

        PHILHEALTH.setEditable(false);
        PHILHEALTH.setText("820126853951");
        PHILHEALTH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PHILHEALTHActionPerformed(evt);
            }
        });
        jPanel3.add(PHILHEALTH, new org.netbeans.lib.awtextra.AbsoluteConstraints(176, 141, 123, -1));

        TIN.setEditable(false);
        TIN.setText("442-605-657-000");
        TIN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TINActionPerformed(evt);
            }
        });
        jPanel3.add(TIN, new org.netbeans.lib.awtextra.AbsoluteConstraints(176, 171, 123, -1));

        SSS.setEditable(false);
        SSS.setText("44-4506057-3");
        jPanel3.add(SSS, new org.netbeans.lib.awtextra.AbsoluteConstraints(176, 81, 123, -1));

        Salary.setEditable(false);
        Salary.setText("₱90,000.00");
        Salary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SalaryActionPerformed(evt);
            }
        });
        jPanel3.add(Salary, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 81, 101, -1));

        Hourly.setEditable(false);
        Hourly.setText("₱535.71");
        jPanel3.add(Hourly, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 111, 101, -1));

        PhoneAll.setEditable(false);
        PhoneAll.setText("₱2,000.00");
        jPanel3.add(PhoneAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 141, 101, -1));

        ClothAll.setEditable(false);
        ClothAll.setText("₱1,000.00");
        jPanel3.add(ClothAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 171, 101, -1));

        Rice.setEditable(false);
        Rice.setText("₱1,500.00");
        jPanel3.add(Rice, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 201, 101, -1));

        ImmSup.setEditable(false);
        ImmSup.setText("N/A");
        ImmSup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImmSupActionPerformed(evt);
            }
        });
        jPanel3.add(ImmSup, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 287, 123, -1));

        PhoneNum.setEditable(false);
        PhoneNum.setText("966-860-270");
        PhoneNum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PhoneNumActionPerformed(evt);
            }
        });
        jPanel3.add(PhoneNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 317, 123, -1));

        Birthday.setEditable(false);
        Birthday.setText("10/11/1983");
        Birthday.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BirthdayActionPerformed(evt);
            }
        });
        jPanel3.add(Birthday, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 347, 123, -1));

        Address.setEditable(false);
        Address.setText("Valero Carpark Building Valero Street 1227, Makati City");
        Address.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddressActionPerformed(evt);
            }
        });
        jPanel3.add(Address, new org.netbeans.lib.awtextra.AbsoluteConstraints(174, 377, 394, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 8)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("CP2 H1102 SY24-25 Team Petix - C.Oreta, S.Singh, R.Sisles, J.Singh, D.Sumatra");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 570, 340, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TINActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TINActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TINActionPerformed

    private void PHILHEALTHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PHILHEALTHActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PHILHEALTHActionPerformed

    private void PAGIBIGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PAGIBIGActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PAGIBIGActionPerformed

    private void EditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditActionPerformed
        // TODO add your handling code here:
        setFieldsEditable(true); //Unlock fields for editinG
 
    }//GEN-LAST:event_EditActionPerformed

    private void SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveActionPerformed
                                    
    if (employeeData == null) return;

    try {
        // 1. Update Text Fields (using .trim() to keep data clean)
        employeeData.setPhoneNumber(PhoneNum.getText().trim());
        employeeData.setAddress(Address.getText().trim());
        employeeData.setSupervisor(ImmSup.getText().trim());
        employeeData.setPosition(Position.getText().trim());
        employeeData.setStatus(Status.getText().trim());
        
        // 2. Update Government IDs
        employeeData.setSssNumber(SSS.getText().trim());
        employeeData.setPhilHealthNumber(PHILHEALTH.getText().trim());
        employeeData.setTinNumber(TIN.getText().trim());
        employeeData.setPagIbigNumber(PAGIBIG.getText().trim());

        // 3. Use parseDoubleSafe to handle currency/empty fields
        employeeData.setBasicSalary(EmployeeFileHandler.parseDoubleSafe(Salary.getText(), employeeData.getBasicSalary()));
        employeeData.setHourlyRate(EmployeeFileHandler.parseDoubleSafe(Hourly.getText(), employeeData.getHourlyRate()));
        employeeData.setRiceSubsidy(EmployeeFileHandler.parseDoubleSafe(Rice.getText(), employeeData.getRiceSubsidy()));
        employeeData.setPhoneAllowance(EmployeeFileHandler.parseDoubleSafe(PhoneAll.getText(), employeeData.getPhoneAllowance()));
        employeeData.setClothingAllowance(EmployeeFileHandler.parseDoubleSafe(ClothAll.getText(), employeeData.getClothingAllowance()));

        // 4. Save to CSV via Handler
        EmployeeFileHandler.updateEmployee(employeeData);
        
        // 5. UI Feedback and Refresh
        JOptionPane.showMessageDialog(this, "Employee record updated successfully!");
        
        if (EmployeeTable.getInstance() != null) {
            EmployeeTable.getInstance().refreshEmployeeTable();
        }

        setFieldsEditable(false);
        dispose();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error saving updates: " + e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
    }
}

/**
 * Helper method to safely parse double values, preserving the existing value if input is invalid or empty.
 */
private double parseDouble(String value, double currentValue) {
    if (value == null || value.trim().isEmpty()) {
        return currentValue; // Added return for empty/null case
    }
    try {
        // Remove currency symbols and commas before parsing
        String cleanValue = value.trim().replaceAll("[₱,]", "");
        return Double.parseDouble(cleanValue);
    } catch (NumberFormatException e) {
        System.err.println("ERROR: Invalid number format: " + value);
        return currentValue; // Return existing value if parsing fails
    }

    }//GEN-LAST:event_SaveActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        dispose ();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void SalaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SalaryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SalaryActionPerformed

    private void ImmSupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImmSupActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ImmSupActionPerformed

    private void PhoneNumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PhoneNumActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PhoneNumActionPerformed

    private void BirthdayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BirthdayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BirthdayActionPerformed

    private void AddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AddressActionPerformed

    private void NameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NameActionPerformed

    private void PositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PositionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PositionActionPerformed

    private void StatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_StatusActionPerformed

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
            java.util.logging.Logger.getLogger(EditEmpInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditEmpInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditEmpInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditEmpInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                String selectedEmpNum = null;
          
                new EditEmpInfo(Integer.parseInt(selectedEmpNum), true).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Address;
    private javax.swing.JTextField Birthday;
    private javax.swing.JFormattedTextField ClothAll;
    private javax.swing.JButton Edit;
    private javax.swing.JFormattedTextField Hourly;
    private javax.swing.JTextField ImmSup;
    private javax.swing.JTextField Name;
    private javax.swing.JTextField PAGIBIG;
    private javax.swing.JLabel PAGIBIGname;
    private javax.swing.JTextField PHILHEALTH;
    private javax.swing.JLabel PHILHEALTHname;
    private javax.swing.JFormattedTextField PhoneAll;
    private javax.swing.JTextField PhoneNum;
    private javax.swing.JTextField Position;
    private javax.swing.JFormattedTextField Rice;
    private javax.swing.JFormattedTextField SSS;
    private javax.swing.JLabel SSSname;
    private javax.swing.JFormattedTextField Salary;
    private javax.swing.JButton Save;
    private javax.swing.JTextField Status;
    private javax.swing.JTextField TIN;
    private javax.swing.JLabel TINname;
    private javax.swing.JLabel headerLogo;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel textLogo;
    private javax.swing.JTextField txtLname;
    // End of variables declaration//GEN-END:variables
}
