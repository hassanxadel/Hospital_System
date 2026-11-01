
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/*
 * Modern Hospital Management System - Doctor Entry Form
 * Improved UI/UX Design with Material Design principles
 */
/**
 *
 * @author Agarwal - Enhanced by AI Assistant
 */
public class Entry extends javax.swing.JFrame {
    Connection con=null;
    ResultSet rs=null;
    PreparedStatement pst=null;

    // Modern Color Scheme
    private final Color PRIMARY_COLOR = new Color(33, 150, 243);    // Material Blue
    private final Color SECONDARY_COLOR = new Color(76, 175, 80);   // Material Green
    private final Color BACKGROUND_COLOR = new Color(250, 250, 250); // Light Gray
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = new Color(66, 66, 66);
    private final Color ACCENT_COLOR = new Color(255, 193, 7);      // Amber

    /**
     * Creates new form Entry with modern design
     */
    public Entry() {
        initComponents();
        customizeGUI();
        
        // Test database connection on startup
        testDatabaseConnection();
        
        // Set location to center
        setLocationRelativeTo(null);
        
        // Set form properties
        setTitle("🏥 Hospital Management System - Doctor Registration");
        setResizable(false);
    }
    
    /**
     * Apply modern styling to the form
     */
    private void customizeGUI() {
        // Set overall background
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Style the main panel
        jPanel1.setBackground(CARD_COLOR);
        jPanel1.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Style the button panel
        jPanel2.setBackground(CARD_COLOR);
        jPanel2.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Style labels
        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);
        Color labelColor = TEXT_COLOR;
        
        jLabel1.setFont(labelFont); jLabel1.setForeground(labelColor);
        jLabel2.setFont(labelFont); jLabel2.setForeground(labelColor);
        jLabel3.setFont(labelFont); jLabel3.setForeground(labelColor);
        jLabel4.setFont(labelFont); jLabel4.setForeground(labelColor);
        jLabel5.setFont(labelFont); jLabel5.setForeground(labelColor);
        jLabel6.setFont(labelFont); jLabel6.setForeground(labelColor);
        jLabel7.setFont(labelFont); jLabel7.setForeground(labelColor);
        jLabel9.setFont(labelFont); jLabel9.setForeground(labelColor);
        jLabel10.setFont(labelFont); jLabel10.setForeground(labelColor);
        jLabel11.setFont(labelFont); jLabel11.setForeground(labelColor);
        
        // Style text fields
        Font textFont = new Font("Segoe UI", Font.PLAIN, 11);
        JTextField[] textFields = {txtId, txtName, txtFname, txtE, txtC, txtAd, txtQ, txtD};
        
        for (JTextField field : textFields) {
            field.setFont(textFont);
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
            field.setBackground(Color.WHITE);
        }
        
        // Style combo boxes
        cmbG.setFont(textFont);
        cmbB.setFont(textFont);
        
        // Style buttons with modern design
        styleButton(btnSave, SECONDARY_COLOR, "💾 Save");
        styleButton(btnUpdate, ACCENT_COLOR, "📝 Update");
        styleButton(btnDelete, new Color(244, 67, 54), "🗑️ Delete");
        styleButton(btnNew, PRIMARY_COLOR, "➕ New");
        styleButton(btnGet, new Color(156, 39, 176), "📋 Get Data");
        
        // Initially disable update and delete buttons
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
    
    /**
     * Style individual buttons with modern look
     */
    private void styleButton(JButton button, Color bgColor, String text) {
        button.setText(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
    
    /**
     * Test database connection and show user-friendly message
     */
    private void testDatabaseConnection() {
        try {
            con = Connect.ConnectDB();
            if (con != null) {
                // Show success message briefly
                JOptionPane.showMessageDialog(this, 
                    "✅ Database connected successfully!\nReady to manage doctor records.", 
                    "Connection Status", 
                    JOptionPane.INFORMATION_MESSAGE);
                con.close(); // Close test connection
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Database connection failed!\n\n" + 
                "Error: " + e.getMessage() + "\n\n" +
                "Please check:\n" +
                "• MySQL server is running\n" +
                "• Database 'hospital_management' exists\n" +
                "• Username/password are correct", 
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reset()
    {
        txtId.setText("");
        txtName.setText("");
        txtFname.setText("");
        txtC.setText("");
        txtAd.setText("");
        txtQ.setText("");
        txtE.setText("");
        txtD.setText("");
        cmbB.setSelectedIndex(-1);
        cmbG.setSelectedIndex(-1);
        btnSave.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        txtFname = new javax.swing.JTextField();
        txtE = new javax.swing.JTextField();
        txtC = new javax.swing.JTextField();
        txtAd = new javax.swing.JTextField();
        txtQ = new javax.swing.JTextField();
        txtD = new javax.swing.JTextField();
        cmbG = new javax.swing.JComboBox();
        cmbB = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        btnGet = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Doctor Details"));

        jLabel1.setText("Doctor ID");

        jLabel2.setText("Full Name");

        jLabel3.setText("Father's Name");

        jLabel4.setText("Email-id");

        jLabel5.setText("Contact no");

        jLabel6.setText("Address");

        jLabel7.setText("Qualifications");

        jLabel9.setText("Gender");

        jLabel10.setText("Blood Group");

        jLabel11.setText("Date Of Joining");

        txtD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDActionPerformed(evt);
            }
        });

        cmbG.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "M", "F" }));

        cmbB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-" }));

        jLabel8.setText("YYYY-MM-DD");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                        .addComponent(txtD, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtQ, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                            .addComponent(txtAd)
                            .addComponent(txtC)
                            .addComponent(txtE)
                            .addComponent(txtFname)
                            .addComponent(txtName)
                            .addComponent(txtId)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addContainerGap(63, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtFname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(txtAd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(txtQ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel9))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(cmbG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(cmbB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnUpdate.setText("Update");
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setText("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnNew.setText("New");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        btnGet.setText("Get Data");
        btnGet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnNew, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGet, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
                .addContainerGap(86, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUpdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDelete)
                .addGap(18, 18, 18)
                .addComponent(btnNew)
                .addGap(18, 18, 18)
                .addComponent(btnGet)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        try{
            int P = JOptionPane.showConfirmDialog(null," Are you sure want to delete ?","Confirmation",JOptionPane.YES_NO_OPTION);
            if (P==0)
            {
                con=Connect.ConnectDB();
                String sql= "delete from Doctor where DoctorID = '" + txtId.getText() + "'";
                pst=con.prepareStatement(sql);
                pst.execute();
                JOptionPane.showMessageDialog(this,"Successfully deleted","Record",JOptionPane.INFORMATION_MESSAGE);

                reset();
            }
        }catch(HeadlessException | SQLException ex){
            JOptionPane.showMessageDialog(this,ex);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
      reset();  // TODO add your handling code here:
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        try{
            // Validate input fields with modern UI feedback
            if (!validateInputs()) {
                return;
            }
            
            con = Connect.ConnectDB();
            if (con == null) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Database connection failed!\nPlease check your connection settings.", 
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if Doctor ID already exists using prepared statement
            String checkSql = "SELECT DoctorID FROM Doctor WHERE DoctorID = ?";
            pst = con.prepareStatement(checkSql);
            pst.setString(1, txtId.getText().trim());
            rs = pst.executeQuery();
            
            if(rs.next()){
                JOptionPane.showMessageDialog(this, 
                    "⚠️ Doctor ID '" + txtId.getText() + "' already exists!\nPlease use a different ID.", 
                    "Duplicate ID Error", 
                    JOptionPane.WARNING_MESSAGE);
                txtId.setText("");
                txtId.requestFocus();
                return;
            }
            
            // Insert new doctor record using prepared statement (SQL injection safe)
            String sql = "INSERT INTO Doctor(DoctorID, Doctorname, FatherName, Email, ContacNo, Qualifications, Gender, BloodGroup, DateOfJoining, Address) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, txtId.getText().trim());
            pst.setString(2, txtName.getText().trim());
            pst.setString(3, txtFname.getText().trim());
            pst.setString(4, txtE.getText().trim());
            pst.setString(5, txtC.getText().trim());
            pst.setString(6, txtQ.getText().trim());
            pst.setString(7, cmbG.getSelectedItem().toString());
            pst.setString(8, cmbB.getSelectedItem().toString());
            pst.setString(9, txtD.getText().trim());
            pst.setString(10, txtAd.getText().trim());
            
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this,
                "✅ Doctor record saved successfully!\n\n" +
                "Doctor ID: " + txtId.getText() + "\n" +
                "Name: " + txtName.getText(),
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            btnSave.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);

        }catch(HeadlessException | SQLException ex){
            JOptionPane.showMessageDialog(this,
                "❌ Error saving doctor record!\n\n" + ex.getMessage(),
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnSaveActionPerformed
    
    /**
     * Validate all input fields with modern UI feedback
     */
    private boolean validateInputs() {
        // Check required fields
        if (txtId.getText().trim().isEmpty()) {
            showValidationError("Please enter Doctor ID", txtId);
            return false;
        }
        if (txtName.getText().trim().isEmpty()) {
            showValidationError("Please enter Doctor's full name", txtName);
            return false;
        }
        if (txtFname.getText().trim().isEmpty()) {
            showValidationError("Please enter Father's name", txtFname);
            return false;
        }
        if (txtAd.getText().trim().isEmpty()) {
            showValidationError("Please enter address", txtAd);
            return false;
        }
        if (txtC.getText().trim().isEmpty()) {
            showValidationError("Please enter contact number", txtC);
            return false;
        }
        if (txtQ.getText().trim().isEmpty()) {
            showValidationError("Please enter qualifications", txtQ);
            return false;
        }
        if (cmbG.getSelectedIndex() == -1) {
            showValidationError("Please select gender", null);
            cmbG.requestFocus();
            return false;
        }
        if (cmbB.getSelectedIndex() == -1) {
            showValidationError("Please select blood group", null);
            cmbB.requestFocus();
            return false;
        }
        if (txtD.getText().trim().isEmpty()) {
            showValidationError("Please enter joining date (YYYY-MM-DD)", txtD);
            return false;
        }
        
        // Validate email format if provided
        if (!txtE.getText().trim().isEmpty() && !isValidEmail(txtE.getText().trim())) {
            showValidationError("Please enter a valid email address", txtE);
            return false;
        }
        
        // Validate contact number
        if (!isValidContactNumber(txtC.getText().trim())) {
            showValidationError("Please enter a valid contact number (10 digits)", txtC);
            return false;
        }
        
        // Validate date format
        if (!isValidDate(txtD.getText().trim())) {
            showValidationError("Please enter date in YYYY-MM-DD format", txtD);
            return false;
        }
        
        return true;
    }
    
    private void showValidationError(String message, JTextField field) {
        JOptionPane.showMessageDialog(this, "⚠️ " + message, "Validation Error", JOptionPane.WARNING_MESSAGE);
        if (field != null) {
            field.requestFocus();
            field.selectAll();
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    private boolean isValidContactNumber(String contact) {
        return contact.matches("\\d{10}");
    }
    
    private boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        try{
            con=Connect.ConnectDB();
            String sql= "update Doctor set Doctorname='" + txtName.getText() + "',FatherName='" + txtFname.getText() + "',Email='" + txtE.getText() + "',ContacNo='"+ txtC.getText() + "',Qualifications='"+ txtQ.getText() +  "',Gender='" + cmbG.getSelectedItem() + "',BloodGroup='"+ cmbB.getSelectedItem() + "',DateOfJoining='" + txtD.getText() + "',Address='" + txtAd.getText() + "' where DoctorID='" + txtId.getText() + "'";
            pst=con.prepareStatement(sql);
            pst.execute();
            JOptionPane.showMessageDialog(this,"Successfully updated");
            btnUpdate.setEnabled(false);
            }catch(HeadlessException | SQLException ex){
            JOptionPane.showMessageDialog(this,ex);
            }        // TODO add your handling code here:
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnGetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetActionPerformed
     this.hide();
     DocRec frm=new DocRec();
     frm.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_btnGetActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
         // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosing

    private void txtDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDActionPerformed

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
            java.util.logging.Logger.getLogger(Entry.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Entry.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Entry.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Entry.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Entry().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnDelete;
    public javax.swing.JButton btnGet;
    public javax.swing.JButton btnNew;
    public javax.swing.JButton btnSave;
    public javax.swing.JButton btnUpdate;
    public javax.swing.JComboBox cmbB;
    public javax.swing.JComboBox cmbG;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    public javax.swing.JTextField txtAd;
    public javax.swing.JTextField txtC;
    public javax.swing.JTextField txtD;
    public javax.swing.JTextField txtE;
    public javax.swing.JTextField txtFname;
    public javax.swing.JTextField txtId;
    public javax.swing.JTextField txtName;
    public javax.swing.JTextField txtQ;
    // End of variables declaration//GEN-END:variables
}
