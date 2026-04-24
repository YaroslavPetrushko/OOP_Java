package ua.edu.sumdu;

import ua.edu.sumdu.model.*;
import ua.edu.sumdu.storage.BookStorage;
import ua.edu.sumdu.storage.TxtBookStorage;
import ua.edu.sumdu.storage.JsonBookStorage;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Контролер програми «Book Manager».
 *
 * <p>Відповідає за:</p>
 * <ul>
 *   <li>консольне меню та взаємодію з користувачем;</li>
 *   <li>зберігання колекції {@code ArrayList<Book>};</li>
 *   <li>координацію завантаження/збереження через {@link BookStorage}.</li>
 * </ul>
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
 * <p>На старті завантажує дані з {@code input.txt} та {@code input.json}.
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
    private final ArrayList<Book> books;

    /** Сховище у форматі текстового файлу. */
    private final BookStorage txtStorage;

    /** Сховище у форматі JSON. */
    private final BookStorage jsonStorage;

    /** Спільний Scanner для всієї програми. */
    private final Scanner scanner;

    // ---------------------------------------------------------------
    // Конструктор
    // ---------------------------------------------------------------

    /**
     * Ініціалізує контролер: створює сховища та порожню колекцію.
     * Дані завантажуються при виклику {@link #run()}.
     */
    public BookManager() {
        this.txtStorage  = new TxtBookStorage(TXT_FILE);
        this.jsonStorage = new JsonBookStorage(JSON_FILE);
        this.books       = new ArrayList<Book>();
        this.scanner     = new Scanner(System.in);
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
                case 1 -> createObject();
                case 2 -> printAllBooks();
                case 3 -> {
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
        System.out.println("║            BOOK MANAGER  v5.0            ║");
        System.out.println("║  5-class hierarchy | TXT + JSON storage  ║");
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
        ArrayList<Book> loaded = txtStorage.load();

        if (loaded.isEmpty()) {
            loaded = jsonStorage.load();
        }

        for (int i = 0; i < loaded.size(); i++) {
            books.add(loaded.get(i));
        }
        System.out.println("  Collection size on start: " + books.size() + "\n");
    }

    /**
     * Зберігає поточний стан колекції до обох форматів при виході.
     */
    private void saveBooks() {
        System.out.println("\n--- Saving data ---");
        txtStorage.save(books);
        jsonStorage.save(books);
    }

    // ---------------------------------------------------------------
    // Головне меню
    // ---------------------------------------------------------------

    /**
     * Виводить пункти головного меню.
     */
    private void printMainMenu() {
        System.out.println("==========================================");
        System.out.println("1. Create new book");
        System.out.println("2. Show all books");
        System.out.println("3. Exit");
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
    // Пункт 1: Створення нової книги
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

            books.add(new Book(title, author, year, price, genre, pages));
            System.out.println("  [OK] Book added. Total in collection: " + books.size() + "\n");

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

            books.add(new EBook(title, author, year, price, genre, pages,
                    fileFormat, fileSizeMB, downloadUrl));
            System.out.println("  [OK] EBook added. Total in collection: " + books.size() + "\n");

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

            books.add(new AudioBook(title, author, year, price, genre, pages,
                    narrator, durationMinutes, audioFormat));
            System.out.println("  [OK] AudioBook added. Total in collection: " + books.size() + "\n");
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

            books.add(new PaperBook(title, author, year, price, genre, pages,
                    publisher, edition, weightGrams));
            System.out.println("  [OK] PaperBook added. Total in collection: " + books.size() + "\n");
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

            books.add(new RareBook(title, author, year, price, genre, pages,
                    publisher, edition, weightGrams,
                    condition, estimatedValueUSD, acquisitionYear));
            System.out.println("  [OK] RareBook added. Total in collection: " + books.size() + "\n");
        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    // ---------------------------------------------------------------
    // Пункт 2: Виведення всіх книг
    // ---------------------------------------------------------------

    /**
     * Виводить усі об'єкти колекції через посилання базового типу {@link Book}.
     *
     * <p>Демонстрація поліморфізму: метод {@code toString()} викликається
     * відповідно до реального типу кожного об'єкта.</p>
     * Якщо список порожній — повідомляє про це.
     */
    private void printAllBooks() {
        System.out.println("\n--- Book List ---");
        System.out.println("\n--- All objects [" + books.size() + "] ---");
        if (books.isEmpty()) {
            System.out.println("  (collection is empty)\n");
            return;
        }
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);   // посилання базового типу
            System.out.println("  " + (i + 1) + ". " + book);  // toString() — поліморфний виклик
        }
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Допоміжні методи зчитування
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

}

