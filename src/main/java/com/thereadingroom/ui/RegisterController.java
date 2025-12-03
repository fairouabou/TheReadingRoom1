package com.thereadingroom.ui;

import com.thereadingroom.db.UserDAO;
import com.thereadingroom.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    public void onRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required.");
            return;
        }

        // role "standard" for normal users
        User newUser = new User(username, password, email, "standard");

        boolean success = UserDAO.registerUser(newUser);

        if (success) {
            showSuccess("Account created! You can now log in.");
        } else {
            showError("Username already taken or DB error.");
        }
    }

    @FXML
    public void onGoToLogin() {
        SceneController.goToLogin();
    }

    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(msg);
    }

    private void showSuccess(String msg) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(msg);
    }
}
