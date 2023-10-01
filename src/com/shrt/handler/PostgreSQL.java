package src.com.shrt.handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class PostgreSQL {
    private Connection conn;

    public PostgreSQL(String jdbcUrl, String username, String password) throws Exception {
        Class.forName("org.postgresql.Driver");
        this.conn = DriverManager.getConnection(jdbcUrl, username, password);
    }

    public boolean insertData(String url, String shrt) throws SQLException {
        String insertQuery = "INSERT INTO links (url, short) VALUES (?, ?)"; // Replace with your table name and
                                                                                 // column names
        PreparedStatement preparedStatement = this.conn.prepareStatement(insertQuery);
        preparedStatement.setString(1, url);
        preparedStatement.setString(2, shrt);

        int rowsAffected = preparedStatement.executeUpdate();

        return rowsAffected > 0;
    }

    public String selectShrt(String url) throws SQLException {
        String select = "SELECT * FROM links WHERE url=?";
        PreparedStatement preparedStatement = this.conn.prepareStatement(select);
        preparedStatement.setString(1, url);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()) {
            String shrt = this.insertUntilShrtIsValid(url);

            return shrt;
        }

        return resultSet.getString("short");
    }

    public String selectUrl(String shrt) throws SQLException {
        String select = "SELECT * FROM links WHERE short=?";
        PreparedStatement preparedStatement = this.conn.prepareStatement(select);
        preparedStatement.setString(1, shrt);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()) {
            throw new SQLException("no rows");
        }

        return resultSet.getString("url");
    }

    private String generateShort() {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    private String insertUntilShrtIsValid(String url) throws SQLException {
        // throw new SQLException("no rows");
        boolean inserted = false;
        String shrt = "";

        for (int i = 0; !inserted; i++) {
            shrt = this.generateShort();
            inserted = this.insertData(url, shrt);

            if (i > 100) {
                break;
            }
        }

        if (shrt == "") {
            throw new SQLException("shrt was never assigned");
        }

        return shrt;
    }
}
