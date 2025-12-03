package com.thereadingroom.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseManager {

    private static final String URL = "jdbc:sqlite:booklovers.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void createTables() {

        System.out.println(">>> createTables() called");

        String usersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                email TEXT,
                role TEXT NOT NULL
            );
        """;

        String booksTable = """
            CREATE TABLE IF NOT EXISTS books (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                genre TEXT
            );
        """;

        String userBooksTable = """
            CREATE TABLE IF NOT EXISTS user_books (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                book_id INTEGER NOT NULL,
                list_type TEXT NOT NULL,
                rating INTEGER,
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (book_id) REFERENCES books(id)
            );
        """;

        String discussionsTable = """
            CREATE TABLE IF NOT EXISTS discussions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                book_id INTEGER NOT NULL,
                title TEXT NOT NULL,
                FOREIGN KEY (book_id) REFERENCES books(id)
            );
        """;

        String postsTable = """
            CREATE TABLE IF NOT EXISTS posts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                discussion_id INTEGER NOT NULL,
                user_id INTEGER NOT NULL,
                content TEXT NOT NULL,
                created_at TEXT,
                FOREIGN KEY (discussion_id) REFERENCES discussions(id),
                FOREIGN KEY (user_id) REFERENCES users(id)
            );
        """;

        String insertAdmin = """
            INSERT OR IGNORE INTO users (id, username, password, email, role)
            VALUES (1, 'admin', 'admin123', 'admin@mail.com', 'admin');
        """;

        try (Connection conn = getConnection();
             java.sql.Statement stmt = conn.createStatement()) {

            stmt.execute(usersTable);
            stmt.execute(booksTable);
            stmt.execute(userBooksTable);
            stmt.execute(discussionsTable);
            stmt.execute(postsTable);

            // Insert default admin
            stmt.execute(insertAdmin);

            System.out.println("All tables created (or already exist). Admin ready.");

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Connecting to database...");

        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Connected to SQLite successfully!");
                createTables();
            } else {
                System.out.println("Connection returned null.");
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}
