package com.thereadingroom.ui;

import com.thereadingroom.db.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ManageUsersController {

    @FXML private TableView<UserDAO.UserDTO> usersTable;
    @FXML private TableColumn<UserDAO.UserDTO, Integer> colId;
    @FXML private TableColumn<UserDAO.UserDTO, String> colUsername;
    @FXML private TableColumn<UserDAO.UserDTO, String> colEmail;
    @FXML private TableColumn<UserDAO.UserDTO, String> colRole;

    private ObservableList<UserDAO.UserDTO> usersObservable;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().id).asObject());

        colUsername.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().username));

        colEmail.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().email));

        colRole.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().role));

        refreshTable();
    }

    private void refreshTable() {
        List<UserDAO.UserDTO> users = UserDAO.findAllUsers(); // now returns id, username, email, role
        usersObservable = FXCollections.observableArrayList(users);
        usersTable.setItems(usersObservable);
    }

    @FXML
    private void onMakeAdmin() {
        UserDAO.UserDTO selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No user selected.");
            return;
        }

        if (UserDAO.updateUserRole(selected.id, "admin")) {
            showInfo("Success", selected.username + " is now admin.");
            refreshTable();
        }
    }

    @FXML
    private void onMakeUser() {
        UserDAO.UserDTO selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No user selected.");
            return;
        }

        if (UserDAO.updateUserRole(selected.id, "standard")) {
            showInfo("Success", selected.username + " is now a normal user.");
            refreshTable();
        }
    }

    @FXML
    private void onBackToAdmin() {
        SceneController.goToAdminDashboard();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.showAndWait();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
