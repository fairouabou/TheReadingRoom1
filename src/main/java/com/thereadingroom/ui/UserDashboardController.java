package com.thereadingroom.ui;

import com.thereadingroom.db.BookDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class UserDashboardController {

    @FXML private TextField searchField;
    @FXML private TableView<BookRow> booksTable;
    @FXML private TableColumn<BookRow, String> colTitle;
    @FXML private TableColumn<BookRow, String> colAuthor;
    @FXML private TableColumn<BookRow, String> colListType;
    @FXML private TableColumn<BookRow, Integer> colRating;

    private ObservableList<BookRow> allBooks;
    private FilteredList<BookRow> filteredBooks;

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colListType.setCellValueFactory(new PropertyValueFactory<>("listType"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));

        allBooks = FXCollections.observableArrayList();
        int userId = Session.getUserId();

        for (BookDAO.BookRowDTO dto : BookDAO.findBooksForUser(userId)) {
            allBooks.add(new BookRow(dto.userBookId, dto.title, dto.author, dto.listType, dto.rating));
        }

        filteredBooks = new FilteredList<>(allBooks, b -> true);
        booksTable.setItems(filteredBooks);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String lower = newVal == null ? "" : newVal.toLowerCase();
            filteredBooks.setPredicate(book ->
                    lower.isEmpty()
                            || book.getTitle().toLowerCase().contains(lower)
                            || book.getAuthor().toLowerCase().contains(lower)
                            || book.getListType().toLowerCase().contains(lower)
            );
        });
    }

    @FXML
    private void onAddBook() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Book");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField titleField = new TextField();
        TextField authorField = new TextField();
        ChoiceBox<String> listChoice = new ChoiceBox<>();

        titleField.setPromptText("Title");
        authorField.setPromptText("Author");
        listChoice.getItems().addAll("Currently Reading", "Want to Read", "Have Read");
        listChoice.setValue("Want to Read");

        VBox content = new VBox(10,
                new Label("Title:"), titleField,
                new Label("Author:"), authorField,
                new Label("List:"), listChoice
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(button -> {
            if (button == saveButtonType) {
                String title = safeText(titleField);
                String author = safeText(authorField);
                String listType = listChoice.getValue();

                if (!title.isEmpty() && !author.isEmpty()) {
                    int userId = Session.getUserId();
                    BookDAO.addBookForUser(userId, title, author, listType);
                    reloadFromDb();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void onEditBook() {
        BookRow selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Book");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        ChoiceBox<String> listChoice = new ChoiceBox<>();
        listChoice.getItems().addAll("Currently Reading", "Want to Read", "Have Read");
        listChoice.setValue(selected.getListType());

        TextField ratingField = new TextField();
        ratingField.setPromptText("1-5 (optional)");
        if (selected.getRating() != null) {
            ratingField.setText(String.valueOf(selected.getRating()));
        }

        VBox content = new VBox(10,
                new Label("Change list for \"" + selected.getTitle() + "\":" ),
                listChoice,
                new Label("Rating (1-5, optional):"),
                ratingField
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(button -> {
            if (button == saveButtonType) {
                String newListType = listChoice.getValue();

                Integer newRating = null;
                String ratingText = ratingField.getText();
                if (ratingText != null && !ratingText.trim().isEmpty()) {
                    try {
                        int r = Integer.parseInt(ratingText.trim());
                        if (r >= 1 && r <= 5) newRating = r;
                    } catch (Exception ignored) {}
                }

                BookDAO.updateListTypeAndRating(selected.getUserBookId(), newListType, newRating);

                int idx = allBooks.indexOf(selected);
                if (idx >= 0) {
                    allBooks.set(idx, new BookRow(
                            selected.getUserBookId(),
                            selected.getTitle(),
                            selected.getAuthor(),
                            newListType,
                            newRating
                    ));
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void onDeleteBook() {
        BookRow selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        BookDAO.deleteUserBook(selected.getUserBookId());
        allBooks.remove(selected);
    }

    @FXML
    private void onDiscussions() {
        SceneController.goToDiscussions();
    }

    @FXML
    private void onFriends() {
        SceneController.goToFriends();
    }

    @FXML
    private void onAnalytics() {
        SceneController.goToAnalytics();
    }

    @FXML
    private void onLogout() {
        Session.clear();
        SceneController.goToLogin();
    }

    private void reloadFromDb() {
        allBooks.clear();
        int userId = Session.getUserId();
        for (BookDAO.BookRowDTO dto : BookDAO.findBooksForUser(userId)) {
            allBooks.add(new BookRow(dto.userBookId, dto.title, dto.author, dto.listType, dto.rating));
        }
    }

    private String safeText(TextInputControl field) {
        String t = field.getText();
        return t == null ? "" : t.trim();
    }

    public static class BookRow {
        private final int userBookId;
        private final String title;
        private final String author;
        private final String listType;
        private final Integer rating;

        public BookRow(int userBookId, String title, String author, String listType, Integer rating) {
            this.userBookId = userBookId;
            this.title = title;
            this.author = author;
            this.listType = listType;
            this.rating = rating;
        }

        public int getUserBookId() { return userBookId; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getListType() { return listType; }
        public Integer getRating() { return rating; }
    }
}
