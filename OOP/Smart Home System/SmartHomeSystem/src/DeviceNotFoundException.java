/**
 Exception thrown when trying to modify or call a device when it hasn't been created or has been removed.
 The exception contains an integer value representing a mode that determines the situation this exception was called for
 and can be gotten be the method getMode().
 */
public class DeviceNotFoundException extends Exception{
    int mode;
    public DeviceNotFoundException(int mode_){
        mode = mode_;
    }
    public int getMode() {
        return mode;
    }

}
