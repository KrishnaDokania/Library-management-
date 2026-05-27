package com.library.repository;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;
import com.library.util.CSVUtils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {
    private static final String DATA_DIR = "data";
    private static final Path BOOKS_FILE = Paths.get(DATA_DIR, "books.csv");
    private static final Path MEMBERS_FILE = Paths.get(DATA_DIR, "members.csv");
    private static final Path TRANSACTIONS_FILE = Paths.get(DATA_DIR, "transactions.csv");

    public FileStorage() {
        initStorage();
    }

    private void initStorage() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            if (!Files.exists(BOOKS_FILE)) {
                writeHeader(BOOKS_FILE, "isbn,title,author,genre,totalCopies,availableCopies");
            }
            if (!Files.exists(MEMBERS_FILE)) {
                writeHeader(MEMBERS_FILE, "id,name,email,phone,unpaidFines");
            }
            if (!Files.exists(TRANSACTIONS_FILE)) {
                writeHeader(TRANSACTIONS_FILE, "transactionId,memberId,isbn,borrowDate,dueDate,returnDate,fineAssessed");
            }
        } catch (IOException e) {
            System.err.println("Error initializing storage directory: " + e.getMessage());
        }
    }

    private void writeHeader(Path file, String header) throws IOException {
        Files.writeString(file, header + System.lineSeparator(), StandardOpenOption.CREATE);
    }

    // --- BOOKS ---
    public List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(BOOKS_FILE)) {
            String line = br.readLine(); // read header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = CSVUtils.parseCSVLine(line);
                if (fields.length >= 6) {
                    try {
                        String isbn = fields[0];
                        String title = fields[1];
                        String author = fields[2];
                        String genre = fields[3];
                        int totalCopies = Integer.parseInt(fields[4]);
                        int availableCopies = Integer.parseInt(fields[5]);
                        books.add(new Book(isbn, title, author, genre, totalCopies, availableCopies));
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping malformed book record: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading books data: " + e.getMessage());
        }
        return books;
    }

    public void saveBooks(List<Book> books) {
        try (BufferedWriter bw = Files.newBufferedWriter(BOOKS_FILE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            bw.write("isbn,title,author,genre,totalCopies,availableCopies");
            bw.newLine();
            for (Book book : books) {
                String[] fields = {
                        book.getIsbn(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        String.valueOf(book.getTotalCopies()),
                        String.valueOf(book.getAvailableCopies())
                };
                bw.write(CSVUtils.toCSVLine(fields));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing books data: " + e.getMessage());
        }
    }

    // --- MEMBERS ---
    public List<Member> loadMembers() {
        List<Member> members = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(MEMBERS_FILE)) {
            String line = br.readLine(); // read header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = CSVUtils.parseCSVLine(line);
                if (fields.length >= 5) {
                    try {
                        String id = fields[0];
                        String name = fields[1];
                        String email = fields[2];
                        String phone = fields[3];
                        double unpaidFines = Double.parseDouble(fields[4]);
                        members.add(new Member(id, name, email, phone, unpaidFines));
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping malformed member record: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading members data: " + e.getMessage());
        }
        return members;
    }

    public void saveMembers(List<Member> members) {
        try (BufferedWriter bw = Files.newBufferedWriter(MEMBERS_FILE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            bw.write("id,name,email,phone,unpaidFines");
            bw.newLine();
            for (Member member : members) {
                String[] fields = {
                        member.getId(),
                        member.getName(),
                        member.getEmail(),
                        member.getPhone(),
                        String.valueOf(member.getUnpaidFines())
                };
                bw.write(CSVUtils.toCSVLine(fields));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing members data: " + e.getMessage());
        }
    }

    // --- TRANSACTIONS ---
    public List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(TRANSACTIONS_FILE)) {
            String line = br.readLine(); // read header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = CSVUtils.parseCSVLine(line);
                if (fields.length >= 7) {
                    try {
                        String transactionId = fields[0];
                        String memberId = fields[1];
                        String isbn = fields[2];
                        LocalDate borrowDate = LocalDate.parse(fields[3]);
                        LocalDate dueDate = LocalDate.parse(fields[4]);
                        LocalDate returnDate = fields[5].isEmpty() ? null : LocalDate.parse(fields[5]);
                        double fineAssessed = Double.parseDouble(fields[6]);
                        transactions.add(new Transaction(transactionId, memberId, isbn, borrowDate, dueDate, returnDate, fineAssessed));
                    } catch (NumberFormatException | DateTimeParseException e) {
                        System.err.println("Skipping malformed transaction record: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading transactions data: " + e.getMessage());
        }
        return transactions;
    }

    public void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter bw = Files.newBufferedWriter(TRANSACTIONS_FILE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            bw.write("transactionId,memberId,isbn,borrowDate,dueDate,returnDate,fineAssessed");
            bw.newLine();
            for (Transaction transaction : transactions) {
                String[] fields = {
                        transaction.getTransactionId(),
                        transaction.getMemberId(),
                        transaction.getIsbn(),
                        transaction.getBorrowDate().toString(),
                        transaction.getDueDate().toString(),
                        transaction.getReturnDate() == null ? "" : transaction.getReturnDate().toString(),
                        String.valueOf(transaction.getFineAssessed())
                };
                bw.write(CSVUtils.toCSVLine(fields));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing transactions data: " + e.getMessage());
        }
    }
}
