package ua.edu.sumdu;

import java.util.ArrayList;
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

    /** Єдина колекція для об'єктів усіх типів ієрархії. */
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
                case 1 -> addBook();
                case 2 -> addEBook();
                case 3 -> addPaperBook();
                case 4 -> printAllBooks();
                case 5 -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("  [!] Please enter a number from 1 to 5.\n");
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
        System.out.println("1. Add new book");
        System.out.println("2. Add EBook");
        System.out.println("3. Add PaperBook");
        System.out.println("4. Show all books");
        System.out.println("5. Exit");
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
    private static void addBook() {
        System.out.println("\n--- Add new Book ---");
        try {
            String title    = readNonEmptyString("Title:  ");
            String author   = readNonEmptyString("Author: ");
            int    year     = readInt("Year:   ");
            double price    = readDouble("Price:  ");
            Genre genre     = readGenre();
            int    pages    = readInt("Pages:  ");

            books.add(new Book(title, author, year, price, genre, pages));
            System.out.println("  [OK] Book added successfully.\n");

        } catch (InvalidBookDataException e) {
            System.out.println("  [!] Validation error: " + e.getMessage() + "\n");
        }
    }

    // ---------------------------------------------------------------
    // Пункт 2 — Додавання EBook
    // ---------------------------------------------------------------

    private static void addEBook() {}

    // ---------------------------------------------------------------
    // Пункт 3 — Додавання PaperBook
    // ---------------------------------------------------------------
    private static void addPaperBook() {}

    // ---------------------------------------------------------------
    // Пункт 4: Виведення всіх книг
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
            Book book = books.get(i);
            System.out.println("  " + (i + 1) + ". " + book);
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

    private static Genre readGenre() {
        return null;
    }

}