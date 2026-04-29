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
 *
 * <p>Якщо передано аргумент командного рядка — трактується як шлях до
 * конфігураційного файлу БД ({@code db.properties}) і ініціалізується
 * {@link DatabaseManager}. Без аргументу програма працює без БД.</p>
 *
 * <p>Приклад запуску з БД:</p>
 * <pre>
 *   java -cp ... ua.edu.sumdu.Main db.properties
 *   java -jar pr12-7.0-DB.jar db.properties
 * </pre>
 *
 */
public class Main {

    /**
     * Запускає програму.
     *
     * @param args {@code args[0]} — необов'язковий шлях до конфіг-файлу БД
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