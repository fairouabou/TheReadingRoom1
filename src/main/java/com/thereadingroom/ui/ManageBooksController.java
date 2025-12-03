package com.thereadingroom.ui;

import com.thereadingroom.db.BookDAO;
import com.thereadingroom.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ManageBooksController {

    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, Integer> colId;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, String> colGenre;

    private ObservableList<Book> bookList;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));

        loadBooks();
    }

    private void loadBooks() {
        List<Book> books = BookDAO.findAllBooks();
        bookList = FXCollections.observableArrayList(books);
        booksTable.setItems(bookList);
    }

    @FXML
    private void onBack() {
        SceneController.goToAdminDashboard();
    }

    @FXML
    private void onAddBook() {
        showBookDialog(null); // null means it's a new book
    }

    @FXML
    private void onEditBook() {
        Book selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No selection", "Please select a book to edit.");
            return;
        }
        showBookDialog(selected);
    }

    @FXML
    private void onDeleteBook() {
        Book selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No selection", "Please select a book to delete.");
            return;
        }

        BookDAO.deleteBook(selected.getId());
        loadBooks();
    }

    // Pop-up dialog for Add/Edit Book
    private void showBookDialog(Book bookToEdit) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(bookToEdit == null ? "Add Book" : "Edit Book");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter: title, author, genre (comma-separated)");

        if (bookToEdit != null) {
            dialog.getEditor().setText(bookToEdit.getTitle() + ", " +
                    bookToEdit.getAuthor() + ", " +
                    bookToEdit.getGenre());
        }

        dialog.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",");
            if (parts.length != 3) {
                showAlert("Invalid Input", "Please enter exactly: title, author, genre");
                return;
            }

            String title = parts[0].trim();
            String author = parts[1].trim();
            String genre = parts[2].trim();

            if (bookToEdit == null) {
                BookDAO.addBook(title, author, genre);
            } else {
                BookDAO.updateBook(bookToEdit.getId(), title, author, genre);
            }

            loadBooks();
        });
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}