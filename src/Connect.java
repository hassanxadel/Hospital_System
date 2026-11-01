
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Agarwal
 */
public class Connect {
    Connection con=null;
   
        public static Connection ConnectDB(){
        try{
           // Updated MySQL driver for newer versions
           Class.forName("com.mysql.cj.jdbc.Driver");
           // Fixed connection string with SSL and authentication parameters
           String url = "jdbc:mysql://localhost:3306/hospital_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
           Connection con = DriverManager.getConnection(url, "root", "");
           return con;
        }catch(ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "MySQL Driver not found: " + e.getMessage());
            return null;
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage() + 
                "\n\nPlease ensure:\n1. MySQL server is running\n2. Database 'hospital_management' exists\n3. Username/password are correct");
            return null;
        }      
    }
}
