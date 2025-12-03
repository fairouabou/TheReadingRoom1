package com.thereadingroom.controllers;

import com.thereadingroom.db.BookDAO;
import com.thereadingroom.db.UserDAO;
import com.thereadingroom.ui.SceneController;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

import java.util.Map;

public class AdminAnalyticsController {

    @FXML private Label lblUsers;
    @FXML private Label lblBooks;
    @FXML private Label lblHaveRead;
    @FXML private Label lblWantToRead;
    @FXML private Label lblCurrentlyReading;
    @FXML private Label lblMostPopularType;

    @FXML private BarChart<String, Number> booksChart;
    @FXML private BarChart<String, Number> authorsChart;
    @FXML private PieChart completionChart;

    @FXML
    public void initialize() {
        loadKPIs();
        loadPopularBooks();
        loadTopAuthors();
        loadCompletionRate();
    }

    private void loadKPIs() {
        lblUsers.setText(String.valueOf(UserDAO.countUsers()));
        lblBooks.setText(String.valueOf(BookDAO.countTotalBooks()));
        lblHaveRead.setText(String.valueOf(BookDAO.countListType("Have Read")));
        lblWantToRead.setText(String.valueOf(BookDAO.countListType("Want to Read")));
        lblCurrentlyReading.setText(String.valueOf(BookDAO.countListType("Currently Reading")));
        lblMostPopularType.setText(BookDAO.findMostPopularListType());
    }

    private void loadPopularBooks() {
        booksChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Map.Entry<String, Integer> e : BookDAO.countBooksByTitle().entrySet()) {
            series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }

        booksChart.getData().add(series);
    }

    private void loadTopAuthors() {
        authorsChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Map.Entry<String, Integer> e : BookDAO.countBooksByAuthor().entrySet()) {
            series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }

        authorsChart.getData().add(series);
    }

    private void loadCompletionRate() {
        completionChart.getData().clear();

        int haveRead = BookDAO.countListType("Have Read");
        int total = BookDAO.countTotalBooks();
        int notCompleted = total - haveRead;

        completionChart.getData().add(new PieChart.Data("Have Read", haveRead));
        completionChart.getData().add(new PieChart.Data("Not Completed", notCompleted));
    }

    @FXML
    private void onBack() {
        SceneController.goToAdminDashboard();
    }
}
