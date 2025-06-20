/**
 Exception thrown when trying to change the state of a device to a state that it is already in.
 The exception contains an integer value representing a mode that determines the situation this exception was called for
 and can be gotten be the method getMode().
 */
public class DeviceStatusUnchangedException extends Exception{
    private int mode;
    public DeviceStatusUnchangedException(int num){
        this.mode = num;
    }
    public int getMode(){
        return mode;
    }
}
