package ua.edu.sumdu;

/**
 * Точка входу в програму «Book Manager».
 *
 * <p>Клас є виключно драйвером: створює {@link BookManager} та
 * передає йому управління. Вся бізнес-логіка, введення/виведення
 * та файлові операції реалізовані у {@link BookManager}.</p>
 */
public class Main {

    /**
     * Запускає програму.
     *
     * @param args аргументи командного рядка (не використовуються)
     */
    public static void main(String[] args) {
        new BookManager().run();
    }
}