/**
 Exception thrown when trying to set a value to a variable that is out of range or negative/equal to zero.
 The exception contains an integer value representing a mode that determines the situation this exception was called for
 and can be gotten be the method getMode().
 */
public class UnacceptedValueException extends Exception{
    private int errorNumber;
    public UnacceptedValueException(int number){
        this.errorNumber = number;
    }
    public int getNumber() {
        return this.errorNumber;
    }
}
