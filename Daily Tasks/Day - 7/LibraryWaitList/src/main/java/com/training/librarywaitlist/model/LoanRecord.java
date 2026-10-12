package com.training.librarywaitlist.model;

public class LoanRecord {
    private final String bookTitle;
    private final int daysOverdue;

    public LoanRecord(String bookTitle, int daysOverdue) {
        this.bookTitle = bookTitle;
        this.daysOverdue = daysOverdue;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getDaysOverdue() {
        return daysOverdue;
    }

    @Override
    public String toString() {
        return bookTitle + " (Overdue: " + daysOverdue + "d)";
    }
}