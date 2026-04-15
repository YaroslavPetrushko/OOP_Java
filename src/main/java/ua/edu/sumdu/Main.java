package ua.edu.sumdu;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Точка входу в програму "Book Manager".
 *
 * <p>Реалізує консольне меню з трьома пунктами:</p>
 * <ol>
 *   <li>Створити новий об'єкт {@link Book}</li>
 *   <li>Вивести інформацію про всі збережені об'єкти</li>
 *   <li>Завершити роботу</li>
 * </ol>
 * <p>Усі помилки введення (нечислові дані, порожні рядки, некоректні значення)
 * перехоплюються та повідомляються користувачу без завершення програми.</p>
 */
public class Main {

    /** Єдина колекція для об'єктів усіх типів ієрархії. Порожня на старті. */
    private static final ArrayList<Book> books = new ArrayList<>();

    /** Спільний Scanner для всієї програми. */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Запускає консольний цикл меню.
     *
     * @param args аргументи командного рядка (не використовуються)
     */
    public static void main(String[] args) {
        System.out.println("=== Book Manager ===");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1 -> createObject();
                case 2 -> printAllBooks();
                case 3 -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("  [!] Please enter 1, 2 or 3.\n");
            }
        }

        scanner.close();
    }

    // ---------------------------------------------------------------
    // Меню
    // ---------------------------------------------------------------

    /**
     * Виводить головне меню на екран.
     */
    private static void printMenu() {
        System.out.println("--------------------");
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
    private static int readMenuChoice() {
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
    private static void createObject() {
        System.out.println("\n--- Select type ---");
        System.out.println("  1. Book (base)");
        System.out.println("  2. EBook");
        System.out.println("  3. AudioBook");
        System.out.println("  4. PaperBook");
        System.out.println("  5. RareBook");
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

    // Методи створення книг
    /**
     * Інтерактивно зчитує дані нової книги та додає її до списку.
     * При будь-якій помилці введення виводить повідомлення і повертається до меню.
     */
    private static void createBook() {
        System.out.println("--- Add new Book ---");
        try {
            String title    = readNonEmptyString("Title:  ");
            String author   = readNonEmptyString("Author: ");
            int    year     = readInt("Year:   ");
            double price    = readDouble("Price:  ");
            Genre genre     = readEnum(Genre.values(), "Genre");
            int    pages    = readInt("Pages:  ");

            books.add(new Book(title, author, year, price, genre, pages));
            System.out.println("  [OK] Book added successfully.\n");

        } catch (InvalidBookDataException e) {
            System.out.println("  [!] Validation error: " + e.getMessage() + "\n");
        }
    }

    private static void createEBook() {
        System.out.println("--- Add EBook ---");
        try {
            String title        = readNonEmptyString("Title:    ");
            String author       = readNonEmptyString("Author:   ");
            int    year         = readInt("Year:     ");
            double price        = readDouble("Price:    ");
            Genre  genre        = readEnum(Genre.values(),"Genre");
            int    pages        = readInt("Pages:   ");
            String fileFormat   = readNonEmptyString("File format (e.g. EPUB, PDF): ");
            double fileSizeMB   = readDouble("File size (MB):   ");
            String downloadUrl  = readNonEmptyString("Download URL: ");

            books.add(new EBook(title, author, year, price, genre, pages,
                    fileFormat, fileSizeMB, downloadUrl));
            System.out.println("  [OK] EBook added.\n");

        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    private static void createAudioBook() {
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
            String audioFormat      = readNonEmptyString("Audio format (e.g. MP3, AAC, FLAC): ");

            books.add(new AudioBook(title, author, year, price, genre, pages,
                    narrator, durationMinutes, audioFormat));
            System.out.println("  [OK] AudioBook added.\n");
        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    private static void createPaperBook() {
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
            System.out.println("  [OK] PaperBook added.\n");

        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    private static void createRareBook() {
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
            System.out.println("  [OK] RareBook added.\n");
        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    // ---------------------------------------------------------------
    // Пункт 3: Виведення всіх книг
    // ---------------------------------------------------------------

    /**
     * Виводить усі збережені книги у форматованому вигляді.
     * Якщо список порожній — повідомляє про це.
     */
    private static void printAllBooks() {
        System.out.println("\n--- Book List ---");
        if (books.isEmpty()) {
            System.out.println("  (no books added yet)\n");
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
     * @return непорожній рядок (після trim)
     */
    private static String readNonEmptyString(String prompt) {
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
    private static int readInt(String prompt) {
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
     * Зчитує число з плаваючою крапкою з клавіатури.
     * Приймає як крапку, так і кому як роздільник.
     * Повторює запит при некоректному введенні.
     *
     * @param prompt текст підказки
     * @return введене дійсне число
     */
    private static double readDouble(String prompt) {
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

    private static <T extends Enum<T>> T readEnum(T[] values, String label) {
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