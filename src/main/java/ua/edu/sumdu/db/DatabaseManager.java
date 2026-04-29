package ua.edu.sumdu.db;

import ua.edu.sumdu.model.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.Properties;

/**
 * Менеджер бази даних для збереження об'єктів ієрархії {@link Book}.
 *
 * <p>Використовує стратегію «single-table inheritance»: усі підкласи
 * зберігаються в одній таблиці {@code books}. Тип об'єкта визначається
 * полем {@code type} (дискримінатор).</p>
 *
 * <p>Параметри підключення зчитуються з конфігураційного файлу,
 * шлях до якого передається через аргументи командного рядка.</p>
 *
 * <p>Усі запити виконуються виключно через {@link PreparedStatement}.</p>
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

    /** SQL-запит для пошуку ідентичної книги*/
    private static final String CHECK_SQL =
            "SELECT id FROM books WHERE type = ? AND title = ? AND author = ? AND year = ?";

    /** SQL-запит для оновлення книги.*/
    private static final String UPDATE_SQL=
            "UPDATE books SET quantity = quantity + ? WHERE id = ?";

    /** З'єднання з базою даних. */
    private final Connection connection;

    // ---------------------------------------------------------------
    // Конструктор
    // ---------------------------------------------------------------

    /**
     * Ініціалізує менеджер: зчитує параметри підключення з файлу та
     * відкриває з'єднання з PostgreSQL.
     *
     * @param configPath шлях до файлу {@code db.properties}
     * @throws IOException  якщо файл не знайдено або не вдалося прочитати
     * @throws SQLException якщо підключення до БД не вдалося встановити
     */
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
     * Виконує INSERT у таблицю {@code books} для вказаного об'єкта.
     *
     * <p>Тип об'єкта визначається автоматично через {@code instanceof},
     * поля, що не стосуються конкретного підкласу, заповнюються як {@code NULL}.</p>
     *
     * @param book     книга для збереження; не може бути {@code null}
     * @param quantity кількість примірників
     * @throws SQLException при помилці виконання запиту
     */
    public void insertBook(Book book, int quantity) throws SQLException {
        String type = resolveType(book);
        int existingId = -1;

        // 1. Перевіряємо, чи існує вже така книга в базі
        String checkSql = CHECK_SQL;
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, type);
            checkStmt.setString(2, book.getTitle());
            checkStmt.setString(3, book.getAuthor());
            checkStmt.setInt(4, book.getYear());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    existingId = rs.getInt("id"); // Отримуємо ID існуючої книги
                }
            }
        }

        // 2. Якщо книга існує -> Оновлюємо quantity
        if (existingId != -1) {
            String updateSql = UPDATE_SQL;
            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, existingId);
                updateStmt.executeUpdate();

                System.out.println("  [DB] Updated quantity for " + type
                        + " \"" + book.getTitle() + "\" (added " + quantity + ").");
            }
            return;
        }

        // 3. Якщо книги немає -> Робимо стандартний INSERT (ваш старий код)
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {

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
            System.out.println("  [DB] Inserted new" + type
                    + " \"" + book.getTitle() + "\" (qty=" + quantity + ").");
        }
    }

    /**
     * Закриває з'єднання з базою даних.
     * Викликається при завершенні програми.
     */
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

    /**
     * Перевіряє, чи є активне з'єднання з БД.
     *
     * @return {@code true} якщо з'єднання відкрите
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // ---------------------------------------------------------------
    // Приватні допоміжні методи
    // ---------------------------------------------------------------

    /**
     * Зчитує {@link Properties} з файлу за вказаним шляхом.
     *
     * @param path шлях до файлу
     * @return завантажені властивості
     * @throws IOException при помилці читання
     */
    private Properties loadProperties(String path) throws IOException {
        Properties props = new Properties();
        try (Reader reader = Files.newBufferedReader(Paths.get(path))) {
            props.load(reader);
        }
        return props;
    }

    /**
     * Визначає рядковий дискримінатор типу для поля {@code type} у таблиці.
     *
     * <p>Перевірка виконується у порядку від найспецифічнішого до базового,
     * щоб {@code RareBook} не потрапив до гілки {@code PaperBook}.</p>
     *
     * @param book об'єкт книги
     * @return рядок типу: {@code "RAREBOOK"}, {@code "PAPERBOOK"},
     *         {@code "EBOOK"}, {@code "AUDIOBOOK"} або {@code "BOOK"}
     */
    private String resolveType(Book book) {
        if (book instanceof RareBook)  return "RAREBOOK";
        if (book instanceof PaperBook) return "PAPERBOOK";
        if (book instanceof EBook)     return "EBOOK";
        if (book instanceof AudioBook) return "AUDIOBOOK";
        return "BOOK";
    }
}