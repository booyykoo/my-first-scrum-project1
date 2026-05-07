package com.epam.assessment.library.presentation;


import com.epam.assessment.library.domain.Loan;
import com.epam.assessment.library.service.BookLoanService;
import com.epam.assessment.library.service.response.BookUsage;
import com.epam.assessment.library.service.response.RatedBook;

import java.util.List;
import java.util.Scanner;


public class UserInterface {

    private final BookLoanService bookLoanService;
    private final Scanner scanner;

    public UserInterface(BookLoanService bookLoanService) {
        this.bookLoanService = bookLoanService;
        this.scanner = new Scanner(System.in);
    }

    public void interact() {
        boolean running = true;

        while (running) {
            System.out.println("Library Manager Menu:");
            System.out.println("1. Query top rated books");
            System.out.println("2. Query most wanted books");
            System.out.println("3. Create loan");
            System.out.println("4. Return book");
            System.out.println("0. Exit");
            System.out.print("Select a menu option: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    showTopRatedBooks();
                    break;
                case "2":
                    showMostWantedBooks();
                    break;
                case "3":
                    createLoan();
                    break;
                case "4":
                    returnBook();
                    break;
                case "0":
                    System.out.println("Exiting application. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    System.out.println("test");
            }
        }
    }

    private void showTopRatedBooks() {
        List<RatedBook> books = bookLoanService.queryTopRatedBooks(3);

        System.out.println("Top 3 rated books:");
        for (RatedBook ratedBook : books) {
            System.out.println(
                    ratedBook.book().getTitle() + " (" +
                            ratedBook.book().getAuthor() + "), Avg Rating: " +
                            String.format("%.2f", ratedBook.rating())
            );
        }
    }

    private void showMostWantedBooks() {
        List<BookUsage> books = bookLoanService.queryMostWantedBooks(3);

        System.out.println("Top 3 most wanted books:");
        for (BookUsage bookUsage : books) {
            System.out.println(
                    bookUsage.book().getTitle() + " (" +
                            bookUsage.book().getAuthor() + "), Borrowed: " +
                            bookUsage.count() + " times"
            );
        }
    }

    private void createLoan() {
        Long bookId = readLong("Enter book ID: ");
        if (bookId == null) {
            return;
        }

        try {
            Loan loan = bookLoanService.createLoan(bookId);
            System.out.println("Loan created successfully.");
            System.out.println(
                    "Loan ID: " + loan.getLoanId() +
                            ", Book: " + loan.getBook().getTitle() +
                            " (" + loan.getBook().getAuthor() + ")" +
                            ", Borrow Date: " + loan.getBorrowDate()
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to enter loan: " + e.getMessage());
        }
    }

    private void returnBook() {
        Long loanId = readLong("Enter loan ID to return: ");
        if (loanId == null) {
            return;
        }

        Integer rating = readInt("Enter rating (1-5): ");
        if (rating == null) {
            return;
        }

        try {
            bookLoanService.returnBook(loanId, rating);
            System.out.println("Book returned successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to return book: " + e.getMessage());
        }
    }

    private Long readLong(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();

            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number");
            }
        }
    }

    private Integer readInt(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number");
            }
        }
    }
}
