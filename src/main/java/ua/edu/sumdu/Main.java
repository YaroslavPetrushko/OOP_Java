package ua.edu.sumdu;

import java.util.Scanner;

public class Main {
    private static final int BOOKNUMBER=5; // Кількість книг у масиві - 5

    private static final Scanner scanner = new Scanner(System.in);

    static void main(String[] args) {
       Scanner scanner = new Scanner(System.in);

        // Масив з книгами
       Book[] books = new Book[BOOKNUMBER];

       // Вводимо дані про кожну книгу: назва, автор, рік, ціна
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

    private static void createBook(int i, Book [] books){
        System.out.println("\n--- New Book ---");
        // Введення даних з клавіатури
        try {
            String title  = readNonEmptyString("Title:  ");
            String author = readNonEmptyString("Author: ");
            int    year   = readInt("Year:   ");
            double price  = readDouble("Price:  ");
            String genre  = readNonEmptyString("Genre:  ");
            int    pages  = readInt("Pages:  ");

            books[i] = new Book(title, author, year, price, genre, pages);

            System.out.println("  [OK] Book added successfully.\n");

        } catch (InvalidBookDataException e) {
            System.out.println("  [!] Validation error: " + e.getMessage() + "\n");
        }
    }

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
