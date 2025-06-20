import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//This class represents books in our library system, with each object instantiated having its own values for properties.
public class Book {

    //Time format for the program
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //Shows how many books are there in our library system.
    public static int bookCount = 0;

    //ID number of the book.
    private int idNumber;

    //Type of the book, whether its Printed or Handwritten.
    private String type;

    //The date that the book has been taken to be read/borrowed.
    private LocalDate borrowDate;

    //The deadline date of returning the book.
    private LocalDate deadLine;

    //Whether the book's deadline has been extended before or not, 0 for no and 1 for yes.
    private int extended = 0;

    //Constructor method that sets type, ID, and book count after a book is added based on input.
    public Book(int idNumber_, String type_){
        setIdNumber(idNumber_);
        if(type_.equals("P")) setType("P");
        else if(type_.equals("H")) setType("H");
        bookCount++;
    }

    //getters and setters of our variables.
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

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowTime) {
        this.borrowDate = LocalDate.parse(borrowTime,formatter);
    }
    public void setDeadLine(String borrowDate, int duration) {
        this.deadLine = LocalDate.parse(borrowDate,formatter).plusDays(duration);
    }
    public LocalDate getDeadLine() {
        return deadLine;
    }

    //A helper method that nullifies borrowdate and deadline of a book and resets int extended after its returned.
    public void borrowDateNullifier(){
        borrowDate = null;
        deadLine = null;
        extended = 0;
    }
    public int getExtended() {
        return extended;
    }

    //Also a helper method that makes the operation of extending the deadline.
    public void extendMaker(int duration){
        this.deadLine = this.deadLine.plusDays(duration);
        this.extended = 1;
    }

    //This is a method to print a repeated string that represents the object at hand.
    public String toString(){
        return String.format("%s [id: %d]", this.getType().equals("P") ? "Printed" : "Handwritten", this.getIdNumber());
    }


}
