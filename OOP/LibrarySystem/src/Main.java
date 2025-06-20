import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    //Main list of books for the library system, contains all books available and is sorted by ID since the books are
    //always added from ID: 1 to the last ID.
    public static List<Book> bookList = new ArrayList<>();

    //Main list of members for the library system, contains all members available and is sorted by ID since the members
    //are always added from ID: 1 to the last ID.
    public static List<Member> memberList = new ArrayList<>();

    //A method that gets index of suitable response for borrowing from method borrowChecker in class Member and gives
    //the corresponding message.
    public static void borrowBook(Member member, Book book, String borrowDate, FileWriter w) throws IOException {
        int borrowValidityIndex = member.borrowChecker(book, borrowDate);
        if(borrowValidityIndex == -1) w.write("You have exceeded the borrowing limit!\n");
        else if(borrowValidityIndex == 0) w.write("You cannot borrow this book!\n");
        else w.write(String.format("The book [%d] was borrowed by member [%d] at %s\n", book.getIdNumber(), member.getIdNumber(), borrowDate));
    }

    //A method for returning books that gets fee for late deadline (or 0 if non) returning from method bookReturner in
    //class Member and gives a message, if fee is -1 that means that the method most likely identified that the member
    //didn't even borrow/read the mentioned book.
    public static void returnBook(Member member, Book book, String returnDate, FileWriter w) throws IOException {
        long fee = member.bookReturner(book, returnDate);
        if(fee == -1) w.write("You cannot return this book!\n");
        else w.write(String.format("The book [%d] was returned by member [%d] at %s Fee: %d\n", book.getIdNumber(), member.getIdNumber(), returnDate, fee));
    }

    //A method specified in the read in library action that gets the needed index of the situation from the method
    //readChecker and prints the needed message.
    public static void readBook(Member member, Book book, String readDate, FileWriter w) throws IOException {
        int readIndex = member.readChecker(book, readDate);
        if(readIndex == -1) w.write("Students can not read handwritten books!\n");
        else if (readIndex == 0) w.write("You can not read this book!\n");
        else if (readIndex == 1) w.write(String.format("The book [%d] was read in library by member [%d] at %s\n", book.getIdNumber(), member.getIdNumber(), readDate));
    }

    //Just like in the previous methods, this one does the same thing, but gets its index from the method bookExtender
    public static void extendBook(Member member, Book book, String currentDate, FileWriter w) throws IOException{
        int canExtend = member.bookExtender(book, currentDate);
        if(canExtend == -2) w.write("Book wasn't even borrowed!\n");
        else if(canExtend == -1) w.write("Deadline has already been passed!\n");
        else if(canExtend == 0) w.write("You cannot extend the deadline!\n");
        else {
            w.write(String.format("The deadline of book [%d] was extended by member [%d] at %s\n",
                    book.getIdNumber(), member.getIdNumber(), currentDate));
            w.write(String.format("New deadline of book [%d] is %s\n", book.getIdNumber(), book.getDeadLine()));
            }
    }

    //This method will get the numerical stats of the library using enhanced for loops and return them in a list.
    public static List<Integer> statsCalculator(){
        int studentCount = 0;
        int academicsCount = 0;
        int printedBooksCount = 0;
        int handWrittenBooksCount = 0;
        int booksBorrowedCount = 0;
        int booksBeingReadCount = 0;

        for(Member member : memberList){
            if(member.getType().equals("S")) studentCount++;
            else if(member.getType().equals("A")) academicsCount++;
            booksBorrowedCount += member.getBorrowedBooks().size();
            booksBeingReadCount += member.getBooksReadInLibrary().size();
        }
        for(Book book : bookList){
            if(book.getType().equals("P")) printedBooksCount++;
            else if (book.getType().equals("H")) handWrittenBooksCount++;
        }
        return Arrays.asList(studentCount, academicsCount, printedBooksCount,
                handWrittenBooksCount, booksBorrowedCount, booksBeingReadCount);
    }

    //This method however, will return the stats of library that are in shape of strings, like members, books registered
    //and so on..
    public static List<String> listPrinter(){
        String studentString = "";
        String academicString = "";
        String borrowedBooksString = "";
        String readBooksString = "";
        String printedBooksString = "";
        String handwrittenBooksString = "";

        for(Member member : memberList){
            if(member.getType().equals("S")) studentString += member + "\n";
            else if(member.getType().equals("A")) academicString += member + "\n";
            for(Book borrowedBook : member.getBorrowedBooks()){
                borrowedBooksString += String.format("The book [%d] was borrowed by member [%d] at %s\n",
                        borrowedBook.getIdNumber(), member.getIdNumber(), borrowedBook.getBorrowDate());
            }
            for(Book readBook : member.getBooksReadInLibrary()){
                readBooksString += String.format("The book [%d] was read in library by member [%d] at %s\n",
                        readBook.getIdNumber(), member.getIdNumber(), readBook.getBorrowDate());
            }
        }
        for(Book book : bookList){
            if(book.getType().equals("P")) printedBooksString += book + "\n";
            else if(book.getType().equals("H")) handwrittenBooksString += book + "\n";
        }
        return Arrays.asList(studentString, academicString, printedBooksString, handwrittenBooksString,
                borrowedBooksString, readBooksString);

    }

    //The primary method of the program that gets the command of the user, checks if it is valid and check what it wants,
    //then call the appropriate method and/or register new books/members with 6-digit ID limitation.
    public static void commandExecuter(String command, FileWriter w) throws IOException {
        String[] splitCommand = command.split("\t");
        if(splitCommand[0].equals("addBook")){
            if(Book.bookCount > 999999){
                w.write("Max number of books has been exceeded\n");
                return;
            }
            Book book = new Book(Book.bookCount + 1, splitCommand[1]);
            bookList.add(book);
            w.write(String.format("Created new book: %s\n", book));
        } else if (splitCommand[0].equals("addMember")) {
            if(Member.memberCount > 999999){
                w.write("Max number of members has been exceeded\n");
                return;
            }
            Member member = new Member(Member.memberCount + 1, splitCommand[1]);
            memberList.add(member);
            w.write(String.format("Created new member: %s\n", member));
        } else if(splitCommand[0].equals("borrowBook")) {
            if(Book.bookCount < Integer.parseInt(splitCommand[1])){
                w.write("Book with ID " + splitCommand[1] + " doesn't seem to exist!!\n");
                return;
            }
            if(Member.memberCount < Integer.parseInt(splitCommand[2])){
                w.write("Member with ID " + splitCommand[2] + " doesn't seem to exist!!\n");
                return;
            }
            Book borrowedBook = bookList.get(Integer.parseInt(splitCommand[1]) - 1);
            Member borrowingMember = memberList.get(Integer.parseInt(splitCommand[2]) - 1);
            borrowBook(borrowingMember, borrowedBook, splitCommand[3], w);
        } else if (splitCommand[0].equals("returnBook")) {
            if(Book.bookCount < Integer.parseInt(splitCommand[1])){
                w.write("Book with ID " + splitCommand[1] + " doesn't seem to exist!!\n");
                return;
            }
            if(Member.memberCount < Integer.parseInt(splitCommand[2])){
                w.write("Member with ID " + splitCommand[1] + " doesn't seem to exist!!\n");
                return;
            }
            Book returnedBook = bookList.get(Integer.parseInt(splitCommand[1]) - 1);
            Member returningMember = memberList.get(Integer.parseInt(splitCommand[2]) - 1);
            returnBook(returningMember, returnedBook, splitCommand[3], w);
        } else if (splitCommand[0].equals("extendBook")) {
            if(Book.bookCount < Integer.parseInt(splitCommand[1])){
                w.write("Book with ID " + splitCommand[1] + " doesn't seem to exist!!\n");
                return;
            }
            if(Member.memberCount < Integer.parseInt(splitCommand[2])){
                w.write("Member with ID " + splitCommand[1] + " doesn't seem to exist!!\n");
                return;
            }
            Book extendedBook = bookList.get(Integer.parseInt(splitCommand[1]) - 1);
            Member extendingMember = memberList.get(Integer.parseInt(splitCommand[2]) - 1);
            extendBook(extendingMember, extendedBook, splitCommand[3], w);
        } else if (splitCommand[0].equals("readInLibrary")) {
            if(Book.bookCount < Integer.parseInt(splitCommand[1])){
                w.write("Book with ID " + splitCommand[1] + " doesn't seem to exist!!\n");
                return;
            }
            if(Member.memberCount < Integer.parseInt(splitCommand[2])){
                w.write("Member with ID " + splitCommand[1] + " doesn't seem to exist!!\n");
                return;
            }
            Book readBook = bookList.get(Integer.parseInt(splitCommand[1]) - 1);
            Member readingMember = memberList.get(Integer.parseInt(splitCommand[2]) - 1);
            readBook(readingMember, readBook, splitCommand[3], w);
        } else if (splitCommand[0].equals("getTheHistory")) {
            List<Integer> libraryStats = statsCalculator();
            List<String> libraryLists = listPrinter();
            w.write("History of library:\n\n");
            w.write("Number of students: " + libraryStats.get(0) + "\n");
            w.write(libraryLists.get(0) + "\n");
            w.write("Number of academics: " + libraryStats.get(1) + "\n");
            w.write(libraryLists.get(1) + "\n");
            w.write("Number of printed books: " + libraryStats.get(2) + "\n");
            w.write(libraryLists.get(2) + "\n");
            w.write("Number of handwritten books: " + libraryStats.get(3) + "\n");
            w.write(libraryLists.get(3) + "\n");
            w.write("Number of borrowed books: " + libraryStats.get(4) + "\n");
            w.write(libraryLists.get(4) + "\n");
            w.write("Number of books read in library: " + libraryStats.get(5) + "\n");
            w.write(libraryLists.get(5));
        } else{
            w.write("Invalid command!\n");
        }
    }

    //Our main method that we get the input file from, create a filewriter and a file to write to, and loop through the
    //string array to use it as a parameter for our primary method commandExecuter.
    public static void main(String[] args) throws IOException {
        String[] lines = ReadFromFile.readFile(args[0]);
        File output = new File(args[1]);
        FileWriter w = new FileWriter(output);
        for(String line : lines){
            commandExecuter(line, w);
        }
        w.close();
    }
}