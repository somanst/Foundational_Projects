import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 This is an abstract class representing a smart device.
 It includes functionality for managing the switch state, switch date, name, and last switched on date.
 */


abstract class SmartDevice {
    /**
     The formatter used to format the date and time for the switch date.
     */
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    /**
     The name of the smart device.
     */
    private String name;

    /**
     The current switch state of the device. 0 for off, 1 for on. and -1 for not assigned
     */
    private int switchState = -1;

    /**
     The date and time the switch will happen.
     */
    private LocalDateTime switchDate;

    /**
     The date and time the device was last switched on.
     */
    private LocalDateTime lastSwitchedOn;

    /**
     Abstract method for printing device info.
     @param w The FileWriter object to write to.
     @throws Exception If an error occurs while writing to the file.
     */
    abstract void infoTyper(FileWriter w) throws Exception;
    public SmartDevice(){

    }

    /**
     Constructor for SmartDevice class.
     @param name The name of the smart device.
     @param switchState The current switch state of the device.
     @param time The time the switch state was set.
     @throws Exception If an error occurs during device initialization.
     */
    public SmartDevice(String name, String switchState, LocalDateTime time) throws Exception{
        setName(name);
        setSwitchState(switchState, time);
    }

    /**
     Sets the switch state of the device and gives an error if nothing changes, as well as assigning lastSwitchedOn;
     @param state The new switch state.
     @param time The time the switch state was set.
     @throws Exception If an error occurs while setting the switch state.
     */
    public void setSwitchState(String state, LocalDateTime time) throws Exception{
        int tempState;
        if(state.trim().equals("On")) {
            if (getSwitchState() == 1) throw new Exception();
            this.lastSwitchedOn = time;
            tempState = 1;
        }
        else if (state.trim().equals("Off")) {
            if (this.switchState == 0) throw new DeviceStatusUnchangedException(1);
            tempState = 0;
        }
        else throw new Exception();
        this.switchState = tempState;
    }
    /**
     Gets the current switch state of the device.
     @return The current switch state.
     */
    public int getSwitchState(){
        return this.switchState;
    }

    /**
     Sets the switchDate of the device.
     @param timeStr The switch date and time as a formatted string.
     @param curTime The current date and time.
     @throws Exception If an error occurs while setting the switch date.
     */
    public void setSwitchDate(String timeStr, LocalDateTime curTime)throws Exception{
        LocalDateTime time = LocalDateTime.parse(timeStr, formatter);
        if(time.isBefore(curTime)) throw new UnacceptedValueException(4);
        this.switchDate = time;
    }
    /**
     Sets the switch date of the device to null.
     */
    public void switchDateNullifier(){
        switchDate = null;
    }

    /**
     Gets the switch date of the device.
     @return The switch date and time.
     */
    public LocalDateTime getSwitchDate(){
        return switchDate;
    }

    /**
     Gets the name of the device.
     @return The name of the device.
     */
    public String getName() {
        return name;
    }
    /**
     Sets the name of the device to param name.
     @param name new name of the device
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Toggles the switch state of the device (i.e., switches it from "On" to "Off" or vice versa).
     */
    public void statusSwitcher(){
        if(getSwitchState() == 1) this.switchState = 0;
        else this.switchState = 1;
    }

    /**
     * Returns the date and time at which the device was last switched on.
     * @return a LocalDateTime object representing the last switch on date and time, or null if the device has never been switched on
     */
    public LocalDateTime getLastSwitchedOn(){
        return this.lastSwitchedOn;
    }

    /**
     * Sets the date and time at which the device was last switched on.
     * @param time a LocalDateTime object representing the last switch on date and time
     */
    public void setLastSwitchedOn(LocalDateTime time){
        this.lastSwitchedOn = time;
    }

    /**
     * Sets the switch state of the device to "Off".
     */
    public void switchOffer(){
        this.switchState = 0;
    }
}
