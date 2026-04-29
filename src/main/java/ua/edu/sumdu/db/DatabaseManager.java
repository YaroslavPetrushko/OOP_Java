package ua.edu.sumdu.db;

import ua.edu.sumdu.model.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * Менеджер бази даних для збереження об'єктів Book.
 */
public class DatabaseManager {

    /** SQL-запит для вставки запису про книгу. */
    private static final String INSERT_SQL =
            "INSERT INTO books " +
                    "(type, title, author, year, price, genre, pages, quantity, " +
                    " file_format, file_size_mb, download_url, " +
                    " narrator, duration_minutes, audio_format, " +
                    " publisher, edition, weight_grams, " +
                    " condition, estimated_value_usd, acquisition_year) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /** З'єднання з базою даних. */
    private final Connection connection;

    // ---------------------------------------------------------------
    // Конструктор
    // ---------------------------------------------------------------

    public DatabaseManager(String configPath) throws IOException, SQLException {
        Properties props = loadProperties(configPath);

        String url      = props.getProperty("db.url");
        String user     = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new IOException(
                    "Config file must contain db.url, db.user, and db.password.");
        }

        this.connection = DriverManager.getConnection(url, user, password);
        System.out.println("  [DB] Connected: " + url);
    }

    // ---------------------------------------------------------------
    // Публічний API
    // ---------------------------------------------------------------

    /**
     * INSERT у таблицю books
     */
    public void insertBook(Book book, int quantity) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(INSERT_SQL);

            String type = resolveType(book);

            // --- Загальні поля (позиції 1–8) ---
            stmt.setString(1, type);
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setInt   (4, book.getYear());
            stmt.setDouble(5, book.getPrice());
            stmt.setString(6, book.getGenre().name());
            stmt.setInt   (7, book.getPages());
            stmt.setInt   (8, quantity);

            // --- EBook (позиції 9–11) ---
            if (book instanceof EBook) {
                EBook eb = (EBook) book;
                stmt.setString(9,  eb.getFileFormat());
                stmt.setDouble(10, eb.getFileSizeMB());
                stmt.setString(11, eb.getDownloadUrl());
            } else {
                stmt.setNull(9,  Types.VARCHAR);
                stmt.setNull(10, Types.DOUBLE);
                stmt.setNull(11, Types.VARCHAR);
            }

            // --- AudioBook (позиції 12–14) ---
            if (book instanceof AudioBook) {
                AudioBook ab = (AudioBook) book;
                stmt.setString(12, ab.getNarrator());
                stmt.setInt   (13, ab.getDurationMinutes());
                stmt.setString(14, ab.getAudioFormat());
            } else {
                stmt.setNull(12, Types.VARCHAR);
                stmt.setNull(13, Types.INTEGER);
                stmt.setNull(14, Types.VARCHAR);
            }

            // --- PaperBook та RareBook (позиції 15–17) ---
            if (book instanceof PaperBook) {
                PaperBook pb = (PaperBook) book;
                stmt.setString(15, pb.getPublisher());
                stmt.setInt   (16, pb.getEdition());
                stmt.setDouble(17, pb.getWeightGrams());
            } else {
                stmt.setNull(15, Types.VARCHAR);
                stmt.setNull(16, Types.INTEGER);
                stmt.setNull(17, Types.DOUBLE);
            }

            // --- RareBook (позиції 18–20) ---
            if (book instanceof RareBook) {
                RareBook rb = (RareBook) book;
                stmt.setString(18, rb.getCondition().name());
                stmt.setDouble(19, rb.getEstimatedValueUSD());
                stmt.setInt   (20, rb.getAcquisitionYear());
            } else {
                stmt.setNull(18, Types.VARCHAR);
                stmt.setNull(19, Types.DOUBLE);
                stmt.setNull(20, Types.INTEGER);
            }

            stmt.executeUpdate();
            System.out.println("  [DB] Inserted " + type
                    + " \"" + book.getTitle() + "\" (qty=" + quantity + ").");

        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("  [DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("  [DB] Error closing connection: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // ---------------------------------------------------------------
    // Допоміжні методи
    // ---------------------------------------------------------------

    private Properties loadProperties(String path) throws IOException {
        Properties props = new Properties();
        try (Reader reader = Files.newBufferedReader(Paths.get(path))) {
            props.load(reader);
        }
        return props;
    }

    private String resolveType(Book book) {
        if (book instanceof RareBook)  return "RAREBOOK";
        if (book instanceof PaperBook) return "PAPERBOOK";
        if (book instanceof EBook)     return "EBOOK";
        if (book instanceof AudioBook) return "AUDIOBOOK";
        return "BOOK";
    }
}