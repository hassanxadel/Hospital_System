package improved.dao;

import improved.config.DatabaseConfig;
import improved.security.SecurityUtils;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.util.ArrayList;

/**
 * Base Data Access Object providing common database operations
 * Implements security measures to prevent SQL injection attacks
 * 
 * @author Hospital Management System - Improved Version
 */
public abstract class BaseDAO {
    
    protected static final Logger LOGGER = Logger.getLogger(BaseDAO.class.getName());
    protected final DatabaseConfig dbConfig;
    
    public BaseDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Execute a prepared statement query and return ResultSet
     * @param sql SQL query with placeholders
     * @param parameters Parameters for the query
     * @return ResultSet
     * @throws SQLException if query execution fails
     */
    protected ResultSet executeQuery(String sql, Object... parameters) throws SQLException {
        Connection conn = dbConfig.getConnection();
        PreparedStatement stmt = prepareStatement(conn, sql, parameters);
        return stmt.executeQuery();
    }
    
    /**
     * Execute a prepared statement update (INSERT, UPDATE, DELETE)
     * @param sql SQL statement with placeholders
     * @param parameters Parameters for the statement
     * @return Number of affected rows
     * @throws SQLException if execution fails
     */
    protected int executeUpdate(String sql, Object... parameters) throws SQLException {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, parameters)) {
            
            int result = stmt.executeUpdate();
            LOGGER.info("Executed update query: " + sql + " (affected rows: " + result + ")");
            return result;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing update: " + sql, e);
            throw e;
        }
    }
    
    /**
     * Execute a prepared statement and return generated keys
     * @param sql SQL INSERT statement with placeholders
     * @param parameters Parameters for the statement
     * @return Generated key (usually auto-increment ID)
     * @throws SQLException if execution fails
     */
    protected long executeInsertAndGetKey(String sql, Object... parameters) throws SQLException {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setParameters(stmt, parameters);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long key = generatedKeys.getLong(1);
                    LOGGER.info("Executed insert query: " + sql + " (generated key: " + key + ")");
                    return key;
                } else {
                    throw new SQLException("Insert failed, no generated key obtained");
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing insert: " + sql, e);
            throw e;
        }
    }
    
    /**
     * Check if a record exists based on a condition
     * @param tableName Table name
     * @param whereClause WHERE clause with placeholders
     * @param parameters Parameters for the WHERE clause
     * @return true if record exists
     * @throws SQLException if query fails
     */
    protected boolean recordExists(String tableName, String whereClause, Object... parameters) throws SQLException {
        String sql = "SELECT 1 FROM " + tableName + " WHERE " + whereClause + " LIMIT 1";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, parameters);
             ResultSet rs = stmt.executeQuery()) {
            
            return rs.next();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking record existence: " + sql, e);
            throw e;
        }
    }
    
    /**
     * Get a single value from the database
     * @param sql SQL query returning a single value
     * @param parameters Parameters for the query
     * @return The value as Object, or null if not found
     * @throws SQLException if query fails
     */
    protected Object getSingleValue(String sql, Object... parameters) throws SQLException {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, parameters);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getObject(1);
            }
            return null;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting single value: " + sql, e);
            throw e;
        }
    }
    
    /**
     * Execute a batch of prepared statements
     * @param sql SQL statement with placeholders
     * @param batchParameters List of parameter arrays for each execution
     * @return Array of update counts
     * @throws SQLException if batch execution fails
     */
    protected int[] executeBatch(String sql, List<Object[]> batchParameters) throws SQLException {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (Object[] parameters : batchParameters) {
                setParameters(stmt, parameters);
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            conn.commit();
            
            LOGGER.info("Executed batch query: " + sql + " (batch size: " + batchParameters.size() + ")");
            return results;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing batch: " + sql, e);
            throw e;
        }
    }
    
    /**
     * Execute a transaction with multiple statements
     * @param operations List of database operations to execute in transaction
     * @throws SQLException if transaction fails
     */
    protected void executeTransaction(List<DatabaseOperation> operations) throws SQLException {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);
            
            for (DatabaseOperation operation : operations) {
                operation.execute(conn);
            }
            
            conn.commit();
            LOGGER.info("Transaction completed successfully with " + operations.size() + " operations");
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    LOGGER.warning("Transaction rolled back due to error");
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Error during rollback", rollbackEx);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    dbConfig.returnConnection(conn);
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error returning connection to pool", e);
                }
            }
        }
    }
    
    /**
     * Prepare a statement with parameters
     * @param conn Database connection
     * @param sql SQL statement with placeholders
     * @param parameters Parameters to set
     * @return Prepared statement
     * @throws SQLException if preparation fails
     */
    private PreparedStatement prepareStatement(Connection conn, String sql, Object... parameters) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        setParameters(stmt, parameters);
        return stmt;
    }
    
    /**
     * Set parameters on a prepared statement
     * @param stmt Prepared statement
     * @param parameters Parameters to set
     * @throws SQLException if parameter setting fails
     */
    private void setParameters(PreparedStatement stmt, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            Object param = parameters[i];
            
            // Sanitize string parameters to prevent SQL injection
            if (param instanceof String) {
                param = SecurityUtils.sanitizeInput((String) param);
            }
            
            if (param == null) {
                stmt.setNull(i + 1, Types.NULL);
            } else if (param instanceof String) {
                stmt.setString(i + 1, (String) param);
            } else if (param instanceof Integer) {
                stmt.setInt(i + 1, (Integer) param);
            } else if (param instanceof Long) {
                stmt.setLong(i + 1, (Long) param);
            } else if (param instanceof Double) {
                stmt.setDouble(i + 1, (Double) param);
            } else if (param instanceof Boolean) {
                stmt.setBoolean(i + 1, (Boolean) param);
            } else if (param instanceof Date) {
                stmt.setDate(i + 1, (Date) param);
            } else if (param instanceof Timestamp) {
                stmt.setTimestamp(i + 1, (Timestamp) param);
            } else {
                stmt.setObject(i + 1, param);
            }
        }
    }
    
    /**
     * Close database resources safely
     * @param resources Database resources to close
     */
    protected void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error closing database resource", e);
                }
            }
        }
    }
    
    /**
     * Interface for database operations in transactions
     */
    @FunctionalInterface
    protected interface DatabaseOperation {
        void execute(Connection conn) throws SQLException;
    }
    
    /**
     * Convert SQLException to a more user-friendly message
     * @param e SQLException
     * @return User-friendly error message
     */
    protected String getUserFriendlyErrorMessage(SQLException e) {
        String sqlState = e.getSQLState();
        int errorCode = e.getErrorCode();
        
        // Common MySQL error codes
        if (errorCode == 1062) {
            return "A record with this information already exists";
        } else if (errorCode == 1452) {
            return "Referenced record does not exist";
        } else if (errorCode == 1451) {
            return "Cannot delete record because it is referenced by other records";
        } else if (sqlState != null && sqlState.startsWith("23")) {
            return "Data integrity constraint violated";
        } else if (sqlState != null && sqlState.startsWith("08")) {
            return "Database connection error";
        } else {
            return "Database operation failed. Please try again";
        }
    }
} 