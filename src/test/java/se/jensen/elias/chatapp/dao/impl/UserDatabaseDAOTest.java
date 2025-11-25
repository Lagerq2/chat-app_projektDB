package se.jensen.elias.chatapp.dao.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.jensen.elias.chatapp.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserDatabaseDAOTest {
    private static Connection conn;
    private static UserDatabaseDAO userDAO;

    //körs en gång innan alla tester
    @BeforeAll
    static void setUpDatabase() throws Exception {
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");

        //Skapar min test databas
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) NOT NULL,
                        password VARCHAR(50) NOT NULL
                                       )
                    """);
            stmt.execute("""
                    CREATE TABLE messages (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        text VARCHAR(255),
                        timestamp TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id)
                                       )
                    """);
        }
    }

    //körs innan varje test
    @BeforeEach
    void setUpUserDAO() {
        userDAO = new UserDatabaseDAO(conn);
    }

    //körs efter testet
    @AfterEach
    void cleanup() throws Exception {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM messages");
            stmt.execute("DELETE FROM users");
        }
    }

    @Test
    void testLoginSuccessful() throws Exception {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO users (username, password) VALUES ('testname', 'testpassword')");
            stmt.execute("INSERT INTO messages (user_id, text, timestamp) VALUES (1, 'testmessage1' ,' " + Timestamp.valueOf(LocalDateTime.now()) + "')");
            stmt.execute("INSERT INTO messages (user_id, text, timestamp) VALUES (1, 'testmessage2' ,' " + Timestamp.valueOf(LocalDateTime.now()) + "')");
        }
        User user = userDAO.login("testname", "testpassword");

        assertNotNull(user, "User ska inte vara null");
        assertEquals("testname", user.getUsername());
        assertEquals("testpassword", user.getPassword());

        assertEquals(2, user.getMessages().size(), "Antal meddelanden ska vara 2");
    }
}