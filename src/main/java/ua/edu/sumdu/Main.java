package ua.edu.sumdu;

import java.util.Scanner;

/**
 * Точка входу в програму.
 *
 * <p>Створює масив з 5 книгами, користувач вводить дані з клавіатури,
 * після чого виводить масив в термінал як форматований рядок.
 * Усі помилки введення (нечислові дані, порожні рядки, некоректні значення)
 * перехоплюються та повідомляються користувачу без завершення програми.</p>
 */
public class Main {

    /** Кількість книг у масиві. За замовчуванням = 5. */
    private static final int BOOKNUMBER=5;

    /** Спільний Scanner для всієї програми.  */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Запускає цикл та викликає метод для створення книг. Виводить масив книг в термінал.
     *
     * @param args аргументи командного рядка (не використовуються)
     */
    public static void main(String[] args) {
        // Масив з книгами
       Book[] books = new Book[BOOKNUMBER];

       // Вводимо дані про кожну книгу: назва, автор, рік, ціна, жанр, кількість сторінок
       for(int i = 0; i < books.length; i++){
       createBook(i, books);
       }

        // Виводимо масив на екран
       System.out.println("\nBooks list:");

       for(Book book : books){
           System.out.println(book.toString());
       }

       scanner.close();
    }

    // ---------------------------------------------------------------
    // Створення книги
    // ---------------------------------------------------------------

    /**
     * Зчитує дані нової книги, що вводить користувач з клавіатури та додає її до масиву.
     * При будь-якій помилці введення виводить повідомлення і користувач заново вводить дані.
     *
     * @param i - число, місце поточної книги в масиві
     * @param books - масив Book[], для додавання даних про книгу до масиву
     */
    private static void createBook(int i, Book [] books){

        while(true) {

            System.out.println("\n--- New Book - #" + (i + 1) + "/5 ---");
            // Введення даних з клавіатури
            try {
                String title = readNonEmptyString("Title:  ");
                String author = readNonEmptyString("Author: ");
                int year = readInt("Year:   ");
                double price = readDouble("Price:  ");
                String genre = readNonEmptyString("Genre:  ");
                int pages = readInt("Pages:  ");

                // Додаємо дані до масиву
                books[i] = new Book(title, author, year, price, genre, pages);

                System.out.println("  [OK] Book added successfully.\n");

                break; // Вихід з циклу якщо книгу успішно додано

            } catch (InvalidBookDataException e) {
                // Ловимо помилку валідації
                System.out.println("  [!] Validation error: " + e.getMessage() + "\n");
                System.out.println("  Please re-enter the book.\n");
            }
        }
    }

    // ---------------------------------------------------------------
    // Допоміжні методи зчитування
    // ---------------------------------------------------------------

    /**
     * Зчитує непорожній рядок із клавіатури.
     * Повторює запит, доки користувач не введе хоча б один непробільний символ.
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
}
