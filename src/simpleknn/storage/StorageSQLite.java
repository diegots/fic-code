package simpleknn.storage;

import java.io.File;
import java.sql.*;
import java.util.List;

public class StorageSQLite implements Storage {

    private String dbEndpoint;
    private String connectionString;

    public StorageSQLite(String dbConnectionString, String dbEndpoint) {
        this.dbEndpoint = dbEndpoint;
        connectionString = dbConnectionString + dbEndpoint;
    }

    @Override
    public void storeUsers(List<Integer> usersList) {

        // TODO this storing operation should only be done the first time the app is run. Where the db is empty

        String queryString = "";

        try (Connection connection = DriverManager.getConnection(connectionString)) {
            Statement stmt = connection.createStatement();
            stmt.setQueryTimeout(30);

            for (Integer userId: usersList) {
                queryString = "INSERT INTO users VALUES (" + userId + ")";
                stmt.execute(queryString);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeSimilarity(int userA, int userU, double similarity) {

        String queryString = "INSERT INTO u_similarity (userA, userU, similarity) " +
                "VALUES (" + userA + ", " + userU + ", " + similarity + ")";

        try (Connection connection = DriverManager.getConnection(connectionString)) {
            Statement stmt = connection.createStatement();
            stmt.setQueryTimeout(30);
            stmt.execute(queryString);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Double getSimilarity(int userA, int userU) {

        String queryString = "SELECT similarity FROM u_similarity WHERE userA = " +
                userA + " AND userU = " + userU;

        try (Connection connection = DriverManager.getConnection(connectionString)) {
            Statement stmt = connection.createStatement();
            stmt.setQueryTimeout(30);
            ResultSet resultSet = stmt.executeQuery(queryString);

            if (! resultSet.isClosed()) {
                Double d = resultSet.getDouble("similarity");
//                System.out.println("similarity in db: " + d);
                return d;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1.0;
    }

    @Override
    public void createTables() {

        // TODO check what table exists and create it. Avoid using table number count

        String tablesExists = "SELECT count(*) as 'num_tables' FROM sqlite_master " +
                "WHERE type='table' and (name='users' or name='u_similarity');')";

        final String createTableU = "CREATE TABLE 'users' (" +
                "'userId' INTEGER NOT NULL UNIQUE," +
                "PRIMARY KEY(userId))";

        final String createTableUSimilarity = "CREATE TABLE 'u_similarity' (" +
                "'similarityId' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "'userA' INTEGER NOT NULL," +
                "'userU' INTEGER NOT NULL," +
                "'similarity' REAL NOT NULL," +
                "FOREIGN KEY('userU') REFERENCES users ( userId )," +
                "FOREIGN KEY('userA') REFERENCES users ( userId ))";

        try (Connection connection = DriverManager.getConnection(connectionString)) {
            Statement stmt = connection.createStatement();
            stmt.setQueryTimeout(30);
            ResultSet resultSet = stmt.executeQuery(tablesExists);

            if (resultSet.getInt("num_tables") <= 0) {
                System.out.println("-> No tables found. Creating 'users' and 'u_similarity'");
                stmt.execute(createTableU);
                stmt.execute(createTableUSimilarity );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
