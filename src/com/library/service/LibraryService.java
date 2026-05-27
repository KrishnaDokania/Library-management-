package com.library.service;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;
import com.library.repository.FileStorage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LibraryService {
    private final FileStorage fileStorage;
    private final List<Book> books;
    private final List<Member> members;
    private final List<Transaction> transactions;

    public static final double DAILY_FINE_RATE = 0.50;
    public static final int MAX_BORROW_LIMIT = 5;
    public static final int BORROW_DURATION_DAYS = 14;
    public static final double MAX_FINE_ALLOWED_TO_BORROW = 10.00;

    public static class ServiceResult {
        private final boolean success;
        private final String message;

        public ServiceResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public static ServiceResult ok(String message) {
            return new ServiceResult(true, message);
        }

        public static ServiceResult fail(String message) {
            return new ServiceResult(false, message);
        }
    }

    public LibraryService() {
        this.fileStorage = new FileStorage();
        this.books = fileStorage.loadBooks();
        this.members = fileStorage.loadMembers();
        this.transactions = fileStorage.loadTransactions();
    }

    // --- BOOK OPERATIONS ---
    public List<Book> getBooks() {
        return new ArrayList<>(books);
    }

    public Book findBookByIsbn(String isbn) {
        return books.stream()
                .filter(b -> b.getIsbn().equalsIgnoreCase(isbn.trim()))
                .findFirst()
                .orElse(null);
    }

    public ServiceResult addBook(Book book) {
        if (findBookByIsbn(book.getIsbn()) != null) {
            return ServiceResult.fail("A book with ISBN " + book.getIsbn() + " already exists.");
        }
        books.add(book);
        fileStorage.saveBooks(books);
        return ServiceResult.ok("Book '" + book.getTitle() + "' added successfully.");
    }

    public ServiceResult updateBook(String isbn, String newTitle, String newAuthor, String newGenre, int newTotalCopies) {
        Book book = findBookByIsbn(isbn);
        if (book == null) {
            return ServiceResult.fail("Book with ISBN " + isbn + " not found.");
        }

        // Calculate currently borrowed copies
        int borrowedCopies = book.getTotalCopies() - book.getAvailableCopies();
        if (newTotalCopies < borrowedCopies) {
            return ServiceResult.fail(String.format("Cannot set total copies to %d. %d copies are currently borrowed.", newTotalCopies, borrowedCopies));
        }

        book.setTitle(newTitle);
        book.setAuthor(newAuthor);
        book.setGenre(newGenre);
        book.setTotalCopies(newTotalCopies);
        book.setAvailableCopies(newTotalCopies - borrowedCopies);

        fileStorage.saveBooks(books);
        return ServiceResult.ok("Book updated successfully.");
    }

    public ServiceResult removeBook(String isbn) {
        Book book = findBookByIsbn(isbn);
        if (book == null) {
            return ServiceResult.fail("Book with ISBN " + isbn + " not found.");
        }

        // Check active borrowings
        boolean hasActiveTransactions = transactions.stream()
                .anyMatch(t -> t.getIsbn().equalsIgnoreCase(isbn) && t.isActive());
        if (hasActiveTransactions) {
            return ServiceResult.fail("Cannot remove book. There are active borrowings for this book.");
        }

        books.remove(book);
        fileStorage.saveBooks(books);
        return ServiceResult.ok("Book '" + book.getTitle() + "' removed successfully.");
    }

    // --- MEMBER OPERATIONS ---
    public List<Member> getMembers() {
        return new ArrayList<>(members);
    }

    public Member findMemberById(String id) {
        return members.stream()
                .filter(m -> m.getId().equalsIgnoreCase(id.trim()))
                .findFirst()
                .orElse(null);
    }

    public ServiceResult addMember(Member member) {
        if (findMemberById(member.getId()) != null) {
            return ServiceResult.fail("A member with ID " + member.getId() + " already exists.");
        }
        members.add(member);
        fileStorage.saveMembers(members);
        return ServiceResult.ok("Member '" + member.getName() + "' registered successfully.");
    }

    public ServiceResult updateMember(String id, String newName, String newEmail, String newPhone) {
        Member member = findMemberById(id);
        if (member == null) {
            return ServiceResult.fail("Member with ID " + id + " not found.");
        }

        member.setName(newName);
        member.setEmail(newEmail);
        member.setPhone(newPhone);

        fileStorage.saveMembers(members);
        return ServiceResult.ok("Member details updated successfully.");
    }

    public ServiceResult removeMember(String id) {
        Member member = findMemberById(id);
        if (member == null) {
            return ServiceResult.fail("Member with ID " + id + " not found.");
        }

        // Check active borrowings
        boolean hasActiveTransactions = transactions.stream()
                .anyMatch(t -> t.getMemberId().equalsIgnoreCase(id) && t.isActive());
        if (hasActiveTransactions) {
            return ServiceResult.fail("Cannot remove member. They currently have active book loans.");
        }

        // Check unpaid fines
        if (member.getUnpaidFines() > 0) {
            return ServiceResult.fail(String.format("Cannot remove member. They have unpaid fines of $%.2f.", member.getUnpaidFines()));
        }

        members.remove(member);
        fileStorage.saveMembers(members);
        return ServiceResult.ok("Member '" + member.getName() + "' removed successfully.");
    }

    // --- TRANSACTION OPERATIONS ---
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getActiveTransactions() {
        return transactions.stream().filter(Transaction::isActive).collect(Collectors.toList());
    }

    public ServiceResult borrowBook(String memberId, String isbn) {
        Member member = findMemberById(memberId);
        if (member == null) {
            return ServiceResult.fail("Member not found.");
        }

        Book book = findBookByIsbn(isbn);
        if (book == null) {
            return ServiceResult.fail("Book not found.");
        }

        // 1. Check book availability
        if (book.getAvailableCopies() <= 0) {
            return ServiceResult.fail("Book is currently out of stock (no available copies).");
        }

        // 2. Check member's outstanding fines
        if (member.getUnpaidFines() > MAX_FINE_ALLOWED_TO_BORROW) {
            return ServiceResult.fail(String.format("Borrowing blocked. Member has $%.2f in outstanding fines (limit is $%.2f).",
                    member.getUnpaidFines(), MAX_FINE_ALLOWED_TO_BORROW));
        }

        // 3. Check active borrowing count
        long activeCount = transactions.stream()
                .filter(t -> t.getMemberId().equalsIgnoreCase(memberId) && t.isActive())
                .count();
        if (activeCount >= MAX_BORROW_LIMIT) {
            return ServiceResult.fail("Borrowing blocked. Member has reached the limit of " + MAX_BORROW_LIMIT + " active loans.");
        }

        // 4. Check if member already has this book borrowed
        boolean alreadyBorrowed = transactions.stream()
                .anyMatch(t -> t.getMemberId().equalsIgnoreCase(memberId) && t.getIsbn().equalsIgnoreCase(isbn) && t.isActive());
        if (alreadyBorrowed) {
            return ServiceResult.fail("Member has already borrowed an active copy of this book.");
        }

        // All checks pass: Create transaction
        String txId = UUID.randomUUID().toString().substring(0, 8); // short ID
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(BORROW_DURATION_DAYS);

        Transaction tx = new Transaction(txId, member.getId(), book.getIsbn(), borrowDate, dueDate, null, 0.0);
        transactions.add(tx);

        // Update book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);

        // Save
        fileStorage.saveTransactions(transactions);
        fileStorage.saveBooks(books);

        return ServiceResult.ok(String.format("Book '%s' checked out to '%s'. Due date: %s.", book.getTitle(), member.getName(), dueDate));
    }

    public ServiceResult returnBook(String memberId, String isbn) {
        Member member = findMemberById(memberId);
        if (member == null) {
            return ServiceResult.fail("Member not found.");
        }

        Book book = findBookByIsbn(isbn);
        if (book == null) {
            return ServiceResult.fail("Book not found.");
        }

        // Find the active transaction
        Transaction activeTx = transactions.stream()
                .filter(t -> t.getMemberId().equalsIgnoreCase(member.getId()) && t.getIsbn().equalsIgnoreCase(book.getIsbn()) && t.isActive())
                .findFirst()
                .orElse(null);

        if (activeTx == null) {
            return ServiceResult.fail("No active loan record found for this member and book.");
        }

        // Calculate fines
        LocalDate returnDate = LocalDate.now();
        long daysLate = ChronoUnit.DAYS.between(activeTx.getDueDate(), returnDate);
        double fine = 0.0;
        if (daysLate > 0) {
            fine = daysLate * DAILY_FINE_RATE;
        }

        // Update transaction
        activeTx.setReturnDate(returnDate);
        activeTx.setFineAssessed(fine);

        // Update book copy
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        // Update member fine balance if any
        if (fine > 0) {
            member.addFine(fine);
        }

        // Save changes
        fileStorage.saveTransactions(transactions);
        fileStorage.saveBooks(books);
        fileStorage.saveMembers(members);

        if (fine > 0) {
            return ServiceResult.ok(String.format("Book '%s' returned. Overdue by %d days. Fine of $%.2f added to member balance.",
                    book.getTitle(), daysLate, fine));
        } else {
            return ServiceResult.ok(String.format("Book '%s' returned on time. No fines assessed.", book.getTitle()));
        }
    }

    // --- FINE PAYMENT ---
    public ServiceResult payFines(String memberId, double paymentAmount) {
        Member member = findMemberById(memberId);
        if (member == null) {
            return ServiceResult.fail("Member not found.");
        }

        if (paymentAmount <= 0) {
            return ServiceResult.fail("Payment amount must be greater than zero.");
        }

        if (member.getUnpaidFines() <= 0) {
            return ServiceResult.fail("Member has no outstanding fines.");
        }

        double paid = Math.min(member.getUnpaidFines(), paymentAmount);
        member.deductFine(paid);

        fileStorage.saveMembers(members);
        return ServiceResult.ok(String.format("Payment of $%.2f accepted. New balance: $%.2f.", paid, member.getUnpaidFines()));
    }

    // --- SEARCH / CATALOG QUERY ---
    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getBooks();
        }
        String lowerQuery = query.toLowerCase().trim();
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lowerQuery)
                        || b.getAuthor().toLowerCase().contains(lowerQuery)
                        || b.getGenre().toLowerCase().contains(lowerQuery)
                        || b.getIsbn().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    public List<Member> searchMembers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getMembers();
        }
        String lowerQuery = query.toLowerCase().trim();
        return members.stream()
                .filter(m -> m.getName().toLowerCase().contains(lowerQuery)
                        || m.getId().toLowerCase().contains(lowerQuery)
                        || m.getEmail().toLowerCase().contains(lowerQuery)
                        || m.getPhone().contains(lowerQuery))
                .collect(Collectors.toList());
    }
}
