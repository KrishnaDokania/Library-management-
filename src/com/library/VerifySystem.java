package com.library;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;
import com.library.service.LibraryService;
import com.library.service.LibraryService.ServiceResult;

import java.time.LocalDate;
import java.util.List;

public class VerifySystem {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Library Management System programmatic test run ");
        System.out.println("==================================================");

        // 1. Instantiate service (loads clean state if empty, or loads existing)
        LibraryService service = new LibraryService();

        // 2. Register a test book
        String testIsbn = "978-0-13-468599-1";
        Book existingBook = service.findBookByIsbn(testIsbn);
        if (existingBook != null) {
            service.removeBook(testIsbn);
        }

        Book book = new Book(testIsbn, "Effective Java (3rd Edition)", "Joshua Bloch", "Programming", 3, 3);
        ServiceResult bookResult = service.addBook(book);
        assertResult(bookResult, true, "Add test book");

        // 3. Register a test member
        String testMemberId = "TEST001";
        Member existingMember = service.findMemberById(testMemberId);
        if (existingMember != null) {
            service.removeMember(testMemberId);
        }

        Member member = new Member(testMemberId, "Testy McTestface", "testy@example.com", "+1-555-0199", 0.0);
        ServiceResult memberResult = service.addMember(member);
        assertResult(memberResult, true, "Register test member");

        // 4. Borrow book
        ServiceResult borrowResult = service.borrowBook(testMemberId, testIsbn);
        assertResult(borrowResult, true, "Borrow book first copy");

        // Verify book copies decreased
        Book updatedBook = service.findBookByIsbn(testIsbn);
        if (updatedBook.getAvailableCopies() == 2) {
            System.out.println("[PASS] Book available copies decremented correctly to 2.");
        } else {
            System.err.println("[FAIL] Book available copies expected 2, got " + updatedBook.getAvailableCopies());
        }

        // 5. Try borrowing book again (should block duplicate borrowing of same book)
        ServiceResult dupBorrowResult = service.borrowBook(testMemberId, testIsbn);
        assertResult(dupBorrowResult, false, "Block duplicate borrowing");

        // 6. Return book
        ServiceResult returnResult = service.returnBook(testMemberId, testIsbn);
        assertResult(returnResult, true, "Return book");

        // Verify book copies increased
        updatedBook = service.findBookByIsbn(testIsbn);
        if (updatedBook.getAvailableCopies() == 3) {
            System.out.println("[PASS] Book available copies incremented back to 3.");
        } else {
            System.err.println("[FAIL] Book available copies expected 3, got " + updatedBook.getAvailableCopies());
        }

        // 7. Clean up
        service.removeBook(testIsbn);
        service.removeMember(testMemberId);
        System.out.println("==================================================");
        System.out.println("  All programmatic checks finished successfully.  ");
        System.out.println("==================================================");
    }

    private static void assertResult(ServiceResult result, boolean expectedSuccess, String actionName) {
        if (result.isSuccess() == expectedSuccess) {
            System.out.printf("[PASS] %s: %s (Expected: %s)%n", actionName, result.getMessage(), expectedSuccess);
        } else {
            System.err.printf("[FAIL] %s: %s (Expected: %s, Got: %s)%n", actionName, result.getMessage(), expectedSuccess, result.isSuccess());
            System.exit(1);
        }
    }
}
