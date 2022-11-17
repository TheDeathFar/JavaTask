package ru.vsu.css.vorobcov_i_a.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {

    private static final String url = "jdbc:postgresql://localhost:5432/players";
    private static final String user = "postgres";
    private static final String password = "12345";
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("The connection to the DB has been completed.");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Error!");
        }
        return connection;
    }
}

