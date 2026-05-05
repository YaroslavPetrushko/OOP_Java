package ua.edu.sumdu.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ua.edu.sumdu.model.*;
import ua.edu.sumdu.storage.JsonBookStorage;
import ua.edu.sumdu.storage.TxtBookStorage;

import java.util.UUID;

/**
 * Контролер JavaFX GUI для Book Manager.
 *
 * <p>Прив'язується до {@code main.fxml} через атрибут {@code fx:controller}.
 * Всі поля, позначені {@link FXML}, ін'єктуються автоматично при завантаженні FXML.
 * Після ін'єкції викликається {@link #initialize()}.</p>
 *
 * <h2>Відповідальність</h2>
 * <ul>
 *   <li>Завантаження та збереження бібліотеки</li>
 *   <li>Оновлення {@link ListView} через Observable-список</li>
 *   <li>Динамічна побудова полів форми при зміні типу книги</li>
 *   <li>Додавання книг до бібліотеки</li>
 *   <li>Пошук книги за UUID</li>
 * </ul>
 */
public class MainController {

    // ================================================================
    // Дані
    // ================================================================

    private Library          library;
    private TxtBookStorage   txtStorage;
    private JsonBookStorage  jsonStorage;

    private final ObservableList<BookEntry> observableEntries =
            FXCollections.observableArrayList();

    // ================================================================
    // @FXML — ін'єктовані поля
    // ================================================================

    // ---- Header ----
    @FXML private Button btnSave;

    // ---- Left panel ----
    @FXML private Label    listHeading;
    @FXML private ListView<BookEntry> bookListView;

    // ---- Add Book form — shared ----
    @FXML private ComboBox<String> cbType;
    @FXML private TextField tfTitle;
    @FXML private TextField tfAuthor;
    @FXML private TextField tfYear;
    @FXML private TextField tfPrice;
    @FXML private ComboBox<Genre> cbGenre;
    @FXML private TextField tfPages;
    @FXML private TextField tfQuantity;

    /** Контейнер для динамічних полів (змінюється при зміні cbType). */
    @FXML private VBox specificFieldsBox;

    // ---- Search ----
    @FXML private TextField tfUuidSearch;
    @FXML private TextArea  taSearchResult;

    // ---- Status ----
    @FXML private Label statusLabel;

    // ---- Динамічні поля (не у FXML, створюються програмно) ----

    // EBook
    private TextField tfFileFormat, tfFileSizeMB, tfDownloadUrl;
    // AudioBook
    private TextField tfNarrator, tfDurationMinutes, tfAudioFormat;
    // PaperBook / RareBook
    private TextField tfPublisher, tfEdition, tfWeightGrams;
    // RareBook extra
    private ComboBox<BookCondition> cbCondition;
    private TextField tfEstimatedValue, tfAcquisitionYear;

    // ================================================================
    // Ініціалізація
    // ================================================================

    /**
     * Викликається JavaFX після ін'єкції всіх @FXML-полів.
     *
     * <p>Виконує:</p>
     * <ol>
     *   <li>Підключення сховищ та завантаження даних</li>
     *   <li>Налаштування ComboBox-ів (type, genre)</li>
     *   <li>Налаштування cell factory для ListView</li>
     *   <li>Початкову побудову специфічних полів форми</li>
     * </ol>
     */
    @FXML
    public void initialize() {
        // ---- Сховища та бібліотека ----
        library     = new Library("City Library", "Main St. 1");
        txtStorage  = new TxtBookStorage("input.txt");
        jsonStorage = new JsonBookStorage("input.json");
        loadData();

        // ---- ComboBox: тип книги ----
        cbType.setItems(FXCollections.observableArrayList(
                "EBook", "AudioBook", "PaperBook", "RareBook"));
        cbType.setValue("EBook");

        // ---- ComboBox: жанр ----
        cbGenre.setItems(FXCollections.observableArrayList(Genre.values()));
        cbGenre.setValue(Genre.FICTION);

        // ---- ListView: cell factory ----
        // Формат: "3x [EBook] "Title" by Author | uuid: 8-hex"
        bookListView.setItems(observableEntries);
        bookListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(BookEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getQuantity() + "x "
                            + item.getBook().getShortInfo());
                }
            }
        });

        // ---- Специфічні поля для початкового типу (EBook) ----
        rebuildSpecificFields();

        // ---- Оновлення списку ----
        refreshList();
        setStatus("Loaded " + library.getEntryCount()
                + " book(s). Click a row to copy UUID to the search field.", false);
    }

    // ================================================================
    // Завантаження / збереження
    // ================================================================

    /** Завантажує дані при старті: спочатку TXT, якщо порожньо — JSON. */
    private void loadData() {
        txtStorage.load(library);
        if (library.getEntryCount() == 0) {
            jsonStorage.load(library);
        }
    }

    /** Зберігає колекцію в обидва формати. Викликається при закритті вікна та кнопкою. */
    public void saveAll() {
        txtStorage.save(library);
        jsonStorage.save(library);
    }

    /** Оновлює Observable-список з поточного стану бібліотеки. */
    private void refreshList() {
        observableEntries.clear();
        for (int i = 0; i < library.getEntryCount(); i++) {
            observableEntries.add(library.getEntry(i));
        }
        listHeading.setText("Library  (" + library.getEntryCount() + " titles)");
    }

    // ================================================================
    // Обробники подій (@FXML)
    // ================================================================

    /**
     * Кнопка «💾 Save» — зберігає та оновлює статус.
     */
    @FXML
    private void handleSave() {
        saveAll();
        setStatus("Saved " + library.getEntryCount() + " book(s).", false);
    }

    /**
     * Клік на рядок ListView → копіює UUID книги в поле пошуку.
     */
    @FXML
    private void handleListClick() {
        BookEntry selected = bookListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            tfUuidSearch.setText(selected.getBook().getUuid().toString());
            setStatus("UUID copied — click 'Find' to look up.", false);
        }
    }

    /**
     * Зміна типу книги в ComboBox → перебудовує специфічні поля форми.
     */
    @FXML
    private void handleTypeChange() {
        rebuildSpecificFields();
    }

    /**
     * Кнопка «Add Book» — зчитує форму, створює об'єкт, додає до бібліотеки.
     */
    @FXML
    private void handleAddBook() {
        try {
            String title  = requireNonEmpty(tfTitle,  "Title");
            String author = requireNonEmpty(tfAuthor, "Author");
            int    year   = parseIntField(tfYear,  "Year");
            double price  = parseDoubleField(tfPrice, "Price");
            Genre  genre  = cbGenre.getValue();
            int    pages  = parseIntField(tfPages, "Pages");
            int    qty    = parseIntField(tfQuantity, "Quantity");

            Book book = switch (cbType.getValue()) {
                case "EBook" -> {
                    String fmt = requireNonEmpty(tfFileFormat, "File format");
                    double sz  = parseDoubleField(tfFileSizeMB, "File size");
                    String url = requireNonEmpty(tfDownloadUrl, "Download URL");
                    yield new EBook(title, author, year, price, genre, pages, fmt, sz, url);
                }
                case "AudioBook" -> {
                    String nar  = requireNonEmpty(tfNarrator,    "Narrator");
                    int    dur  = parseIntField(tfDurationMinutes, "Duration");
                    String afmt = requireNonEmpty(tfAudioFormat,  "Audio format");
                    yield new AudioBook(title, author, year, price, genre, pages, nar, dur, afmt);
                }
                case "PaperBook" -> {
                    String pub = requireNonEmpty(tfPublisher,  "Publisher");
                    int    ed  = parseIntField(tfEdition,      "Edition");
                    double wt  = parseDoubleField(tfWeightGrams, "Weight");
                    yield new PaperBook(title, author, year, price, genre, pages, pub, ed, wt);
                }
                case "RareBook" -> {
                    String        pub  = requireNonEmpty(tfPublisher,    "Publisher");
                    int           ed   = parseIntField(tfEdition,        "Edition");
                    double        wt   = parseDoubleField(tfWeightGrams, "Weight");
                    BookCondition cond = cbCondition.getValue();
                    double        val  = parseDoubleField(tfEstimatedValue,  "Est. value");
                    int           acq  = parseIntField(tfAcquisitionYear, "Acq. year");
                    yield new RareBook(title, author, year, price, genre, pages,
                            pub, ed, wt, cond, val, acq);
                }
                default -> throw new InvalidBookDataException("Unknown type.");
            };

            library.addNewBook(book, qty);
            refreshList();
            clearAddForm();
            setStatus("✓ Added: " + book.getShortInfo(), false);

        } catch (InvalidBookDataException e) {
            setStatus("✗ " + e.getMessage(), true);
        }
    }

    /**
     * Кнопка «Find» або Enter у полі пошуку UUID.
     */
    @FXML
    private void handleUuidSearch() {
        String input = tfUuidSearch.getText().trim();
        BookEntry entry = null;
        boolean isFullMatch = false;

        if (input.isEmpty()) {
            taSearchResult.setText("⚠  Please enter a UUID.");
            setStatus("UUID field is empty.", true);
            return;
        }
        // Захист від занадто коротких запитів для часткового пошуку
        if (input.length() < 4) {
            taSearchResult.setText("✗  Search query is too short.\n\n"
                    + "Please enter a full UUID or at least 4 characters of it.");
            setStatus("✗ Invalid input length.", true);
            return;
        }

        // Перевірка формату
        try {
            UUID.fromString(input);
            entry = library.findByFullUuid(input);
            isFullMatch = true;
        } catch (IllegalArgumentException e) {
            // Якщо зловили помилку, значить користувач ввів короткий UUID
            entry = library.findByShortUuid(input);
        }


        if (entry != null) {
            // Знайшли книгу
            taSearchResult.setText(buildFullInfo(entry.getBook(), entry.getQuantity()));
            setStatus("✓ Found: " + entry.getBook().getShortInfo(), false);

            String matchType = isFullMatch ? "Full UUID match" : "Partial UUID match";
            setStatus("✓ Success (" + matchType + ").", false);
        } else {
            // Нічого не знайшли
            taSearchResult.setText(
                    "✗  Not found.\n\n"
                            + "No book with UUID:\n  " + input + "\n\n"
                            + "Make sure the library is up to date.");
            setStatus("✗ Not found for UUID: " + input.substring(0, 8) + "…", true);
        }
    }

    // ================================================================
    // Динамічна форма
    // ================================================================

    /**
     * Перебудовує блок специфічних полів відповідно до обраного типу книги.
     *
     * <p>Очищує {@code specificFieldsBox} та заповнює потрібними рядками форми.
     * Кожен рядок — {@link HBox} з лейблом і текстовим полем (або ComboBox).</p>
     */
    private void rebuildSpecificFields() {
        specificFieldsBox.getChildren().clear();
        String type = cbType.getValue();
        if (type == null) return;

        switch (type) {
            case "EBook" -> {
                tfFileFormat  = field("EPUB / PDF / MOBI");
                tfFileSizeMB  = field("e.g. 5.2");
                tfDownloadUrl = field("https://…");
                specificFieldsBox.getChildren().addAll(
                        row("Format:",   tfFileFormat),
                        row("Size MB:", tfFileSizeMB),
                        row("URL:",      tfDownloadUrl));
            }
            case "AudioBook" -> {
                tfNarrator        = field("Full name");
                tfDurationMinutes = field("e.g. 480  (minutes)");
                tfAudioFormat     = field("MP3 / AAC / FLAC");
                specificFieldsBox.getChildren().addAll(
                        row("Narrator:", tfNarrator),
                        row("Duration:", tfDurationMinutes),
                        row("Format:",   tfAudioFormat));
            }
            case "PaperBook" -> {
                tfPublisher   = field("Publisher name");
                tfEdition     = field("e.g. 1");
                tfWeightGrams = field("e.g. 450  (grams)");
                specificFieldsBox.getChildren().addAll(
                        row("Publisher:", tfPublisher),
                        row("Edition:",   tfEdition),
                        row("Weight g:",  tfWeightGrams));
            }
            case "RareBook" -> {
                tfPublisher      = field("Publisher name");
                tfEdition        = field("e.g. 1");
                tfWeightGrams    = field("e.g. 800  (grams)");
                cbCondition      = new ComboBox<>();
                cbCondition.setItems(
                        FXCollections.observableArrayList(BookCondition.values()));
                cbCondition.setValue(BookCondition.GOOD);
                cbCondition.getStyleClass().add("combo-field");
                cbCondition.setMaxWidth(Double.MAX_VALUE);
                tfEstimatedValue  = field("e.g. 5000.00  (USD)");
                tfAcquisitionYear = field("e.g. 1990");
                specificFieldsBox.getChildren().addAll(
                        row("Publisher:",  tfPublisher),
                        row("Edition:",    tfEdition),
                        row("Weight g:",   tfWeightGrams),
                        row("Condition:",  cbCondition),
                        row("Est. value:", tfEstimatedValue),
                        row("Acq. year:",  tfAcquisitionYear));
            }
        }
    }

    /** Очищає всі поля форми та повертає специфічний блок у початковий стан. */
    private void clearAddForm() {
        tfTitle.clear();
        tfAuthor.clear();
        tfYear.clear();
        tfPrice.clear();
        tfPages.clear();
        tfQuantity.clear();
        cbGenre.setValue(Genre.FICTION);
        rebuildSpecificFields();
    }

    // ================================================================
    // Форматування результату пошуку
    // ================================================================

    /**
     * Будує повний текстовий опис знайденої книги для відображення у TextArea.
     *
     * @param b        знайдена книга
     * @param quantity кількість примірників
     * @return форматований рядок
     */
    private String buildFullInfo(Book b, int quantity) {
        StringBuilder sb = new StringBuilder();
        sb.append("✓  Found!\n");
        sb.append("─".repeat(48)).append("\n");
        sb.append(String.format("UUID:     %s%n", b.getUuid()));
        sb.append(String.format("Type:     %s%n", b.getClass().getSimpleName()));
        sb.append(String.format("Title:    %s%n", b.getTitle()));
        sb.append(String.format("Author:   %s%n", b.getAuthor()));
        sb.append(String.format("Year:     %d%n", b.getYear()));
        sb.append(String.format("Price:    $%.2f%n", b.getPrice()));
        sb.append(String.format("Genre:    %s%n", b.getGenre()));
        sb.append(String.format("Pages:    %d%n", b.getPages()));
        sb.append("─".repeat(48)).append("\n");
        sb.append(buildTypeSpecificInfo(b));
        sb.append("─".repeat(48)).append("\n");
        sb.append(String.format("Quantity: %d copy(ies)%n", quantity));
        return sb.toString();
    }

    /**
     * Повертає рядок із полями, специфічними для типу книги.
     *
     * @param b книга будь-якого підкласу
     * @return форматований рядок
     */
    private String buildTypeSpecificInfo(Book b) {
        if (b instanceof RareBook rb) {
            return String.format(
                    "Publisher: %s%nEdition:   %d%nWeight:    %.0f g%n"
                            + "Condition: %s%nEst.Value: $%.2f%nAcquired:  %d%n",
                    rb.getPublisher(), rb.getEdition(), rb.getWeightGrams(),
                    rb.getCondition(), rb.getEstimatedValueUSD(), rb.getAcquisitionYear());
        }
        if (b instanceof PaperBook pb) {
            return String.format(
                    "Publisher: %s%nEdition:   %d%nWeight:    %.0f g%n",
                    pb.getPublisher(), pb.getEdition(), pb.getWeightGrams());
        }
        if (b instanceof EBook eb) {
            return String.format(
                    "Format:    %s%nSize:      %.1f MB%nURL:       %s%n",
                    eb.getFileFormat(), eb.getFileSizeMB(), eb.getDownloadUrl());
        }
        if (b instanceof AudioBook ab) {
            return String.format(
                    "Narrator:  %s%nDuration:  %d min%nFormat:    %s%n",
                    ab.getNarrator(), ab.getDurationMinutes(), ab.getAudioFormat());
        }
        return "";
    }

    // ================================================================
    // Статус
    // ================================================================

    /**
     * Встановлює текст і колір у рядку статусу.
     *
     * @param msg     повідомлення
     * @param isError {@code true} — червоний, {@code false} — зелений
     */
    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.getStyleClass().setAll(isError ? "status-error" : "status-ok");
    }

    // ================================================================
    // UI-хелпери для динамічної форми
    // ================================================================

    /**
     * Створює стилізований TextField із підказкою.
     *
     * @param prompt текст підказки
     * @return {@link TextField}
     */
    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("text-field-dark");
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    /**
     * Будує рядок форми: лейбл (100 px) + контрол.
     *
     * @param labelText текст лейблу
     * @param control   будь-який {@link Control}
     * @return {@link HBox} рядку форми
     */
    private HBox row(String labelText, Control control) {
        Label lbl = new Label(labelText);
        lbl.setMinWidth(100);
        lbl.getStyleClass().add("form-label");
        HBox.setHgrow(control, Priority.ALWAYS);
        control.setMaxWidth(Double.MAX_VALUE);
        HBox hbox = new HBox(8, lbl, control);
        hbox.getStyleClass().add("form-row");
        return hbox;
    }

    // ================================================================
    // Хелпери валідації вводу
    // ================================================================

    /**
     * Зчитує непорожній рядок або кидає {@link InvalidBookDataException}.
     *
     * @param tf        поле введення
     * @param fieldName назва поля для повідомлення
     * @return непорожній рядок після trim
     */
    private String requireNonEmpty(TextField tf, String fieldName) {
        String v = tf.getText().trim();
        if (v.isEmpty()) {
            throw new InvalidBookDataException(fieldName + " cannot be empty.");
        }
        return v;
    }

    /**
     * Парсить ціле число або кидає {@link InvalidBookDataException}.
     *
     * @param tf        поле введення
     * @param fieldName назва поля
     * @return ціле число
     */
    private int parseIntField(TextField tf, String fieldName) {
        try {
            return Integer.parseInt(tf.getText().trim());
        } catch (NumberFormatException e) {
            throw new InvalidBookDataException(fieldName + " must be a whole number.");
        }
    }

    /**
     * Парсить дійсне число (підтримує кому і крапку) або кидає виключення.
     *
     * @param tf        поле введення
     * @param fieldName назва поля
     * @return double-значення
     */
    private double parseDoubleField(TextField tf, String fieldName) {
        try {
            return Double.parseDouble(tf.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new InvalidBookDataException(
                    fieldName + " must be a number (e.g. 19.99).");
        }
    }
}