# Application Introduction

The **"Book Loan"** application helps library staff manage book loans and analyze book popularity.

The application tracks books and loans in a simple data model:
- **Books** contain information like ISBN, author, title, and genre.
- **Loans** record when a book is borrowed, when it's returned, and the rating given by the reader.

Example of a book:
- id: 1
- ISBN: 978-0451524935
- Author: Jane Austen
- Title: Pride and Prejudice
- Genre: FICTION

Example of a loan:
- Book: Pride and Prejudice (Jane Austen)
- Borrow Date: 2025-11-03 10:15:00
- Return Date: 2025-11-15 16:20:00
- Rating: 5


The application allows library staff to:
- Query the top-rated books based on reader ratings.
- Identify the most wanted books based on how often they were borrowed.
- Enter a new loan when a reader borrows a book.
- Process book returns and record ratings.

Your task is to implement this application according to the specifications below.


# Application Lifecycle

Here are the main steps from application start to stop:

1. When the application starts, it loads **Books** and **Loans** from CSV files.
2. The application displays a menu of functions that the user can do in the application.
   The user can repeatedly select some function that the application processes until they choose to exit.
5. When the user exits, the application saves any new or updated data back to the CSV files.


## Data Representation - CSV

The application stores data in two CSV files: one for books and one for loans.

### Books CSV

The books CSV file has the following format (first row is the header):

```
bookId,ISBN,author,title,genre
1,978-0451524935,Jane Austen,Pride and Prejudice,FICTION
2,978-0261103573,J. R. R. Tolkien,The Lord of the Rings,FANTASY
3,978-0062316110,Antoine de Saint-Exupéry,The Little Prince,FICTION
4,978-0553380163,J. K. Rowling,Harry Potter and the Philosopher's Stone,FICTION
```

Notes:
- ISBN format is not validated; any string is accepted.
- All fields are required.

### Loans CSV

The loans CSV file has the following format (first row is the header):

```
loanId,bookId,borrowDate,returnDate,rating
1001,1,2025-11-03T10:15:00,2025-11-15T16:20:00,5
1002,1,2025-11-05T09:00:00,2025-11-12T14:45:00,3
1003,2,2025-11-07T13:30:00,2025-11-21T11:10:00,5
1008,4,2025-11-20T10:10:00,,
```

Notes:
- Date format: ISO 8601 (`YYYY-MM-DDThh:mm:ss`)
- The `returnDate` and `rating` fields can be empty if the book has not been returned yet.
- `bookId` references a book from the books CSV file.


# User Interface

The user interface is command-line based.

## Main Menu

When the application runs, it displays a menu:

```
Library Manager Menu:
1. Query top rated books
2. Query most wanted books
3. Enter loan
4. Return book
0. Exit
Select a menu option: 
```

## Query Top Rated Books

When the user selects option 1, the application displays the top 3 books with the highest average rating.

Example output:
```
Book: Pride and Prejudice, Avg Rating: 4.00
Book: The Lord of the Rings, Avg Rating: 4.50
Book: The Little Prince, Avg Rating: 4.33
```

Details of the calculation will be covered in the service layer chapter.

## Query Most Wanted Books

When the user selects option 2, the application displays the top 3 most borrowed books.

Example output:
```
Book: The Little Prince, Borrowed: 3 times
Book: Harry Potter and the Philosopher's Stone, Borrowed: 2 times
Book: Pride and Prejudice, Borrowed: 2 times
```

Details of the calculation will be covered in the service layer chapter.

## Enter Loan

When the user selects option 3, the application prompts for a book ID and creates a new loan.

Example interaction:
```
Enter book ID: 1
Loan created successfully.
Loan ID: 1010, Book author: Jane Austen, Book title: Pride and Prejudice, Borrow Date: 2026-03-08T14:30:00
```

Error handling
- If the user enters a book ID that is not a valid number, display an error message: `Invalid number.`,
  and asks the user to repeat.
- If some error happens during loan creation (see details later), display:
  `Failed to enter loan: ` + error message. And returns to the main menu.

## Return Book

When the user selects option 4, the application prompts for a loan ID and rating, then marks the book as returned.

Example interaction:
```
Enter loan ID to return: 1008
Enter rating (1-5): 5
Book returned successfully.
```

Error handling
- If the user enters a loan ID or rating that is not a valid number, display an error message: `Invalid number.`,
  and asks the user to repeat.
- If some error happens during loan return (see details later), display:
  `Failed to return book: ` + error message. And returns to the main menu.

## Exit

When the user selects option 0, the application saves the updated loans to the CSV file and exits.

```
Exiting application. Goodbye!
```

# Implementation

Instructions for implementing the application:

## Application and Package Structure

Base package: `com.epam.assessment.library`

The application classes should be organized into the following sub-packages according to their responsibilities:
- `domain` - Domain model classes
- `persistence` - Persistence (or Data Access) layer
- `service` - Service layer
- `presentation` - Presentation layer

## Application Class

The `Application` class is the entry point and orchestrates the application components.
The class exists, and you do not have to modify it.


## Domain Model Classes

The classes are located in `com.epam.assessment.library.domain` package.
The classes (`Book` and `Loan` and `Genre` enum) are partially implemented. You can add and modify them as needed.


## Persistence Layer

Classes of the persistence layer are responsible for loading and saving data from/to CSV files.

### BookRepository Class

**Constructor:**
- `filePath` argument - path to the books CSV file

**Methods:**

`void loadBooks()` method:
- Opens the file at `filePath`.
- Reads each line (skipping the header).
- Stores the books in a collection.
- If some `IOException` happens while it processing the file, wraps into `RuntimeException` with a meaningful message.
- If the format of a CSV row is invalid, throws `IllegalArgumentException` with a meaningful message.

`Optional<Book> findById(long bookId)` method:
- Searches the internal book collection for a book with the given ID.
- Returns `Optional.of(book)` if found, otherwise `Optional.empty()`.

### LoanRepository Class

**Constructor:**
- `filePath` argument - path to the loans CSV file
- bookRepository argument - used for resolving book references by book id

**Methods:**

`void loadLoans()` method:
- Implementation and requirements are similar to `BookRepository.loadBooks()`,
  but with additional logic for parsing loans and resolving book references.
  If a loan references a book ID (e.g. 1), it must call `bookRepository.findById()` method to get the Book object,
  which is **Pride and Prejudice** in this case.
- If the book does not exist, it should throw `IllegalArgumentException` with a meaningful message.

`List<Loan> getLoans()` method:
- Returns the list of all loans.

`Optional<Loan> findById(long loanId)` method:
- Similar to `BookRepository.findById()`, but for loans.

`long getNextLoanId()` method:
- Finds the maximum loan ID in the current loan list. Returns `maxId + 1`.
- If the list is empty, returns 1001 (initial loan ID).


## Service Layer

The single class of the service layer contains all the business logic of the application.

### BookLoanService Class

**Constructor:**
- `BookLoanService(BookRepository , LoanRepository loanRepository)`
- `bookRepository` - used to perform book data access operations
- `loanRepository` - used to perform loan data access operations

**Methods:**

`List<RatedBook> queryTopRatedBooks(int topN)` method:
- For each book it calculates the average rating based on the loans that have a rating for that book.
- Return the top N books with the highest average rating.

The example csv file contains to entries for book id=1, ratings are 5 and 3,
so the average rating is `(5 + 3) / 2 = 4`.

If a book has no ratings, it should not be included in the result.

The return type `RatedBook` is a record that contains the `Book` and its average rating.

`List<BookUsage> queryMostWantedBooks(int topN)` method:
- Counts the number of loans for each book. Sorts books by loan count in descending order,
  and returns the top N books with the highest loan count.

The return type `BookUsage` is a record that contains the `Book` and the count of how many times it was borrowed.

`Loan createLoan(long bookId)` method:
- Creates a new loan for the book with the given ID.
- The `Loan` object must be initialized with:
    - `loanId`: generated ID, use `loanRepository.getNextLoanId()`
    - `book`: looked-up book, use `bookRepository.findById(bookId)` to resolve
    - `borrowDate`: current system date/time
    - `returnDate`: null
    - `rating`: null
- Adds the created loan object to the repository using, and returns the created loan.

Error cases
- If the book does not exist, throws `IllegalArgumentException` with message: `"Book not found with ID: " + bookId`

`void returnBook(long loanId, int rating)` method:
- Returns a book for the loan with the given ID and sets the rating.
- Looks up the loan using `loanRepository.findById(loanId)`.
  - Sets the `returnDate` to the current system date/time.
  - Sets the `rating` to the provided value.

Error cases:
- If the rate argument is out of range (between 1 and 5 inclusive), throw `IllegalArgumentException`.
- If the loan does not exist, throws `IllegalArgumentException` with message: `"Loan not found with ID: " + loanId`.
- If the loan already has a return date (i.e. the book is already returned),
  throws `IllegalStateException` with message: `"Loan with ID: " + loanId + " is already returned"`.

## Presentation Layer

The single class of the presentation layer is responsible for user interactions.


### UserInterface Class

**Constructor:**
- `bookLoanService` - used to perform service layer operations

**Methods:**

`void interact()` method:
- This is the main loop of all user interactions.

Configurations
- When you call `queryTopRatedBooks()` and `queryMostWantedBooks()` service methods,
  pass `3` as the `topN` argument to get the top 3 books.


# Testing

The template project includes some JUnit test classes for testing the application components:

- `BookRepositoryTest` for testing book loading from CSV.
- `LoanRepositoryTest` for testing loan loading from CSV.
- `BookLoanServiceTest` for testing service layer methods.

## Running Tests Locally

You can run tests locally using your IDE or using Maven from the command line.

Make sure all tests pass before submitting your implementation.
Autocode platform will perform automated testing using the same test classes,
so it's crucial that your implementation is correct and all tests pass.

Note:
- The tests cover only a few cases. Passing all tests does not guarantee that your implementation is fully correct,
- Make sure to follow the specifications carefully, and handle all edge cases as described.
- Run your implementation and test it manually to ensure that the user interface works as expected,
  and that all functionalities are correctly implemented.

# Restrictions

- Do not change the provided test classes.
- Do not change the content of the CSV files in the `test_data` directory, as they are used in the tests.
  You are free to change the content of the CSV files in the `data` directory.

# Summary

Good luck, and enjoy implementing the "Book Loan" application!
