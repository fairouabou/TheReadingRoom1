package com.thereadingroom.ui;


import com.thereadingroom.db.FriendDAO;
import com.thereadingroom.db.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class FriendsController {

    @FXML
    private TextField searchUsersField;

    @FXML
    private TableView<FriendRow> friendsTable;
    @FXML
    private TableColumn<FriendRow, String> colFriendUsername;

    @FXML
    private TableView<UserRow> searchResultsTable;
    @FXML
    private TableColumn<UserRow, String> colSearchUsername;

    private ObservableList<FriendRow> friends;
    private ObservableList<UserRow> allUsersForSearch;
    private FilteredList<UserRow> filteredUsers;

    private List<Integer> cachedFriendIds = new ArrayList<>();

    @FXML
    public void initialize() {
        colFriendUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colSearchUsername.setCellValueFactory(new PropertyValueFactory<>("username"));

        friends = FXCollections.observableArrayList();
        allUsersForSearch = FXCollections.observableArrayList();

        loadFriends();
        loadSearchUsers();

        filteredUsers = new FilteredList<>(allUsersForSearch, u -> true);
        searchResultsTable.setItems(filteredUsers);

        searchUsersField.textProperty().addListener((obs, oldV, newV) -> {
            String lower = newV == null ? "" : newV.toLowerCase();
            filteredUsers.setPredicate(u -> {
                if (lower.isEmpty()) return true;
                return u.getUsername().toLowerCase().contains(lower);
            });
        });

        friendsTable.setItems(friends);
    }

    private void loadFriends() {
        int userId = Session.getUserId();

        cachedFriendIds = FriendDAO.findFriendIdsForUser(userId);

        friends.clear();
        for (int fId : cachedFriendIds) {
            String username = UserDAO.findUsernameById(fId);
            if (username != null) {
                friends.add(new FriendRow(fId, username));
            }
        }
    }

    private void loadSearchUsers() {
        allUsersForSearch.clear();
        int userId = Session.getUserId();

        for (UserDAO.UserDTO user : UserDAO.findAllUsers()) {
            if (user.id == userId) continue;
            if (cachedFriendIds.contains(user.id)) continue;

            allUsersForSearch.add(new UserRow(user.id, user.username));
        }
    }

    @FXML
    private void onAddFriend() {
        UserRow selected = searchResultsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        int userId = Session.getUserId();

        FriendDAO.addFriendPair(userId, selected.getId());

        cachedFriendIds.add(selected.getId());

        friends.add(new FriendRow(selected.getId(), selected.getUsername()));

        allUsersForSearch.remove(selected);
    }

    @FXML
    private void onRemoveFriend() {
        FriendRow selected = friendsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        int userId = Session.getUserId();

        FriendDAO.removeFriendPair(userId, selected.getId());

        cachedFriendIds.remove(Integer.valueOf(selected.getId()));

        friends.remove(selected);

        allUsersForSearch.add(new UserRow(selected.getId(), selected.getUsername()));
    }

    @FXML
    private void onBack() {
        SceneController.goToUserDashboard();
    }

    public static class FriendRow {
        private final int id;
        private final String username;

        public FriendRow(int id, String username) {
            this.id = id;
            this.username = username;
        }

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }
    }

    public static class UserRow {
        private final int id;
        private final String username;

        public UserRow(int id, String username) {
            this.id = id;
            this.username = username;
        }

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }
    }
}
