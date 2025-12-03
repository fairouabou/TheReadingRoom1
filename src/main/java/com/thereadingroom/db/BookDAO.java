package com.thereadingroom.db;

import com.thereadingroom.model.Book;
import java.sql.*;
import java.util.*;

public class BookDAO {

    public static class BookRowDTO {
        public int userBookId;
        public String title;
        public String author;
        public String listType;
        public Integer rating;

        public BookRowDTO(int userBookId, String title, String author, String listType, Integer rating) {
            this.userBookId = userBookId;
            this.title = title;
            this.author = author;
            this.listType = listType;
            this.rating = rating;
        }

        public BookRowDTO(int userBookId, String title, String author, String listType) {
            this.userBookId = userBookId;
            this.title = title;
            this.author = author;
            this.listType = listType;
            this.rating = null;
        }
    }

    public static List<BookRowDTO> findBooksForUser(int userId) {
        String sql = """
            SELECT ub.id AS userBookId,
                   b.title,
                   b.author,
                   ub.list_type,
                   ub.rating
            FROM user_books ub
            JOIN books b ON ub.book_id = b.id
            WHERE ub.user_id = ?
        """;

        List<BookRowDTO> result = new ArrayList<>();

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new BookRowDTO(
                            rs.getInt("userBookId"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("list_type"),
                            (Integer) rs.getObject("rating")
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }

        return result;
    }

    public static void addBookForUser(int userId, String title, String author, String listType) {
        String insertBook = "INSERT INTO books (title, author) VALUES (?, ?)";
        String insertUserBook = "INSERT INTO user_books (user_id, book_id, list_type) VALUES (?, ?, ?)";

        try (Connection conn = DataBaseManager.getConnection()) {
            conn.setAutoCommit(false);

            int bookId;

            try (PreparedStatement stmt = conn.prepareStatement(insertBook, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, title);
                stmt.setString(2, author);
                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conn.rollback();
                        return;
                    }
                    bookId = keys.getInt(1);
                }
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(insertUserBook)) {
                stmt2.setInt(1, userId);
                stmt2.setInt(2, bookId);
                stmt2.setString(3, listType);
                stmt2.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    public static void deleteUserBook(int userBookId) {
        String sql = "DELETE FROM user_books WHERE id = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userBookId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error deleting user_book: " + e.getMessage());
        }
    }

    public static void updateListTypeAndRating(int userBookId, String listType, Integer rating) {
        String sql = "UPDATE user_books SET list_type = ?, rating = ? WHERE id = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, listType);

            if (rating == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, rating);
            }

            stmt.setInt(3, userBookId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating list_type/rating: " + e.getMessage());
        }
    }

    public static int countTotalBooks() {
        String sql = "SELECT COUNT(*) FROM user_books";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int countListType(String type) {
        String sql = "SELECT COUNT(*) FROM user_books WHERE list_type = ?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String findMostPopularListType() {
        String sql = """
            SELECT list_type, COUNT(*) AS cnt
            FROM user_books
            GROUP BY list_type
            ORDER BY cnt DESC
            LIMIT 1
        """;

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getString("list_type");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "N/A";
    }

    public static Map<String, Integer> countBooksByTitle() {
        Map<String, Integer> map = new LinkedHashMap<>();

        String sql = """
            SELECT b.title, COUNT(*) AS cnt
            FROM user_books ub
            JOIN books b ON ub.book_id = b.id
            GROUP BY b.title
            ORDER BY cnt DESC
            LIMIT 10
        """;

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("title"), rs.getInt("cnt"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static Map<String, Integer> countBooksByAuthor() {
        Map<String, Integer> map = new LinkedHashMap<>();

        String sql = """
            SELECT b.author, COUNT(*) AS cnt
            FROM user_books ub
            JOIN books b ON ub.book_id = b.id
            GROUP BY b.author
            ORDER BY cnt DESC
            LIMIT 10
        """;

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("author"), rs.getInt("cnt"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static Map<String, Integer> getCountsByListTypeForUser(int userId) {
        Map<String, Integer> result = new HashMap<>();

        String sql = """
            SELECT list_type, COUNT(*) AS count
            FROM user_books
            WHERE user_id = ?
            GROUP BY list_type
        """;

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("list_type"), rs.getInt("count"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error counting books by list_type: " + e.getMessage());
        }

        return result;
    }

    public static List<Book> findAllBooks() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }

        return list;
    }

    public static void addBook(String title, String author, String genre) {
        String sql = "INSERT INTO books(title, author, genre) VALUES (?, ?, ?)";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, genre);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    public static void updateBook(int id, String title, String author, String genre) {
        String sql = "UPDATE books SET title=?, author=?, genre=? WHERE id=?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, genre);
            stmt.setInt(4, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
    }

    public static void deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id=?";

        try (Connection conn = DataBaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
        }
    }
}
