package com.library.model;

import java.time.LocalDate;

public class Transaction {
    private String transactionId;
    private String memberId;
    private String isbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fineAssessed;

    public Transaction(String transactionId, String memberId, String isbn, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate, double fineAssessed) {
        this.transactionId = transactionId;
        this.memberId = memberId;
        this.isbn = isbn;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAssessed = fineAssessed;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getFineAssessed() {
        return fineAssessed;
    }

    public void setFineAssessed(double fineAssessed) {
        this.fineAssessed = fineAssessed;
    }

    public boolean isActive() {
        return returnDate == null;
    }

    @Override
    public String toString() {
        return String.format("Transaction[ID=%s, MemberID=%s, ISBN=%s, Borrowed=%s, Due=%s, Returned=%s, Fine=$%.2f]",
                transactionId, memberId, isbn, borrowDate, dueDate, returnDate != null ? returnDate : "Active", fineAssessed);
    }
}
