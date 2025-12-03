package com.thereadingroom.ui;

import com.thereadingroom.db.UserDAO;
import com.thereadingroom.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    // No initialize() needed because no images or setup required

    @FXML
    public void onLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        User user = UserDAO.findUserByUsernameAndPassword(username, password);

        if (user == null) {
            showError("Invalid username or password.");
        } else {
            Session.setUser(user);
            showMessage("Welcome " + user.getUsername() + "!");

            if ("admin".equals(user.getRole())) {
                SceneController.goToAdminDashboard();
            } else {
                SceneController.goToUserDashboard();
            }
        }
    }

    @FXML
    public void onRegisterLink() {
        SceneController.goToRegister();
    }

    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(msg);
    }

    private void showMessage(String msg) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(msg);
    }
}
