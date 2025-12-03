# The Reading Room is a JavaFX application that allows users to:

## Overview
- Track books ("Have Read", "Want to Read", "Currently Reading")
- Participate in book discussions
- Add posts to discussion threads
- Add and manage friends
- View analytics based on their reading activity

The project follows a layered architecture, uses object-oriented design, and persists all data in an SQLite database through a DAO layer.

## Architecture 
The system is built following a layered design with clear separation of concerns:

### 1. Presentation Layer (JavaFX UI + Controllers)
This layer contains all the FXML screens and their controllers.
Controllers handle user interaction only:
- Button actions
- Field validation
- Updating tables/charts
- Navigating between screens

Main controllers:
- LoginController
- RegisterController
- DashboardController
- FriendsController
- DiscussionsController
- AnalyticsController

Controllers do not contain business logic or SQL calls.
They delegate work to the application core and DAOs.

### 2. Application Core

**SceneController**
- Centralized navigation system.
- Instead of each controller loading FXML manually, controllers call methods such as:
goToDashboard()
goToFriends()
goToDiscussions()
goToAnalytics()

This avoids duplicated code and keeps navigation consistent.

**Session**

Stores the currently logged-in user as a User object.
Any controller can use:

Session.getUser();

This prevents passing user objects manually between screens.

### 3. Domain Model 

Represents the core concepts of the application:

**User:**

id, username, password, email, role

permission methods:

canManageUsers()

canManageBooks()

canDeleteDiscussion()

Users can be admin or standard, controlled by the role field.

**Book:**

Stores book metadata: title, author, genre.

UserBook

Connects a User to a Book and contains:

- list type

- rating

This models the three reading lists:

- Have Read

- Want to Read

- Currently Reading

**Discussion**

- Represents a discussion thread for a specific book.

- Postable (Interface)

Defines two generic methods:

getAuthorId()

getContent()


**Post (implements Postable)**

Represents a message inside a discussion.

Key relationships

- A user has many list entries (UserBook)

- A book appears in many user lists

- A book has discussions

- A discussion contains posts

- A user writes many posts

- Users can be friends (many-to-many)


## 4. Persistence Layer (SQLite + DAOs)

All database communication is encapsulated in:

- DataBaseManager

- UserDAO

- BookDAO

- DiscussionDAO

- FriendDAO

- PostDAO

Controllers do not execute SQL.
Instead, they call methods like:

UserDAO.findByUsernameAndPassword(...)
BookDAO.findBooksForUser(...)
PostDAO.addPost(...)

This keeps the system modular and easy to maintain.

### **OOP Concepts Used**

**Encapsulation**

- SQL and JDBC details are hidden inside DAO classes.

- Controllers do not know how data is stored.

**Separation of Concerns**

- UI logic → Controllers

- Navigation → SceneController

- Logged-in user management → Session

- Business model → Domain classes

- Persistence → DAOs

**Composition**

- UserBook connects users and books with additional fields (list type + rating).

- Discussion contains a list of posts.

**Polymorphism**

- Post implements Postable to allow future extensible message types.

- Permission methods (canManageUsers(), etc.) allow different behavior based on role.

**Abstraction**

Postable abstracts any object that has content written by a user.

### **UML Class Diagram**

A clear UML diagram was designed to show:

- Controllers and how they depend on Session + SceneController

- Domain entities and their relationships

- DAO layer separation

- Inheritance via Postable


### **Database Schema (ERD)**
The SQLite schema includes the following tables:
- users

- books

- user_books

- discussions

- posts

- friends

- genres, roles, list_types (optional reference tables)

DAOs map between these tables and domain classes.

### *Features*
- Login & Register
- Manage reading lists
- Add/Edit/Delete books
- Participate in discussions
- Post messages
- View personalized analytics
- Manage friends
- Role-based permissions 
- How to Run

Install Java 21+
Ensure SQLite JDBC driver is on classpath, and make sure the configuration matches the local machine from which you're running the program. 
Run Main.java
Database initializes automatically via DataBaseManager

## **Conclusion**

This project demonstrates a clean architecture with:

Strong separation of concerns

Meaningful OOP design

Reusable components

A scalable domain model

Organized navigation and session handling

The result is a modular, maintainable JavaFX application that models a real social reading platform.


## Credentials 
admin credentials: 
username: admin 
password: admin123

user credentials:
username: miadragovic 
password: Pass12345!

