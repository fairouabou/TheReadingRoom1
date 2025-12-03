package com.thereadingroom.model;

public class Discussion implements Postable {

    private int id;
    private int bookId;
    private String title;

    public Discussion(int id, int bookId, String title) {
        this.id = id;
        this.bookId = bookId;
        this.title = title;
    }

    public Discussion(int bookId, String title) {
        this(-1, bookId, title);
    }

    @Override
    public int getAuthorId() {
        return -1; // discussions have no author
    }

    @Override
    public String getContent() {
        return title;
    }

    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public String getTitle() { return title; }

    public void setId(int id) { this.id = id; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public void setTitle(String title) { this.title = title; }

    @Override
    public String toString() {
        return title;
    }
}
