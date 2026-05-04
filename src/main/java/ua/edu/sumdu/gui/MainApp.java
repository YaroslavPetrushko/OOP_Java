package ua.edu.sumdu.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ua.edu.sumdu.model.*;
import ua.edu.sumdu.storage.JsonBookStorage;
import ua.edu.sumdu.storage.TxtBookStorage;

import java.util.UUID;

/**
 * JavaFX GUI для Book Manager.
 */
public class MainApp extends Application {

    // ----------------------------------------------------------------
    // Дані
    // ----------------------------------------------------------------

    private Library        library;
    private TxtBookStorage txtStorage;
    private JsonBookStorage jsonStorage;

    // Observable-список для ListView
    private final ObservableList<BookEntry> observableEntries =
            FXCollections.observableArrayList();

    // ----------------------------------------------------------------
    // Ліва панель
    // ----------------------------------------------------------------

    private ListView<BookEntry> bookListView;

    // ----------------------------------------------------------------
    // Форма «Додати книгу»
    // ----------------------------------------------------------------

    // Вибір типу книги
    private ComboBox<String> cbType;

    // Загальні поля
    private TextField tfTitle, tfAuthor, tfYear, tfPrice, tfPages;
    private ComboBox<Genre> cbGenre;

    // Контейнер для полів
    private VBox specificFieldsBox;

    // EBook
    private TextField tfFileFormat, tfFileSizeMB, tfDownloadUrl;
    // AudioBook
    private TextField tfNarrator, tfDurationMinutes, tfAudioFormat;
    // PaperBook / RareBook (спільні)
    private TextField tfPublisher, tfEdition, tfWeightGrams;
    // RareBook (додаткові)
    private ComboBox<BookCondition> cbCondition;
    private TextField tfEstimatedValue, tfAcquisitionYear;

    // ----------------------------------------------------------------
    // Пошук за UUID
    // ----------------------------------------------------------------

    private TextField tfUuidSearch;
    private TextArea  taSearchResult;

    // ----------------------------------------------------------------
    // Рядок статусу
    // ----------------------------------------------------------------

    private Label statusLabel;

    // ================================================================
    // Точка входу JavaFX
    // ================================================================

    @Override
    public void start(Stage stage) {
        library     = new Library("City Library", "Main St. 1");
        txtStorage  = new TxtBookStorage("input.txt");
        jsonStorage = new JsonBookStorage("input.json");
        loadData();

        // ---- Root layout ----
        BorderPane root = new BorderPane();
        root.setTop(buildHeader(stage));
        root.setCenter(buildCenter());
        root.setBottom(buildStatusBar());

        Scene scene = new Scene(root, 1180, 730);
        stage.setTitle("Book Manager — UUID Edition");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setOnCloseRequest(e -> saveAll());
        stage.show();

        refreshList();
        setStatus("Loaded " + library.getEntryCount() + " book(s). "
                + "Click a row to copy UUID into the search field.", false);
    }

    // ================================================================
    // Допоміжні методи роботи з даними
    // ================================================================

    private void loadData() {
        txtStorage.load(library);
        if (library.getEntryCount() == 0) {
            jsonStorage.load(library);
        }
    }

    private void saveAll() {
        txtStorage.save(library);
        jsonStorage.save(library);
    }

    private void refreshList() {
        observableEntries.clear();
        for (int i = 0; i < library.getEntryCount(); i++) {
            observableEntries.add(library.getEntry(i));
        }
    }

    // ================================================================
    // Побудова GUI
    // ================================================================

    // ----------------------------------------------------------------
    // Header
    // ----------------------------------------------------------------

    private HBox buildHeader(Stage stage) {
        Label title = new Label("Book Manager — GUI");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSave = new Button("Save");
        btnSave.setOnAction(e -> {
            saveAll();
            setStatus("Saved " + library.getEntryCount() + " book(s).", false);
        });

        HBox header = new HBox(14, title, spacer, btnSave);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 20, 10, 20));
        return header;
    }

    // ----------------------------------------------------------------
    // Center: SplitPane (list | tabs)
    // ----------------------------------------------------------------

    private SplitPane buildCenter() {
        SplitPane split = new SplitPane(buildLeftPanel(), buildRightPanel());
        split.setDividerPositions(0.37);
        return split;
    }

    // ----------------------------------------------------------------
    // Ліва панель: список книг
    // ----------------------------------------------------------------

    private VBox buildLeftPanel() {
        Label heading = sectionLabel("Library  (" + library.getEntryCount() + " titles)");

        bookListView = new ListView<>(observableEntries);
        bookListView.setStyle("-fx-border-radius: 6; -fx-background-radius: 6;");

        // Кастомні клітинки: короткий вигляд книги + кількість примірників
        bookListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(BookEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getBook().getShortInfo()
                            + "   ×" + item.getQuantity());
                    setStyle(
                            "-fx-font-size: 12px; -fx-padding: 6 10 6 10;");
                }
            }
        });

        // Підставляємо UUID у поле пошуку
        bookListView.setOnMouseClicked(e -> {
            BookEntry selected = bookListView.getSelectionModel().getSelectedItem();
            if (selected != null && tfUuidSearch != null) {
                tfUuidSearch.setText(selected.getBook().getUuid().toString());
                setStatus("UUID copied to search field. Click 'Find' to look up.",false);
            }
        });

        VBox.setVgrow(bookListView, Priority.ALWAYS);

        VBox box = new VBox(10, heading, bookListView);
        box.setPadding(new Insets(16));
        return box;
    }

    // ----------------------------------------------------------------
    // Права панель: вкладки
    // ----------------------------------------------------------------

    private TabPane buildRightPanel() {
        Tab addTab    = new Tab(" Add Book", buildAddBookPanel());
        Tab searchTab = new Tab(" Search Book", buildSearchPanel());
        addTab.setClosable(false);

        TabPane tabs = new TabPane(addTab, searchTab);
        tabs.setTabMinWidth(150);
        return tabs;
    }

    // ----------------------------------------------------------------
    // Вкладка «Add Book»
    // ----------------------------------------------------------------

    private ScrollPane buildAddBookPanel() {
        Label heading = sectionLabel("New Book");

        cbType = new ComboBox<>();
        cbType.getItems().addAll("EBook", "AudioBook", "PaperBook", "RareBook");
        cbType.setValue("EBook");
        styleCombo(cbType);

        // Загальні поля
        tfTitle  = styledField("required");
        tfAuthor = styledField("required");
        tfYear   = styledField("e.g. 2023");
        tfPrice  = styledField("e.g. 19.99");
        tfPages  = styledField("e.g. 300");
        cbGenre  = new ComboBox<>();
        cbGenre.getItems().addAll(Genre.values());
        cbGenre.setValue(Genre.FICTION);
        styleCombo(cbGenre);

        specificFieldsBox = new VBox(8);
        cbType.setOnAction(e -> rebuildSpecificFields());
        rebuildSpecificFields(); // початкова побудова для EBook

        Button btnAdd = new Button("Add Book");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setOnAction(e -> handleAddBook());

        Separator sep1 = new Separator();
        Separator sep2 = new Separator();

        VBox form = new VBox(9,
                heading,
                labeledRow("Type:",   cbType),
                labeledRow("Title:",  tfTitle),
                labeledRow("Author:", tfAuthor),
                labeledRow("Year:",   tfYear),
                labeledRow("Price:",  tfPrice),
                labeledRow("Genre:",  cbGenre),
                labeledRow("Pages:",  tfPages),
                sep1,
                specificFieldsBox,
                sep2,
                btnAdd
        );
        form.setPadding(new Insets(16));

        ScrollPane sp = new ScrollPane(form);
        sp.setFitToWidth(true);
        return sp;
    }

    private void rebuildSpecificFields() {
        specificFieldsBox.getChildren().clear();
        String type = cbType.getValue();

        switch (type) {
            case "EBook" -> {
                tfFileFormat  = styledField("EPUB / PDF / MOBI");
                tfFileSizeMB  = styledField("e.g. 5.2");
                tfDownloadUrl = styledField("https://…");
                specificFieldsBox.getChildren().addAll(
                        labeledRow("Format:", tfFileFormat),
                        labeledRow("Size MB:", tfFileSizeMB),
                        labeledRow("URL:",    tfDownloadUrl));
            }
            case "AudioBook" -> {
                tfNarrator        = styledField("Full name");
                tfDurationMinutes = styledField("e.g. 480");
                tfAudioFormat     = styledField("MP3 / AAC / FLAC");
                specificFieldsBox.getChildren().addAll(
                        labeledRow("Narrator:", tfNarrator),
                        labeledRow("Duration:", tfDurationMinutes),
                        labeledRow("Format:",   tfAudioFormat));
            }
            case "PaperBook" -> {
                tfPublisher   = styledField("Publisher name");
                tfEdition     = styledField("e.g. 1");
                tfWeightGrams = styledField("e.g. 450");
                specificFieldsBox.getChildren().addAll(
                        labeledRow("Publisher:", tfPublisher),
                        labeledRow("Edition:",   tfEdition),
                        labeledRow("Weight g:",  tfWeightGrams));
            }
            case "RareBook" -> {
                tfPublisher      = styledField("Publisher name");
                tfEdition        = styledField("e.g. 1");
                tfWeightGrams    = styledField("e.g. 800");
                cbCondition      = new ComboBox<>();
                cbCondition.getItems().addAll(BookCondition.values());
                cbCondition.setValue(BookCondition.GOOD);
                styleCombo(cbCondition);
                tfEstimatedValue  = styledField("e.g. 5000.00");
                tfAcquisitionYear = styledField("e.g. 1990");
                specificFieldsBox.getChildren().addAll(
                        labeledRow("Publisher:",   tfPublisher),
                        labeledRow("Edition:",     tfEdition),
                        labeledRow("Weight g:",    tfWeightGrams),
                        labeledRow("Condition:",   cbCondition),
                        labeledRow("Est. value:", tfEstimatedValue),
                        labeledRow("Acq. year:",  tfAcquisitionYear));
            }
        }
    }

    private void handleAddBook() {
        try {
            String title  = requireNonEmpty(tfTitle,  "Title");
            String author = requireNonEmpty(tfAuthor, "Author");
            int    year   = parseInt(tfYear,  "Year");
            double price  = parseDouble(tfPrice, "Price");
            Genre  genre  = cbGenre.getValue();
            int    pages  = parseInt(tfPages, "Pages");

            Book book = switch (cbType.getValue()) {
                case "EBook" -> {
                    String fmt = requireNonEmpty(tfFileFormat, "File format");
                    double sz  = parseDouble(tfFileSizeMB, "File size");
                    String url = requireNonEmpty(tfDownloadUrl, "Download URL");
                    yield new EBook(title, author, year, price, genre, pages, fmt, sz, url);
                }
                case "AudioBook" -> {
                    String nar  = requireNonEmpty(tfNarrator, "Narrator");
                    int    dur  = parseInt(tfDurationMinutes, "Duration");
                    String afmt = requireNonEmpty(tfAudioFormat, "Audio format");
                    yield new AudioBook(title, author, year, price, genre, pages, nar, dur, afmt);
                }
                case "PaperBook" -> {
                    String pub = requireNonEmpty(tfPublisher,   "Publisher");
                    int    ed  = parseInt(tfEdition,     "Edition");
                    double wt  = parseDouble(tfWeightGrams, "Weight");
                    yield new PaperBook(title, author, year, price, genre, pages, pub, ed, wt);
                }
                case "RareBook" -> {
                    String        pub  = requireNonEmpty(tfPublisher,     "Publisher");
                    int           ed   = parseInt(tfEdition,       "Edition");
                    double        wt   = parseDouble(tfWeightGrams, "Weight");
                    BookCondition cond = cbCondition.getValue();
                    double        val  = parseDouble(tfEstimatedValue,  "Est. value");
                    int           acq  = parseInt(tfAcquisitionYear, "Acq. year");
                    yield new RareBook(title, author, year, price, genre, pages, pub, ed, wt, cond, val, acq);
                }
                default -> throw new InvalidBookDataException("Unknown type.");
            };

            library.addNewBook(book, 1);
            refreshList();
            clearAddForm();
            setStatus("Added: " + book.getShortInfo(), false);

        } catch (InvalidBookDataException e) {
            setStatus("✗ " + e.getMessage(), true);
        }
    }

    private void clearAddForm() {
        tfTitle.clear();
        tfAuthor.clear();
        tfYear.clear();
        tfPrice.clear();
        tfPages.clear();
        cbGenre.setValue(Genre.FICTION);
        rebuildSpecificFields();
    }

    // ----------------------------------------------------------------
    // Вкладка «Search by UUID»
    // ----------------------------------------------------------------

    private VBox buildSearchPanel() {
        Label heading = sectionLabel("Search by UUID");

        tfUuidSearch = styledField("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
        tfUuidSearch.setFont(javafx.scene.text.Font.font("Monospaced", 12));

        Button btnFind = new Button("Find");
        btnFind.setMinWidth(70);
        btnFind.setOnAction(e -> handleUuidSearch());
        // Enter у полі вводу також запускає пошук
        tfUuidSearch.setOnAction(e -> handleUuidSearch());

        HBox searchRow = new HBox(8, tfUuidSearch, btnFind);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(tfUuidSearch, Priority.ALWAYS);

        taSearchResult = new TextArea();
        taSearchResult.setEditable(false);
        taSearchResult.setWrapText(true);
        taSearchResult.setFont(javafx.scene.text.Font.font("Monospaced", 13));
        taSearchResult.setPromptText("Result will appear here…");
        taSearchResult.setStyle(
                "-fx-border-radius: 6; -fx-font-family: monospace;");
        VBox.setVgrow(taSearchResult, Priority.ALWAYS);

        Label hint = new Label(
                "💡 Click any book in the left list to auto-fill the UUID field.");
        hint.setStyle("-fx-font-size: 11px;");

        VBox box = new VBox(12, heading, searchRow, taSearchResult, hint);
        box.setPadding(new Insets(16));
        return box;
    }

    private void handleUuidSearch() {
        String input = tfUuidSearch.getText().trim();

        if (input.isEmpty()) {
            taSearchResult.setText("Please enter a UUID.");
            setStatus("UUID field is empty.", true);
            return;
        }

        // Перевірка формату UUID
        try {
            UUID.fromString(input);
        } catch (IllegalArgumentException ex) {
            taSearchResult.setText(
                    " Invalid UUID format.\n\n"
                            + "Expected:\n"
                            + "  xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\n\n"
                            + "Got:\n  " + input);
            setStatus("✗ Invalid UUID format.", true);
            return;
        }

        BookEntry entry = library.findByUuid(input);

        if (entry == null) {
            taSearchResult.setText(
                    "✗  Not found.\n\n"
                            + "No book with UUID:\n  " + input + "\n\n"
                            + "Make sure the library is up to date.");
            setStatus("✗ Book not found for UUID: " + input.substring(0, 8) + "…", true);
        } else {
            Book b = entry.getBook();
            taSearchResult.setText(buildFullInfo(b, entry.getQuantity()));
            setStatus("✓ Found: " + b.getShortInfo(), false);
        }
    }

    private String buildFullInfo(Book b, int quantity) {
        StringBuilder sb = new StringBuilder();
        sb.append("Found Book\n");
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

    // ----------------------------------------------------------------
    // Status bar
    // ----------------------------------------------------------------

    /**
     * Будує рядок статусу внизу вікна.
     *
     * @return HBox рядку статусу
     */
    private HBox buildStatusBar() {
        statusLabel = new Label("Ready.");
        statusLabel.setStyle("-fx-font-size: 12px;");

        HBox bar = new HBox(statusLabel);
        bar.setPadding(new Insets(6, 16, 6, 16));
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
    }

    // ================================================================
    // UI-хелпери
    // ================================================================

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        return l;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 6 10;");
        return tf;
    }

    private <T> void styleCombo(ComboBox<T> cb) {
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setStyle(
                "-fx-border-radius: 4; -fx-background-radius: 4;");
    }

    private Button styledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-font-weight: bold; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 7 16;");
        return btn;
    }

    private HBox labeledRow(String labelText, Control control) {
        Label lbl = new Label(labelText);
        lbl.setMinWidth(100);
        lbl.setStyle("-fx-font-size: 12px;");
        HBox.setHgrow(control, Priority.ALWAYS);
        control.setMaxWidth(Double.MAX_VALUE);
        HBox row = new HBox(8, lbl, control);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // ================================================================
    // Хелпери валідації вводу
    // ================================================================

    private String requireNonEmpty(TextField tf, String fieldName) {
        String v = tf.getText().trim();
        if (v.isEmpty()) {
            throw new InvalidBookDataException(fieldName + " cannot be empty.");
        }
        return v;
    }

    private int parseInt(TextField tf, String fieldName) {
        try {
            return Integer.parseInt(tf.getText().trim());
        } catch (NumberFormatException e) {
            throw new InvalidBookDataException(fieldName + " must be a whole number.");
        }
    }

    private double parseDouble(TextField tf, String fieldName) {
        try {
            return Double.parseDouble(tf.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new InvalidBookDataException(fieldName + " must be a number (e.g. 19.99).");
        }
    }

    // ================================================================
    // Точка входу JVM
    // ================================================================

    public static void main(String[] args) {
        launch(args);
    }
}