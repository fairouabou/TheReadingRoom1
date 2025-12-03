package com.thereadingroom.ui;

import com.thereadingroom.db.DiscussionDAO;
import com.thereadingroom.db.PostDAO;
import com.thereadingroom.model.Discussion;
import com.thereadingroom.model.Post;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.util.List;

public class DiscussionsController {

    @FXML private ListView<Discussion> discussionsList;
    @FXML private ListView<Post> postsList;
    @FXML private Label discussionTitleLabel;
    @FXML private TextArea postContentArea;
    @FXML private Button newDiscussionButton;

    private final ObservableList<Discussion> discussions = FXCollections.observableArrayList();
    private final ObservableList<Post> posts = FXCollections.observableArrayList();
    private Discussion selectedDiscussion;

    @FXML
    public void initialize() {

        // --- Fix TextArea not editable ---
        postContentArea.setEditable(true);
        postContentArea.setDisable(false);
        postContentArea.setFocusTraversable(true);
        postContentArea.setWrapText(true);

        discussionsList.setItems(discussions);
        postsList.setItems(posts);

        String role = Session.getRole();
        if (role == null || !role.equalsIgnoreCase("admin")) {
            newDiscussionButton.setVisible(false);
            newDiscussionButton.setManaged(false);
        }

        loadDiscussions();

        discussionsList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            selectedDiscussion = newV;
            loadPostsForSelected();
        });
    }

    private void loadDiscussions() {
        discussions.clear();
        discussions.addAll(DiscussionDAO.findAll());

        if (!discussions.isEmpty()) {
            discussionsList.getSelectionModel().selectFirst();
        } else {
            discussionTitleLabel.setText("No discussions yet");
            posts.clear();
        }
    }

    private void loadPostsForSelected() {
        posts.clear();
        if (selectedDiscussion == null) {
            discussionTitleLabel.setText("Select a discussion");
            return;
        }

        discussionTitleLabel.setText(selectedDiscussion.getTitle());
        posts.addAll(PostDAO.findByDiscussion(selectedDiscussion.getId()));
    }

    @FXML
    private void onBack() {
        SceneController.goBackFromDiscussions();
    }

    @FXML
    private void onNewDiscussion() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Discussion");
        dialog.setHeaderText("Create a new discussion");
        dialog.setContentText("Title:");

        String title = dialog.showAndWait().orElse(null);
        if (title == null || title.trim().isEmpty()) return;

        TextInputDialog bookDialog = new TextInputDialog();
        bookDialog.setTitle("Link Book");
        bookDialog.setHeaderText("Enter the book id for this discussion");
        bookDialog.setContentText("Book ID:");

        String bookIdText = bookDialog.showAndWait().orElse(null);
        if (bookIdText == null || bookIdText.trim().isEmpty()) return;

        int bookId;
        try {
            bookId = Integer.parseInt(bookIdText.trim());
        } catch (NumberFormatException e) {
            return;
        }

        DiscussionDAO.insert(new Discussion(bookId, title.trim()));
        loadDiscussions();
    }

    @FXML
    private void onSendPost() {
        System.out.println("POST CLICKED");

        if (selectedDiscussion == null) return;
        String content = postContentArea.getText();
        if (content == null || content.trim().isEmpty()) return;

        int userId = Session.getUserId();
        String now = LocalDateTime.now().toString();

        Post p = new Post(selectedDiscussion.getId(), userId, content.trim(), now);
        PostDAO.insert(p);

        postContentArea.clear();
        loadPostsForSelected();
    }
}
