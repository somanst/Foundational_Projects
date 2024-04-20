/**
 Exception thrown when trying to set a value to a variable for a device that doesn't even have it.
 The exception contains an integer value representing a mode that determines the situation this exception was called for.
 */
public class DeviceTypeException extends Exception{
    private int mode;
    public DeviceTypeException(int num){
        this.mode = num;
    }
    public int getMode(){
        return mode;
    }
}
