package ua.edu.sumdu;

import java.util.Scanner;

/**
 * Точка входу в програму.
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

    /** Спільний Scanner для всієї програми. */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Запускає консольний цикл меню.
     *
     * @param args аргументи командного рядка (не використовуються)
     */
    public static void main(String[] args) {
        System.out.println("=== Book Manager ===");

        Library library = initLibrary();
        System.out.println(" * Library \"" + library.getName() + "\" created.\n");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1 -> addBook(library);
                case 2 -> printAllBooks(library);
                case 3 -> copyBook(library);
                case 4 -> removeBook(library);
                case 5 -> showInstanceCount();
                case 6 -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("  [!] Unknown option. Please enter a number from 1 to 6.\n");
            }
        }

        scanner.close();
    }

    // ---------------------------------------------------------------
    // Ініціалізація бібліотеки
    // ---------------------------------------------------------------

    private static Library initLibrary() {
        System.out.println("\n--- Library Setup ---");
        while (true) {
            try {
                String name     = readNonEmptyString("Library name:    ");
                String address  = readNonEmptyString("Library address: ");
                return new Library(name, address);
            } catch (InvalidBookDataException e) {
                System.out.println("  [!] " + e.getMessage() + "\n");
            }
        }
    }

    // ---------------------------------------------------------------
    // Меню
    // ---------------------------------------------------------------

    /**
     * Виводить головне меню на екран.
     */
    private static void printMenu() {
        System.out.println("--------------------");
        System.out.println("1. Add new book");
        System.out.println("2. Show all books");
        System.out.println("3. Copy book (copy constructor)");
        System.out.println("4. Remove book");
        System.out.println("5. Show total Book instances created");
        System.out.println("6. Exit");
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
    // Пункт 1: Додавання книги
    // ---------------------------------------------------------------

    /**
     * Інтерактивно зчитує дані нової книги та додає її до списку.
     * При будь-якій помилці введення виводить повідомлення і повертається до меню.
     */
    private static void addBook(Library library) {
        System.out.println("\n--- Add new Book ---");
        try {
            String title    = readNonEmptyString("Title:  ");
            String author   = readNonEmptyString("Author: ");
            int    year     = readInt("Year:   ");
            double price    = readDouble("Price:  ");
            Genre genre     = readEnum("Genre",Genre.values());
            int    pages    = readInt("Pages:  ");

            Book book = new Book(title, author, year, price, genre, pages);
            library.addBook(book);
            System.out.println("  [OK] Book added successfully. Total Book objects ever created: "
                    + Book.getInstanceCount() + "\n");

        } catch (InvalidBookDataException e) {
            System.out.println("  [!] Validation error: " + e.getMessage() + "\n");
        }
    }

    // ---------------------------------------------------------------
    // Пункт 2: Виведення всіх книг
    // ---------------------------------------------------------------

    /**
     * Виводить усі збережені книги у форматованому вигляді.
     * Якщо список порожній — повідомляє про це.
     */
    private static void printAllBooks(Library library) {
        System.out.println("\n--- Book List ---");
        if (library.getBookCount() == 0) {
            System.out.println("  (library is empty - no books added yet)\n");
            return;
        }
        System.out.println("Total number of books: " + Book.getInstanceCount()+"\n");
        System.out.println(library);
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Пункт 3: Копіювання книги
    // ---------------------------------------------------------------

    private static void copyBook(Library library) {
        System.out.println("\n--- Copy Book ---");
        if (library.getBookCount() == 0) {
            System.out.println("  (library is empty - add book first!)\n");
            return;
        }
        printNumberedList(library);

        try {
            int index = readInt("Book number to copy: ") - 1;
            Book original   = library.getBook(index);
            Book copy       = new Book(original);          // використовуємо конструктор копіювання
            library.addBook(copy);
            System.out.println("  [OK] Copy created: " + copy);
            System.out.println("  Total Book objects ever created: "
                    + Book.getInstanceCount() + "\n");
        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    // ---------------------------------------------------------------
    // Пункт 4: Видалення книги
    // ---------------------------------------------------------------

    private static void removeBook(Library library) {
        System.out.println("\n--- Remove Book ---");
        if (library.getBookCount() == 0) {
            System.out.println("  (library is empty)\n");
            return;
        }
        printNumberedList(library);

        try {
            int index = readInt("Book number to remove: ") - 1;
            library.removeBook(index);
            System.out.println("  [OK] Book removed.\n");
        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    // ---------------------------------------------------------------
    // Пункт 5: Статичний лічильник
    // ---------------------------------------------------------------

    private static void showInstanceCount() {
        System.out.println("\n--- Static Counter ---");
        System.out.println("  Total Book objects created (including copies): "
                + Book.getInstanceCount());
        System.out.println("  Books currently in library: "
                + "see list");
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Допоміжні методи зчитування
    // ---------------------------------------------------------------

    /**
     * Виводить список книг.
     */
    private static void printNumberedList(Library library) {
        for (int i = 0; i < library.getBookCount(); i++) {
            System.out.println("  " + (i + 1) + ". " + library.getBook(i).getTitle()
                    + " / " + library.getBook(i).getAuthor());
        }
    }

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

    // Відображає меню вибору жанру та повертає обране значення enum
    private static <T extends Enum<T>> T readEnum(String label, T[] values) {
        System.out.println("  Select " + label + ":");
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