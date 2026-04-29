package ua.edu.sumdu;

import ua.edu.sumdu.model.*;
import ua.edu.sumdu.db.DatabaseManager;
import ua.edu.sumdu.storage.BookStorage;
import ua.edu.sumdu.storage.TxtBookStorage;
import ua.edu.sumdu.storage.JsonBookStorage;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Контролер програми «Book Manager».
 *
 * <p>Делегує зберігання та пошук до екземпляра {@link Library}.
 * Відповідає виключно за взаємодію з користувачем, введення даних
 * та координацію завантаження/збереження через {@link BookStorage}.</p>
 *
 * <p>Головне меню:</p>
 * <ol>
 *   <li>Пошук об'єкта</li>
 *   <li>Створити новий об'єкт</li>
 *   <li>Вивести всі об'єкти</li>
 *   <li>Завершити роботу</li>
 * </ol>
 *
 * <p>Ієрархія підтримуваних класів:</p>
 * <pre>
 * Book
 * ├── EBook
 * ├── AudioBook
 * └── PaperBook
 *     └── RareBook
 * </pre>
 *
 * <p>На старті завантажує дані з {@code input.txt} або {@code input.json}.
 * При завершенні зберігає актуальний стан до обох файлів.</p>
 */
public class BookManager {

    // ---------------------------------------------------------------
    // Константи — шляхи до файлів
    // ---------------------------------------------------------------

    /** Шлях до текстового файлу зберігання. */
    private static final String TXT_FILE  = "input.txt";

    /** Шлях до JSON-файлу зберігання (альтернативний варіант). */
    private static final String JSON_FILE = "input.json";

    // ---------------------------------------------------------------
    // Поля
    // ---------------------------------------------------------------

    /** Єдина колекція для об'єктів усієї ієрархії. */
    private final Library library;

    /** Сховище у форматі текстового файлу. */
    private final BookStorage txtStorage;

    /** Сховище у форматі JSON. */
    private final BookStorage jsonStorage;

    /** Спільний Scanner для всієї програми. */
    private final Scanner scanner;

    /** Менеджер бази даних. */
    private final DatabaseManager db;

    // ---------------------------------------------------------------
    // Конструктор
    // ---------------------------------------------------------------

    /**
     * Ініціалізує контролер: створює бібліотеку з іменем/адресою за замовчуванням
     * та обидва сховища.
     */
    public BookManager(DatabaseManager db) {
        this.library     = new Library("City Library", "Main St. 1");
        this.txtStorage  = new TxtBookStorage(TXT_FILE);
        this.jsonStorage = new JsonBookStorage(JSON_FILE);
        this.scanner     = new Scanner(System.in);
        this.db          = db;
    }

    // ---------------------------------------------------------------
    // Точка входу в контролер
    // ---------------------------------------------------------------

    /**
     * Запускає головний цикл програми:
     * завантажує дані → цикл меню → зберігає дані → закриває сканер.
     */
    public void run() {
        printBanner();
        loadBooks();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1 -> searchMenu();
                case 2 -> createObject();
                case 3 -> printAllBooks();
                case 4 -> {
                    saveBooks();
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("  [!] Please enter 1, 2 or 3.\n");
            }
        }

        scanner.close();
    }

    // ---------------------------------------------------------------
    // Операції при ініціації програми
    // ---------------------------------------------------------------

    /**
     * Виводить інформаційну шапку програми.
     */
    private void printBanner() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║            BOOK MANAGER  v7.0            ║");
        System.out.println("║ Library | Aggregation | TXT+JSON storage ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    /**
     * Завантажує книги з файлів при старті програми.
     *
     * <p>Спочатку завантажує {@code input.txt}; якщо той порожній —
     * намагається завантажити {@code input.json}. Завдяки цьому обидва
     * формати залишаються актуальними після збереження.</p>
     */
    private void loadBooks() {
        System.out.println("\n--- Loading data ---");
        txtStorage.load(library);
        if (library.getEntryCount() == 0) {
            jsonStorage.load(library);
        }
        System.out.println("  Library \"" + library.getName()
                + "\": " + library.getEntryCount() + " unique title(s) on start.\n");
    }

    /**
     * Зберігає поточний стан колекції до обох форматів при виході.
     */
    private void saveBooks() {
        System.out.println("\n--- Saving data ---");
        txtStorage.save(library);
        jsonStorage.save(library);
    }

    // ---------------------------------------------------------------
    // Головне меню
    // ---------------------------------------------------------------

    /**
     * Виводить пункти головного меню.
     */
    private void printMainMenu() {
        System.out.println("==========================================");
        System.out.println("1. Search book");
        System.out.println("2. Create new book");
        System.out.println("3. Show all books");
        System.out.println("4. Exit");
        System.out.print("Your choice: ");
    }

    /**
     * Зчитує вибір пункту меню (ціле число).
     * Якщо введено нечислове значення — повертає {@code -1}.
     *
     * @return вибраний пункт меню або {@code -1} при помилці введення
     */
    private int readMenuChoice() {
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    // ---------------------------------------------------------------
    // Пункт 1: Пошук книги
    // ---------------------------------------------------------------

    /**
     * Підменю вибору критерію пошуку. {@code 0} — повернення до меню.
     */
    private void searchMenu() {
            System.out.println("\n--- Search ---");
            System.out.println("  1. By author");
            System.out.println("  2. By genre");
            System.out.println("  3. By price range");
            System.out.println("  0. Back to main menu");
            System.out.print("Criterion: ");

            int choice = readMenuChoice();
            System.out.println();

            switch (choice) {
                case 1 -> searchByAuthor();
                case 2 -> searchByGenre();
                case 3 -> searchByPriceRange();
                case 0 -> System.out.println("  Cancelled.\n");
                default -> System.out.println("  [!] Unknown criterion.\n");
            }
    }

    private void searchByAuthor() {
        String author = readNonEmptyString("Author name: ");
        ArrayList<BookEntry> result = library.findByAuthor(author);
        printSearchResult(result, "author contains \"" + author + "\"");
    }

    private void searchByGenre() {
        Genre genre = readEnum(Genre.values(), "Genre");
        System.out.println();
        ArrayList<BookEntry> result = library.findByGenre(genre);
        printSearchResult(result, "genre = " + genre);
    }

    private void searchByPriceRange() {
        double minPrice = readDouble("Min price ($): ");
        double maxPrice = readDouble("Max price ($): ");
        ArrayList<BookEntry> result = library.findByPriceRange(minPrice, maxPrice);
        printSearchResult(result,
                "price in [$" + String.format("%.2f", minPrice)
                        + " .. $" + String.format("%.2f", maxPrice) + "]");
    }

    /**
     * Виводить результати пошуку або повідомлення про відсутність збігів.
     * Показує книгу та кількість примірників.
     *
     * @param result    список знайдених записів
     * @param criterion текстовий опис критерію
     */
    private void printSearchResult(ArrayList<BookEntry> result, String criterion) {
        System.out.println("--- Search results [" + criterion + "] ---");
        if (result.isEmpty()) {
            System.out.println("  No objects found matching the given criterion.\n");
            return;
        }
        System.out.println("  Found " + result.size() + " record(s):");
        for (int i = 0; i < result.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + result.get(i));
        }
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Пункт 2: Створення нової книги
    // ---------------------------------------------------------------

    /**
     * Показує підменю вибору типу об'єкта та делегує до відповідного
     * методу створення. Пункт {@code 0} дозволяє повернутись до головного
     * меню без створення об'єкта.
     */
    private void createObject() {
        System.out.println("\n--- Select type ---");
        System.out.println("  1. Book (base)");
        System.out.println("  2. EBook");
        System.out.println("  3. Audio Book");
        System.out.println("  4. Paper Book");
        System.out.println("  5. Rare Book");
        System.out.println("  0. Back to main menu");
        System.out.print("Type: ");

        int type = readMenuChoice();
        System.out.println();

        switch (type) {
            case 1 -> createBook();
            case 2 -> createEBook();
            case 3 -> createAudioBook();
            case 4 -> createPaperBook();
            case 5 -> createRareBook();
            case 0 -> System.out.println("  Cancelled.\n");
            default -> System.out.println("  [!] Unknown type. Returning to main menu.\n");
        }
    }

    // ---------------------------------------------------------------
    // Методи створення конкретних книг
    // ---------------------------------------------------------------

    /**
     * Зчитує дані для базової {@link Book} та додає її до колекції.
     * При будь-якій помилці введення виводить повідомлення і повертається до меню.
     */
    private void createBook() {
        System.out.println("--- Add new Book ---");
        try {
            String title    = readNonEmptyString("Title:  ");
            String author   = readNonEmptyString("Author: ");
            int    year     = readInt("Year:   ");
            double price    = readDouble("Price:  ");
            Genre genre     = readEnum(Genre.values(), "Genre");
            int    pages    = readInt("Pages:  ");
            int    quantity = readInt("Quantity:  ");

            Book book = new Book(title, author, year, price, genre, pages);
            library.addNewBook(book, quantity);
            persistToDatabase(book, quantity);
            System.out.println("  [OK] Book added. Library size: " + library.getEntryCount() + "\n");

        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    /**
     * Зчитує дані для {@link EBook} та додає її до колекції.
     */
    private void createEBook() {
        System.out.println("--- Add EBook ---");
        try {
            String title        = readNonEmptyString("Title:    ");
            String author       = readNonEmptyString("Author:   ");
            int    year         = readInt("Year:     ");
            double price        = readDouble("Price:    ");
            Genre  genre        = readEnum(Genre.values(),"Genre");
            int    pages        = readInt("Pages:  ");
            String fileFormat   = readNonEmptyString("File format (EPUB/PDF/MOBI): ");
            double fileSizeMB   = readDouble("File size (MB):  ");
            String downloadUrl  = readNonEmptyString("Download URL: ");
            int    quantity    = readInt("Quantity:  ");

            EBook book = new EBook(title, author, year, price, genre, pages,
                    fileFormat, fileSizeMB, downloadUrl);
            library.addNewBook(book, quantity);
            persistToDatabase(book, quantity);
            System.out.println("  [OK] EBook added. Library size: " + library.getEntryCount() + "\n");

        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    /**
     * Зчитує дані для {@link AudioBook} та додає її до колекції.
     */
    private void createAudioBook() {
        System.out.println("--- Add AudioBook ---");
        try {
            String title            = readNonEmptyString("Title:    ");
            String author           = readNonEmptyString("Author:   ");
            int    year             = readInt("Year:     ");
            double price            = readDouble("Price:    ");
            Genre  genre            = readEnum(Genre.values(), "Genre");
            int    pages            = readInt("Pages (original):    ");
            String narrator         = readNonEmptyString("Narrator:     ");
            int    durationMinutes  = readInt("Duration (minutes):  ");
            String audioFormat      = readNonEmptyString("Audio format (MP3/AAC/FLAC): ");
            int    quantity        = readInt("Quantity:  ");

            AudioBook book = new AudioBook(title, author, year, price, genre, pages,
                    narrator, durationMinutes, audioFormat);
            library.addNewBook(book, quantity);
            persistToDatabase(book, quantity);
            System.out.println("  [OK] AudioBook added. Library size: " + library.getEntryCount() + "\n");
        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    /**
     * Зчитує дані для {@link PaperBook} та додає її до колекції.
     */
    private void createPaperBook() {
        System.out.println("--- Add PaperBook ---");
        try {
            String title       = readNonEmptyString("Title:     ");
            String author      = readNonEmptyString("Author:    ");
            int    year        = readInt("Year:      ");
            double price       = readDouble("Price:     ");
            Genre  genre       = readEnum(Genre.values(), "Genre");
            int    pages       = readInt("Pages:    ");
            String publisher   = readNonEmptyString("Publisher:  ");
            int    edition     = readInt("Edition:    ");
            double weightGrams = readDouble("Weight (g): ");
            int    quantity    = readInt("Quantity:  ");

            PaperBook book = new PaperBook(title, author, year, price, genre, pages,
                    publisher, edition, weightGrams);
            library.addNewBook(book, quantity);
            persistToDatabase(book, quantity);
            System.out.println("  [OK] PaperBook added. Library size: " + library.getEntryCount() + "\n");
        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    /**
     * Зчитує дані для {@link RareBook} та додає її до колекції.
     */
    private void createRareBook() {
        System.out.println("--- Add RareBook ---");
        try {
            String        title             = readNonEmptyString("Title:    ");
            String        author            = readNonEmptyString("Author:   ");
            int           year              = readInt("Year:     ");
            double        price             = readDouble("Price:    ");
            Genre         genre             = readEnum(Genre.values(), "Genre:  ");
            int           pages             = readInt("Pages:   ");
            String        publisher         = readNonEmptyString("Publisher: ");
            int           edition           = readInt("Edition:   ");
            double        weightGrams       = readDouble("Weight (g): ");
            BookCondition condition         = readEnum(BookCondition.values(), "Condition");
            double        estimatedValueUSD = readDouble("Estimated value ($):  ");
            int           acquisitionYear   = readInt("Acquisition year:     ");
            int           quantity          = readInt("Quantity:  ");

            RareBook book = new RareBook(title, author, year, price, genre, pages,
                    publisher, edition, weightGrams, condition, estimatedValueUSD, acquisitionYear);
            library.addNewBook(book, quantity);
            persistToDatabase(book, quantity);
            System.out.println("  [OK] RareBook added. Library size: " + library.getEntryCount() + "\n");
        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    // ---------------------------------------------------------------
    // Пункт 3: Виведення всіх книг
    // ---------------------------------------------------------------

    /**
     * Виводить усі записи бібліотеки (книга + кількість примірників)
     * через посилання базового типу {@link Book}.
     *
     * <p>Демонстрація поліморфізму: метод {@code toString()} викликається
     * відповідно до реального типу кожного об'єкта.</p>
     * Якщо список порожній — повідомляє про це.
     */
    private void printAllBooks() {
        System.out.println("\n--- Library: " + library.getName()
                + " [" + library.getEntryCount() + " title(s)] ---");
        if (library.getEntryCount() == 0) {
            System.out.println("  (library is empty)\n");
            return;
        }
        for (int i = 0; i < library.getEntryCount(); i++) {
            BookEntry entry = library.getEntry(i);
            System.out.println("  " + (i + 1) + ". " + entry);
        }
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Допоміжні методи введення
    // ---------------------------------------------------------------

    /**
     * Зчитує непорожній рядок із клавіатури.
     * Повторює запит доти, доки користувач не введе хоча б один непробільний символ.
     *
     * @param prompt текст підказки, що виводиться перед полем введення
     * @return непорожній рядок після {@code trim()}
     */
    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("  [!] This field cannot be empty. Try again.");
        }
    }

    /**
     * Зчитує ціле число з клавіатури.
     * Повторює запит при нечисловому або порожньому введенні.
     *
     * @param prompt текст підказки
     * @return введене ціле число
     */
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("  [!] Value cannot be empty. Try again.");
                continue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a whole number. Try again.");
            }
        }
    }

    /**
     * Зчитує дійсне число. Підтримує крапку і кому як роздільник.
     *
     * @param prompt текст підказки
     * @return введене дійсне число
     */
    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            // Замінюємо кому на крапку, щоб підтримувати обидва формати
            String line = scanner.nextLine().trim().replace(',', '.');
            if (line.isEmpty()) {
                System.out.println("  [!] Value cannot be empty. Try again.");
                continue;
            }
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a valid number (e.g. 19.99). Try again.");
            }
        }
    }

    /**
     * Відображає нумерований список констант enum і повертає обрану.
     *
     * @param <T>    тип enum
     * @param values масив констант ({@code SomeEnum.values()})
     * @param label  назва поля для підказки
     * @return обрана константа
     */
    private <T extends Enum<T>> T readEnum(T[] values, String label) {
        System.out.println("  " + label + ":");
        for (int i = 0; i < values.length; i++) {
            System.out.println("    " + (i + 1) + ". " + values[i]);
        }
        while (true) {
            int choice = readInt("  " + label + " [1-" + values.length + "]: ");
            if (choice >= 1 && choice <= values.length) {
                return values[choice - 1];
            }
            System.out.println("  [!] Enter a number from 1 to " + values.length + ".");
        }
    }

    // Допоміжний метод взаємодії з БД
    private void persistToDatabase(Book book, int quantity) {
        if (db == null || !db.isConnected()) {
            return;
        }
        try {
            db.insertBook(book, quantity);
        } catch (java.sql.SQLException e) {
            System.out.println("  [DB] Failed to insert record: " + e.getMessage());
        }
    }


}

