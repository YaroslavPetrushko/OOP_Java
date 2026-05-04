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
    // Панель зі списком книг
    // ----------------------------------------------------------------

    private ListView<BookEntry> bookListView;

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
        root.setCenter(buildPanel());
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
    // Панель зі списком книг
    // ----------------------------------------------------------------

    private VBox buildPanel() {
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

        VBox.setVgrow(bookListView, Priority.ALWAYS);

        VBox box = new VBox(10, heading, bookListView);
        box.setPadding(new Insets(16));
        return box;
    }


    // ----------------------------------------------------------------
    // Status bar
    // ----------------------------------------------------------------

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

    // ================================================================
    // Точка входу JVM
    // ================================================================

    public static void main(String[] args) {
        launch(args);
    }
}