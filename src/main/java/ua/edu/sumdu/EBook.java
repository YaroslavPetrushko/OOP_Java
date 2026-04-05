package ua.edu.sumdu;

import java.util.Objects;

public class EBook extends Book{
    /** Мінімально допустимий розмір файлу (у мегабайтах). */
    private static final double MIN_FILE_SIZE = 0.01;

    private String fileFormat;
    private double fileSizeMB;
    private String downloadUrl;

    // ---------------------------------------------------------------
    // Конструктори
    // ---------------------------------------------------------------

    // Основний конструктор
    public EBook(String title, String author, int year, double price,
                 Genre genre, int pages,
                 String fileFormat, double fileSizeMB, String downloadUrl) {
        super(title, author, year, price, genre, pages);
        setFileFormat(fileFormat);
        setFileSizeMB(fileSizeMB);
        setDownloadUrl(downloadUrl);
    }

    // Конструктор копіювання
    public EBook(EBook other) {
        super(other);
        this.fileFormat   = other.fileFormat;
        this.fileSizeMB   = other.fileSizeMB;
        this.downloadUrl  = other.downloadUrl;
    }

    // ---------------------------------------------------------------
    // Геттери та сетери
    // ---------------------------------------------------------------

    public String getFileFormat() { return fileFormat; }

    public void setFileFormat(String fileFormat) {
        if (fileFormat == null || fileFormat.isBlank()) {
            throw new InvalidBookDataException("File format cannot be empty.");
        }
        this.fileFormat = fileFormat.trim().toUpperCase();
    }

    public double getFileSizeMB() { return fileSizeMB; }

    public void setFileSizeMB(double fileSizeMB) {
        if (fileSizeMB < MIN_FILE_SIZE) {
            throw new InvalidBookDataException(
                    "File size must be at least " + MIN_FILE_SIZE + " MB.");
        }
        this.fileSizeMB = fileSizeMB;
    }

    public String getDownloadUrl() { return downloadUrl; }

    public void setDownloadUrl(String downloadUrl) {
        if (downloadUrl == null || downloadUrl.isBlank()) {
            throw new InvalidBookDataException("Download URL cannot be empty.");
        }
        this.downloadUrl = downloadUrl.trim();
    }

    @Override
    public String toString() {
        return String.format(
                "[EBook] \"%s\" by %s | %d | $%.2f | %s | %d pages" +
                        " | %s | %.1f MB | %s",
                getTitle(), getAuthor(), getYear(), getPrice(), getGenre(), getPages(),
                fileFormat, fileSizeMB, downloadUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        EBook eBook = (EBook) o;
        return Double.compare(fileSizeMB, eBook.fileSizeMB) == 0
                && Objects.equals(fileFormat,  eBook.fileFormat)
                && Objects.equals(downloadUrl, eBook.downloadUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileFormat, fileSizeMB, downloadUrl);
    }
}
