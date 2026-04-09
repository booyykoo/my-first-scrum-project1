package com.epam.assessment.library.persistence;

import com.epam.assessment.library.domain.Book;
import com.epam.assessment.library.domain.Loan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoanRepository {

    private final String filePath;
    private final BookRepository bookRepository;
    private final List<Loan> loans;

    public LoanRepository(String filePath, BookRepository bookRepository) {
        this.filePath = filePath;
        this.bookRepository = bookRepository;
        this.loans = new ArrayList<>();
    }

    public void loadLoans() {
        loans.clear();

        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line == null || line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",", -1);

                if (parts.length != 3 && parts.length != 5) {
                    throw new IllegalArgumentException("Invalid CSV row: " + line);
                }

                long loanId = Long.parseLong(parts[0].trim());
                long bookId = Long.parseLong(parts[1].trim());
                LocalDateTime borrowDate = LocalDateTime.parse(parts[2].trim());

                LocalDateTime returnDate = null;
                Integer rating = null;

                if (parts.length == 5) {
                    if (!parts[3].trim().isEmpty()) {
                        returnDate = LocalDateTime.parse(parts[3].trim());
                    }

                    if (!parts[4].trim().isEmpty()) {
                        rating = Integer.parseInt(parts[4].trim());
                    }
                }

                Book book = bookRepository.findById(bookId)
                        .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

                Loan loan = new Loan(loanId, book, borrowDate, returnDate, rating);
                loans.add(loan);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load loans from file: " + filePath, e);
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new IllegalArgumentException("Invalid data in file: " + filePath, e);
        }
    }


    public List<Loan> getLoans () {
                return loans;
            }

            public Optional<Loan> findById ( long loanId){
                for (Loan loan : loans) {
                    if (loan.getLoanId() == loanId) {
                        return Optional.of(loan);
                    }
                }
                return Optional.empty();
            }

            public long getNextLoanId () {
                if (loans.isEmpty()) {
                    return 1001;
                }

                long maxId = loans.get(0).getLoanId();

                for (Loan loan : loans) {
                    if (loan.getLoanId() > maxId) {
                        maxId = loan.getLoanId();
                    }
                }

                return maxId + 1;
            }
        }