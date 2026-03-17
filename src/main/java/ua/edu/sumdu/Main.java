package ua.edu.sumdu;

import java.util.Scanner;

public class Main {
    static void main(String[] args) {
       Scanner scanner = new Scanner(System.in);

       Book[] books = new Book[5];

       // Вводимо дані про кожну книгу: назва, автор, рік, ціна
       for(int i = 0; i < books.length; i++){
           //Input data from keyboard
           System.out.println("Enter Book " + (i + 1));

           System.out.print("Title: ");
           String title = scanner.nextLine();

           System.out.print("Author: ");
           String author = scanner.nextLine();

           System.out.print("Year: ");
           int year = scanner.nextInt();

           System.out.print("Price: ");
           Double price = scanner.nextDouble();
           scanner.nextLine();

           books[i] = new Book(title, author, year, price);
       }

        // Виводимо масив на екран
       System.out.println("\nBooks list:");

       for(Book book : books){
           System.out.println(book.toString());
       }

       scanner.close();
    }
}
