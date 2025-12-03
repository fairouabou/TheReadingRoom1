package com.thereadingroom.controllers;

import com.thereadingroom.db.BookDAO;
import com.thereadingroom.ui.Session;
import com.thereadingroom.ui.SceneController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

import java.util.Map;

public class AnalyticsController {

    @FXML
    private PieChart pieChart;

    @FXML
    public void initialize() {
        loadChart();
    }

    private void loadChart() {
        int userId = Session.getUserId();
        Map<String, Integer> counts = BookDAO.getCountsByListTypeForUser(userId);

        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

        for (String listType : counts.keySet()) {
            chartData.add(new PieChart.Data(
                    listType + " (" + counts.get(listType) + ")",
                    counts.get(listType)
            ));
        }

        pieChart.setData(chartData);
    }

    @FXML
    private void onBack() {
        SceneController.goBackFromAnalytics();
    }
}
