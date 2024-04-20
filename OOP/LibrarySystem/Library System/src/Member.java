import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//This class is going to be the one that defines objects of members that are registered in our library system which will
//be useful to save information of each member.
public class Member {

    //Time format for the program
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //Days that a member can borrow a book without getting a fee based on registration type.
    private int borrowDaysAllowed;

    //Count of members in our library system.
    public static int memberCount = 0;

    //ID number of the member.
    private int idNumber;

    //Type whether its "A" for Academic or "S" for student.
    private String type;

    //A list of the books the members has borrowed.
    private final List<Book> borrowedBooks = new ArrayList<>();

    //A list of the books the member is reading in the library.
    private final List<Book> booksReadInLibrary = new ArrayList<>();

    //Constructor method that sets type, ID, and member count after a member is added based on input.
    public Member(int idNumber_, String type_){
        setIdNumber(idNumber_);
        if(type_.equals("S")) {
            setType("S");
            borrowDaysAllowed = 7;
        }
        else if(type_.equals("A")) {
            setType("A");
            borrowDaysAllowed = 14;
        }
        memberCount++;
    }

    //public setters and getters for our variables.
    public int getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public List<Book> getBorrowedBooks(){
        return borrowedBooks;
    }
    public List<Book> getBooksReadInLibrary(){
        return booksReadInLibrary;
    }

    //This method will check whether a member is eligible for borrowing a specific book based on his type, number of
    //books he has already borrowed, book type, and if the book is already being used, and return an index referring to
    //a suitable response for the request.
    public int borrowChecker(Book book, String borrowDate){
        int canBorrow = 1;
        if(getType().equals("S") && this.borrowedBooks.size() == 2 || getType().equals("A") && this.borrowedBooks.size() == 4){
            canBorrow = -1;
        } else if (book.getType().equals("H") || book.getBorrowDate() != null) {
            canBorrow = 0;
        } else {
            borrowedBooks.add(book);
            book.setBorrowDate(borrowDate);
            book.setDeadLine(borrowDate, borrowDaysAllowed);
        }
        return canBorrow;
    }

    //Here we are able to check if a member is able to read a book in the library based on type of member/book, and book
    //is borrowed already or not, and will give an index to give a suitable message as well.
    public int readChecker(Book book, String readDate){
        int canRead = 1;
        if(getType().equals("S") && book.getType().equals("H")){
            canRead = -1;
        } else if (book.getBorrowDate() != null) {
            canRead = 0;
        } else{
            booksReadInLibrary.add(book);
            book.setBorrowDate(readDate);
        }
        return canRead;
    }

    //For this method, we are checking if a member can return his book by checking if he even borrowed it or is reading
    //it. Then if found, calculate the fee of passing deadline, removing the book from the borrowedbooks/booksreadinlibrary
    //list, nullifying the borrowdate of the book and returning the fee at the end.
    public long bookReturner(Book book, String returnDate){
        long fee = -1;
        for(Book borrowed : borrowedBooks){
            if(borrowed.getIdNumber() == book.getIdNumber()){
                fee = Duration.between(book.getDeadLine().atStartOfDay() , LocalDate.parse(returnDate,formatter).atStartOfDay()).toDays();
                if(fee < 0) fee = 0;
                borrowedBooks.remove(borrowed);
                book.borrowDateNullifier();
                break;
            }
        }
        for(Book read : booksReadInLibrary){
            if (book.getIdNumber() == read.getIdNumber()) {
                booksReadInLibrary.remove(read);
                book.borrowDateNullifier();
                fee = 0;
                break;
            }
        }
        return fee;
    }

    //Here just like in the previous methods, we are taking a decision in what suitable message is going to be
    //provided based on the situation while extending book deadline. So that we check if the member even has the book,
    //check if the time has passed already, if the deadline has been previously extended, or if it is normal so it can
    //extend it with a duration based on the type of the member.
    public int bookExtender(Book extendedBook, String currentDate){
        int canExtend = 1;
        if(!borrowedBooks.contains(extendedBook)){
            canExtend = -2;
        }
        else if(extendedBook.getDeadLine().isBefore(LocalDate.parse(currentDate, formatter))) canExtend = -1;
        else if(extendedBook.getExtended() == 1) canExtend = 0;
        else {
            for(Book book : borrowedBooks){
                if(book.getIdNumber() == extendedBook.getIdNumber()){
                    book.extendMaker(borrowDaysAllowed);
                }
            }
        }
        return canExtend;
    }

    //This is a method to print a repeated string that represents the object at hand.
    public String toString(){
        return String.format("%s [id: %d]", this.getType().equals("A") ? "Academic" : "Student", this.getIdNumber());
    }
}
