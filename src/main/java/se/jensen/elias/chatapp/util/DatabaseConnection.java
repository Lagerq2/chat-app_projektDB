package se.jensen.elias.chatapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    String url;
    String user;
    String password;


    private DatabaseConnection() throws SQLException {
        loadProperties();

        this.connection = DriverManager.getConnection(url, user, password);
    }
    private void loadProperties() {
        try(InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")){
            Properties prop = new Properties();
            if(input == null){
                throw new RuntimeException("db.properties hittades inte i resources!");
            }
            prop.load(input);
            this.url = prop.getProperty("url");
            this.user = prop.getProperty("user");
            this.password = prop.getProperty("password");
        }catch (IOException e){
            throw new RuntimeException("db.properties hittades inte i resources!" + e.getMessage());
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

}
