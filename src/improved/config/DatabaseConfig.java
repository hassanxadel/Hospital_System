package improved.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Secure database configuration and connection management
 * Implements basic connection pooling and secure credential handling
 * 
 * @author Hospital Management System - Improved Version
 */
public class DatabaseConfig {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static DatabaseConfig instance;
    private Properties dbProperties;
    private ConcurrentLinkedQueue<Connection> connectionPool;
    private AtomicInteger activeConnections;
    private final int maxPoolSize = 20;
    private final int minPoolSize = 5;
    
    // Private constructor for singleton pattern
    private DatabaseConfig() {
        initializeConnectionPool();
    }
    
    /**
     * Get singleton instance of DatabaseConfig
     * @return DatabaseConfig instance
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    /**
     * Initialize the connection pool
     */
    private void initializeConnectionPool() {
        try {
            loadDatabaseProperties();
            connectionPool = new ConcurrentLinkedQueue<>();
            activeConnections = new AtomicInteger(0);
            
            // Pre-create minimum number of connections
            for (int i = 0; i < minPoolSize; i++) {
                Connection conn = createNewConnection();
                if (conn != null) {
                    connectionPool.offer(conn);
                }
            }
            
            LOGGER.info("Database connection pool initialized successfully with " + connectionPool.size() + " connections");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database connection pool", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    /**
     * Create a new database connection
     * @return new Connection
     * @throws SQLException if connection fails
     */
    private Connection createNewConnection() throws SQLException {
        String url = dbProperties.getProperty("db.url", "jdbc:mysql://localhost:3306/hospital_management_improved");
        String username = dbProperties.getProperty("db.username", "root");
        String password = dbProperties.getProperty("db.password", "");
        
        try {
            Class.forName(dbProperties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }
    
    /**
     * Load database properties from configuration file
     */
    private void loadDatabaseProperties() {
        dbProperties = new Properties();
        
        // Try to load from external config file first
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                dbProperties.load(input);
                LOGGER.info("Database properties loaded from database.properties");
            } else {
                LOGGER.warning("database.properties not found, using default values");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error loading database properties", e);
        }
        
        // Override with system properties if available (for production deployment)
        dbProperties.putAll(System.getProperties());
    }
    
    /**
     * Get a connection from the pool
     * @return Database connection
     * @throws SQLException if connection cannot be obtained
     */
    public Connection getConnection() throws SQLException {
        if (connectionPool == null) {
            throw new SQLException("Connection pool not initialized");
        }
        
        Connection conn = connectionPool.poll();
        if (conn == null || conn.isClosed()) {
            // Create new connection if pool is empty or connection is closed
            if (activeConnections.get() < maxPoolSize) {
                conn = createNewConnection();
                activeConnections.incrementAndGet();
            } else {
                throw new SQLException("Maximum connection pool size reached");
            }
        }
        
        return conn;
    }
    
    /**
     * Return a connection to the pool
     * @param conn Connection to return
     */
    public void returnConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed() && connectionPool.size() < maxPoolSize) {
                    connectionPool.offer(conn);
                } else {
                    conn.close();
                    activeConnections.decrementAndGet();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error returning connection to pool", e);
            }
        }
    }
    
    /**
     * Close all connections in the pool
     */
    public void close() {
        if (connectionPool != null) {
            while (!connectionPool.isEmpty()) {
                Connection conn = connectionPool.poll();
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing connection", e);
                }
            }
            LOGGER.info("Database connection pool closed");
        }
    }
    
    /**
     * Test database connectivity
     * @return true if connection is successful
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database connection test failed", e);
            return false;
        }
    }
    
    /**
     * Get pool statistics for monitoring
     * @return Pool statistics as formatted string
     */
    public String getPoolStats() {
        if (connectionPool != null) {
            return String.format(
                "Pool Stats - Available: %d, Active: %d, Max Size: %d",
                connectionPool.size(),
                activeConnections.get(),
                maxPoolSize
            );
        }
        return "Pool not initialized";
    }
} 