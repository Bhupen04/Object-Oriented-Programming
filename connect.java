package test1;
import java.sql.*;

public class connect {
    private static final String JDBC_URL = "jdbc:mysql://localhost";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final String DATABASE_NAME = "Coursemanage";

    public Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            connectToDatabase(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void connectToDatabase(Connection connection) throws SQLException {
        String useDatabaseSQL = "USE " + DATABASE_NAME;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(useDatabaseSQL);
        }
    }
}
