package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConfig {
    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASSWORD;
    
    static {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(".env"));
            DB_URL = props.getProperty("DB_URL", "jdbc:mysql://localhost:3306/be_submissionmember_soal2_davingabrieljonathan");
            DB_USER = props.getProperty("DB_USER", "root");
            DB_PASSWORD = props.getProperty("DB_PASSWORD", "");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }
}
