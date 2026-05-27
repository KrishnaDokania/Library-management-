package com.library;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;
import com.library.service.LibraryService;
import com.library.service.LibraryService.ServiceResult;
import com.library.util.ConsoleUtils;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final LibraryService service = new LibraryService();

    public static void main(String[] args) {
        showWelcomeBanner();
        runMainMenu();
        showGoodbyeMessage();
    }

    private static void showWelcomeBanner() {
        System.out.println("========================================");
        System.out.println("       LIBRARY MANAGEMENT SYSTEM        ");
        System.out.println("========================================");
    }

    private static void showGoodbyeMessage() {
        System.out.println("\nThank you for using the Library Management System. Goodbye!");
    }

    private static void runMainMenu() {
        while (true) {
            ConsoleUtils.printHeader("Main Menu");
            System.out.println("1. Book Management");
            System.out.println("2. Member Management");
            System.out.println("3. Borrow & Return Operations");
            System.out.println("4. Search Catalog");
            System.out.println("5. Fines & Payments");
            System.out.println("6. Exit");

            int choice = ConsoleUtils.readInt("\nSelect option (1-6): ", 1, 6);
            switch (choice) {
                case 1 -> handleBookMenu();
                case 2 -> handleMemberMenu();
                case 3 -> handleBorrowReturnMenu();
                case 4 -> handleSearchMenu();
                case 5 -> handleFineMenu();
                case 6 -> {
                    return;
                }
            }
        }
    }

    // ==========================================
    // 1. BOOK MENU
    // ==========================================
    private static void handleBookMenu() {
        while (true) {
            ConsoleUtils.printHeader("Book Management");
            System.out.println("1. List All Books");
            System.out.println("2. Add Book");
            System.out.println("3. Update Book");
            System.out.println("4. Remove Book");
            System.out.println("5. Back to Main Menu");

            int choice = ConsoleUtils.readInt("\nSelect option (1-5): ", 1, 5);
            switch (choice) {
                case 1 -> listAllBooks();
                case 2 -> addNewBook();
                case 3 -> updateBookDetails();
                case 4 -> removeBook();
                case 5 -> {
                    return;
                }
            }
        }
    }

    private static void listAllBooks() {
        ConsoleUtils.printHeader("All Books");
        List<Book> books = service.getBooks();
        String[] headers = {"ISBN", "Title", "Author", "Genre", "Total", "Available"};
        List<String[]> rows = new ArrayList<>();
        for (Book b : books) {
            rows.add(new String[]{
                    b.getIsbn(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getGenre(),
                    String.valueOf(b.getTotalCopies()),
                    String.valueOf(b.getAvailableCopies())
            });
        }
        ConsoleUtils.printTable(headers, rows);
        ConsoleUtils.pressEnterToContinue();
    }

    private static void addNewBook() {
        ConsoleUtils.printHeader("Add Book");
        String isbn = ConsoleUtils.readISBN("Enter ISBN: ");
        if (service.findBookByIsbn(isbn) != null) {
            ConsoleUtils.printError("ISBN already exists.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        String title = ConsoleUtils.readString("Enter Title: ");
        String author = ConsoleUtils.readString("Enter Author: ");
        String genre = ConsoleUtils.readString("Enter Genre: ");
        int copies = ConsoleUtils.readInt("Enter Total Copies: ", 1, 1000);

        Book book = new Book(isbn, title, author, genre, copies, copies);
        ServiceResult result = service.addBook(book);
        if (result.isSuccess()) {
            ConsoleUtils.printSuccess(result.getMessage());
        } else {
            ConsoleUtils.printError(result.getMessage());
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private static void updateBookDetails() {
        ConsoleUtils.printHeader("Update Book");
        String isbn = ConsoleUtils.readISBN("Enter Book ISBN: ");
        Book existing = service.findBookByIsbn(isbn);
        if (existing == null) {
            ConsoleUtils.printError("Book not found.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }

        System.out.println("Note: Press Enter to keep current value.");
        String title = ConsoleUtils.readStringOptional("Enter Title", existing.getTitle());
        String author = ConsoleUtils.readStringOptional("Enter Author", existing.getAuthor());
        String genre = ConsoleUtils.readStringOptional("Enter Genre", existing.getGenre());
        int totalCopies = ConsoleUtils.readIntOptional("Enter Total Copies", existing.getTotalCopies(), 1, 1000);

        ServiceResult result = service.updateBook(isbn, title, author, genre, totalCopies);
        if (result.isSuccess()) {
            ConsoleUtils.printSuccess(result.getMessage());
        } else {
            ConsoleUtils.printError(result.getMessage());
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private static void removeBook() {
        ConsoleUtils.printHeader("Remove Book");
        String isbn = ConsoleUtils.readISBN("Enter ISBN: ");
        Book existing = service.findBookByIsbn(isbn);
        if (existing == null) {
            ConsoleUtils.printError("Book not found.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }

        String confirm = ConsoleUtils.readString("Are you sure? (y/n): ");
        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            ServiceResult result = service.removeBook(isbn);
            if (result.isSuccess()) {
                ConsoleUtils.printSuccess(result.getMessage());
            } else {
                ConsoleUtils.printError(result.getMessage());
            }
        } else {
            ConsoleUtils.printInfo("Cancelled.");
        }
        ConsoleUtils.pressEnterToContinue();
    }

    // ==========================================
    // 2. MEMBER MENU
    // ==========================================
    private static void handleMemberMenu() {
        while (true) {
            ConsoleUtils.printHeader("Member Management");
            System.out.println("1. List All Members");
            System.out.println("2. Register New Member");
            System.out.println("3. Update Member Details");
            System.out.println("4. Remove Member");
            System.out.println("5. Back to Main Menu");

            int choice = ConsoleUtils.readInt("\nSelect option (1-5): ", 1, 5);
            switch (choice) {
                case 1 -> listAllMembers();
                case 2 -> registerMember();
                case 3 -> updateMemberProfile();
                case 4 -> removeMember();
                case 5 -> {
                    return;
                }
            }
        }
    }

    private static void listAllMembers() {
        ConsoleUtils.printHeader("All Members");
        List<Member> members = service.getMembers();
        String[] headers = {"ID", "Name", "Email", "Phone", "Fines"};
        List<String[]> rows = new ArrayList<>();
        for (Member m : members) {
            rows.add(new String[]{
                    m.getId(),
                    m.getName(),
                    m.getEmail(),
                    m.getPhone(),
                    String.format("$%.2f", m.getUnpaidFines())
            });
        }
        ConsoleUtils.printTable(headers, rows);
        ConsoleUtils.pressEnterToContinue();
    }

    private static void registerMember() {
        ConsoleUtils.printHeader("Register Member");
        String id = ConsoleUtils.readString("Enter Member ID: ").toUpperCase();
        if (service.findMemberById(id) != null) {
            ConsoleUtils.printError("Member ID already exists.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        String name = ConsoleUtils.readString("Enter Name: ");
        String email = ConsoleUtils.readEmail("Enter Email: ");
        String phone = ConsoleUtils.readPhone("Enter Phone: ");

        Member m = new Member(id, name, email, phone, 0.0);
        ServiceResult result = service.addMember(m);
        if (result.isSuccess()) {
            ConsoleUtils.printSuccess(result.getMessage());
        } else {
            ConsoleUtils.printError(result.getMessage());
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private static void updateMemberProfile() {
        ConsoleUtils.printHeader("Update Member");
        String id = ConsoleUtils.readString("Enter Member ID: ").toUpperCase();
        Member existing = service.findMemberById(id);
        if (existing == null) {
            ConsoleUtils.printError("Member not found.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }

        System.out.println("Note: Press Enter to keep current value.");
        String name = ConsoleUtils.readStringOptional("Enter Name", existing.getName());
        String email = ConsoleUtils.readEmailOptional("Enter Email", existing.getEmail());
        String phone = ConsoleUtils.readPhoneOptional("Enter Phone", existing.getPhone());

        ServiceResult result = service.updateMember(id, name, email, phone);
        if (result.isSuccess()) {
            ConsoleUtils.printSuccess(result.getMessage());
        } else {
            ConsoleUtils.printError(result.getMessage());
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private static void removeMember() {
        ConsoleUtils.printHeader("Remove Member");
        String id = ConsoleUtils.readString("Enter ID: ").toUpperCase();
        Member existing = service.findMemberById(id);
        if (existing == null) {
            ConsoleUtils.printError("Member not found.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }

        String confirm = ConsoleUtils.readString("Are you sure? (y/n): ");
        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            ServiceResult result = service.removeMember(id);
            if (result.isSuccess()) {
                ConsoleUtils.printSuccess(result.getMessage());
            } else {
                ConsoleUtils.printError(result.getMessage());
            }
        } else {
            ConsoleUtils.printInfo("Cancelled.");
        }
        ConsoleUtils.pressEnterToContinue();
    }

    // ==========================================
    // 3. BORROW & RETURN OPERATIONS
    // ==========================================
    private static void handleBorrowReturnMenu() {
        while (true) {
            ConsoleUtils.printHeader("Borrow & Return Operations");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. List Active Loans");
            System.out.println("4. List All Transactions");
            System.out.println("5. Back to Main Menu");

            int choice = ConsoleUtils.readInt("\nSelect option (1-5): ", 1, 5);
            switch (choice) {
                case 1 -> borrowBook();
                case 2 -> returnBook();
                case 3 -> listActiveLoans();
                case 4 -> listAllTransactions();
                case 5 -> {
                    return;
                }
            }
        }
    }

    private static void borrowBook() {
        ConsoleUtils.printHeader("Borrow Book");
        String memberId = ConsoleUtils.readString("Enter Member ID: ").toUpperCase();
        Member member = service.findMemberById(memberId);
        if (member == null) {
            ConsoleUtils.printError("Member not found.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }

        String isbn = ConsoleUtils.readISBN("Enter Book ISBN: ");
        Book book = service.findBookByIsbn(isbn);
        if (book == null) {
            ConsoleUtils.printError("Book not found.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }

        ServiceResult result = service.borrowBook(memberId, isbn);
        if (result.isSuccess()) {
            ConsoleUtils.printSuccess(result.getMessage());
        } else {
            ConsoleUtils.printError(result.getMessage());
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private static void returnBook() {
        ConsoleUtils.printHeader("Return Book");
        String memberId = ConsoleUtils.readString("Enter Member ID: ").toUpperCase();
        Member member = service.findMemberById(memberId);
        if (member == null) {
            ConsoleUtils.printError("Member not found.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }

        String isbn = ConsoleUtils.readISBN("Enter Book ISBN: ");
        Book book = service.findBookByIsbn(isbn);
        if (book == null) {
            ConsoleUtils.printError("Book not found.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }

        ServiceResult result = service.returnBook(memberId, isbn);
        if (result.isSuccess()) {
            ConsoleUtils.printSuccess(result.getMessage());
        } else {
            ConsoleUtils.printError(result.getMessage());
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private static void listActiveLoans() {
        ConsoleUtils.printHeader("Active Loans");
        List<Transaction> active = service.getActiveTransactions();
        displayTransactionsTable(active);
        ConsoleUtils.pressEnterToContinue();
    }

    private static void listAllTransactions() {
        ConsoleUtils.printHeader("Transaction History");
        List<Transaction> all = service.getTransactions();
        displayTransactionsTable(all);
        ConsoleUtils.pressEnterToContinue();
    }

    private static void displayTransactionsTable(List<Transaction> list) {
        String[] headers = {"TX ID", "Member ID", "Member Name", "ISBN", "Book Title", "Borrow Date", "Due Date", "Status", "Fine"};
        List<String[]> rows = new ArrayList<>();
        for (Transaction t : list) {
            Member m = service.findMemberById(t.getMemberId());
            Book b = service.findBookByIsbn(t.getIsbn());

            String memberName = m != null ? m.getName() : "Unknown";
            String bookTitle = b != null ? b.getTitle() : "Unknown";

            String status = t.isActive() ? "ACTIVE" : "Returned (" + t.getReturnDate() + ")";
            String fineStr = t.getFineAssessed() > 0 ? String.format("$%.2f", t.getFineAssessed()) : "$0.00";

            rows.add(new String[]{
                    t.getTransactionId(),
                    t.getMemberId(),
                    memberName,
                    t.getIsbn(),
                    bookTitle,
                    t.getBorrowDate().toString(),
                    t.getDueDate().toString(),
                    status,
                    fineStr
            });
        }
        ConsoleUtils.printTable(headers, rows);
    }

    // ==========================================
    // 4. CATALOG SEARCH MENU
    // ==========================================
    private static void handleSearchMenu() {
        while (true) {
            ConsoleUtils.printHeader("Search Catalog");
            System.out.println("1. Search Books");
            System.out.println("2. Search Members");
            System.out.println("3. Back to Main Menu");

            int choice = ConsoleUtils.readInt("\nSelect option (1-3): ", 1, 3);
            switch (choice) {
                case 1 -> searchBooks();
                case 2 -> searchMembers();
                case 3 -> {
                    return;
                }
            }
        }
    }

    private static void searchBooks() {
        ConsoleUtils.printHeader("Search Books");
        String query = ConsoleUtils.readString("Enter search keyword: ");
        List<Book> results = service.searchBooks(query);

        String[] headers = {"ISBN", "Title", "Author", "Genre", "Total", "Available"};
        List<String[]> rows = new ArrayList<>();
        for (Book b : results) {
            rows.add(new String[]{
                    b.getIsbn(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getGenre(),
                    String.valueOf(b.getTotalCopies()),
                    String.valueOf(b.getAvailableCopies())
            });
        }
        ConsoleUtils.printTable(headers, rows);
        ConsoleUtils.pressEnterToContinue();
    }

    private static void searchMembers() {
        ConsoleUtils.printHeader("Search Members");
        String query = ConsoleUtils.readString("Enter search keyword: ");
        List<Member> results = service.searchMembers(query);

        String[] headers = {"ID", "Name", "Email", "Phone", "Fines"};
        List<String[]> rows = new ArrayList<>();
        for (Member m : results) {
            rows.add(new String[]{
                    m.getId(),
                    m.getName(),
                    m.getEmail(),
                    m.getPhone(),
                    String.format("$%.2f", m.getUnpaidFines())
            });
        }
        ConsoleUtils.printTable(headers, rows);
        ConsoleUtils.pressEnterToContinue();
    }

    // ==========================================
    // 5. FINES & PAYMENTS MENU
    // ==========================================
    private static void handleFineMenu() {
        while (true) {
            ConsoleUtils.printHeader("Fines & Payments");
            System.out.println("1. View Members Oving Fines");
            System.out.println("2. Pay Fines");
            System.out.println("3. Back to Main Menu");

            int choice = ConsoleUtils.readInt("\nSelect option (1-3): ", 1, 3);
            switch (choice) {
                case 1 -> viewFines();
                case 2 -> processPayment();
                case 3 -> {
                    return;
                }
            }
        }
    }

    private static void viewFines() {
        ConsoleUtils.printHeader("Members Owing Fines");
        List<Member> members = service.getMembers();
        String[] headers = {"ID", "Name", "Phone", "Fines Owed"};
        List<String[]> rows = new ArrayList<>();
        for (Member m : members) {
            if (m.getUnpaidFines() > 0) {
                rows.add(new String[]{
                        m.getId(),
                        m.getName(),
                        m.getPhone(),
                        String.format("$%.2f", m.getUnpaidFines())
                });
            }
        }
        ConsoleUtils.printTable(headers, rows);
        ConsoleUtils.pressEnterToContinue();
    }

    private static void processPayment() {
        ConsoleUtils.printHeader("Pay Fines");
        String memberId = ConsoleUtils.readString("Enter Member ID: ").toUpperCase();
        Member member = service.findMemberById(memberId);
        if (member == null) {
            ConsoleUtils.printError("Member not found.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }

        if (member.getUnpaidFines() <= 0) {
            ConsoleUtils.printInfo("Member has no outstanding fines.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }

        System.out.printf("Member owes: $%.2f%n", member.getUnpaidFines());
        double amount = ConsoleUtils.readDouble("Enter Payment Amount: $", 0.01);

        ServiceResult result = service.payFines(memberId, amount);
        if (result.isSuccess()) {
            ConsoleUtils.printSuccess(result.getMessage());
        } else {
            ConsoleUtils.printError(result.getMessage());
        }
        ConsoleUtils.pressEnterToContinue();
    }
}
