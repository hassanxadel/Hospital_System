import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 * Helper class to replace net.proteanit.sql.DbUtils functionality
 * This provides the same resultSetToTableModel method used throughout the project
 */
public class DbUtilsHelper {
    
    /**
     * Converts a ResultSet to a DefaultTableModel for use with JTable
     * @param rs The ResultSet to convert
     * @return DefaultTableModel containing the data from the ResultSet
     * @throws SQLException if there's an error reading the ResultSet
     */
    public static DefaultTableModel resultSetToTableModel(ResultSet rs) throws SQLException {
        java.sql.ResultSetMetaData metaData = rs.getMetaData();
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        
        // Get column names
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }
        
        // Get data from ResultSet
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }
        
        return new DefaultTableModel(data, columnNames);
    }
} 