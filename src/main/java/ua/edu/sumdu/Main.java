package ua.edu.sumdu;

import ua.edu.sumdu.db.DatabaseManager;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Точка входу в програму «Book Manager».
 *
 * <p>Клас є виключно драйвером: створює {@link BookManager} та
 * передає йому управління. Вся бізнес-логіка, введення/виведення
 * та файлові операції реалізовані у {@link BookManager}.</p>
 */
public class Main {

    /**
     * Запускає програму.
     *
     * @param args аргументи командного рядка (не використовуються)
     */
    public static void main(String[] args) {
        DatabaseManager db = null;

        if (args.length > 0) {
            System.out.println("[DB] Loading config: " + args[0]);
            try {
                db = new DatabaseManager(args[0]);
            } catch (IOException e) {
                System.out.println("[DB] Cannot read config file: " + e.getMessage());
                System.out.println("[DB] Continuing without database.");
            } catch (SQLException e) {
                System.out.println("[DB] Connection failed: " + e.getMessage());
                System.out.println("[DB] Continuing without database.");
            }
        } else {
            System.out.println("[DB] No config file provided — running without database.");
        }

        try {
            new BookManager(db).run();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}