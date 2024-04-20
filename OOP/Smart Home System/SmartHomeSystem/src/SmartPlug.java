import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * This is a class derived from the superclass SmartDevice that represent smart plugs in the system.
 */
public class SmartPlug extends SmartDevice{
    private final int voltageRate = 220;
    private double ampere;
    private int plugState = 0;
    private double powerUsage = 0.0;

    /**
     Constructs a new SmartPlug object with the given name, switch state, ampere, and time, calling the constructor
     of its superclass and other methods.
     @param name the name of the smart plug
     @param switchState the initial state of the smart plug, either "On" or "Off"
     @param ampere the ampere rating of the smart plug
     @param time the time when the smart plug is created
     @throws Exception if the switch state or the ampere rating is invalid
     */
    public SmartPlug(String name, String switchState, double ampere, LocalDateTime time) throws Exception{
        super(name, switchState, time);
        setAmpere(ampere);
    }

    /**
     Updates the power usage of the smart plug based on the duration it has been switched on.
     @param time the current time
     */
    public void updatePowerUsage(LocalDateTime time){
        LocalDateTime lastOn = getLastSwitchedOn();
        Duration duration = Duration.between(lastOn, time);
        if(plugState == 1 && getSwitchState() == 0) this.powerUsage += duration.getSeconds()/3600.0 * voltageRate * ampere;
    }

    /**
     Sets the ampere rating of the smart plug and determines if the plug is plugged in or not
     @param ampere the ampere rating of the smart plug
     @throws UnacceptedValueException if the ampere rating is not positive
     @throws DeviceStatusUnchangedException if the smart plug is already plugged in
     */
    public void setAmpere(double ampere) throws UnacceptedValueException, DeviceStatusUnchangedException{
        if(this.plugState == 1) throw new DeviceStatusUnchangedException(2);
        if(Double.isNaN(ampere)) {
            this.ampere = 0;
            return;
        }
        if(ampere > 0) {
            this.ampere = ampere;
            plugState = 1;
        }
        else throw new UnacceptedValueException(2);
    }

    /**
     Plugs out the smart plug from the socket and updates the power usage based on the duration it has been switched on.
     @param plugState the state of the plug, either 1 if it's plugged in or 0 if it's unplugged
     @param time the current time
     @throws Exception if the smart plug is already unplugged
     */

    public void plugOut(int plugState, LocalDateTime time) throws Exception{
        if(this.plugState == 0) throw new DeviceStatusUnchangedException(4);
        this.plugState = 0;
        if(getSwitchState() == 1) {
            Duration duration = Duration.between(getLastSwitchedOn(), time);
            this.powerUsage += duration.getSeconds() / 3600.0 * voltageRate * ampere;
        }
        setLastSwitchedOn(time);
    }

    /**
     Switches the switchState of the smart plug.
     @param time the current time
     @throws Exception if the smart plug is already unplugged or if the switch state is invalid
     */
    public void statusSwitcher(LocalDateTime time) throws Exception{
        if(getSwitchState() == 1) {
            setSwitchState("Off", time);
            updatePowerUsage(time);
        }
        else setSwitchState("On", time);

    }

    /**
     * Gets the current powerusage value of the SmartPlug.
     * @return the current powerusage value of the SmartPlug
     */
    public double getPowerUsage(){
        return powerUsage;
    }


    /**
     * An override of the abstract method in the superclass that writes information about the SmartPlug to the output file.
     * @param w the FileWriter to write the information to the file
     * @throws IOException if there is an error in writing the information
     */
    @Override
    void infoTyper(FileWriter w) throws IOException {
        LocalDateTime sTime = getSwitchDate();
        String stringSTime;
        if(sTime == null) stringSTime = "null";
        else stringSTime = sTime.format(formatter);
        String sState = (getSwitchState() == 1) ? "on" : "off";
        w.write(String.format("Smart Plug %s is %s and consumed %.2fW so far (excluding current device), and its time to switch its status is %s.%n", getName(),sState,getPowerUsage(),stringSTime));
    }
}
