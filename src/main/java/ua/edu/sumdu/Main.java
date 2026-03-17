package ua.edu.sumdu;

import java.util.Scanner;

public class Main {
    static void main(String[] args) {
       Scanner scanner = new Scanner(System.in);

       Book[] books = new Book[5];

       // Вводимо дані про кожну книгу: назва, автор, рік, ціна
       for(int i = 0; i < books.length; i++){
           //Input data from keyboard
       }

        // Виводимо масив на екран
       System.out.println("\nBooks list:");

       for(Book book : books){
           System.out.println(book);
       }

       scanner.close();
    }
}
