import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * This is a class derived from the superclass SmartDevice that represent smart cameras in the system.
 */
public class SmartCamera extends SmartDevice{
    /**
     * The rate of storage usage in megabytes per minute.
     */
    private double mbRate;
    /**
     * The current storage usage in megabytes.
     */
    private double mbUsage;
    /**
     * Constructs a new SmartCamera object with the specified name, switch state, megabyte rate and time of being on..
     * @param name the name of the camera
     * @param switchState the switch state of the camera
     * @param mbRate the rate of storage usage in megabytes per minute
     * @param time the time when the camera is switched on (if it was created on)
     * @throws Exception if the megabyte rate is not positive
     */
    public SmartCamera(String name, String switchState, double mbRate, LocalDateTime time) throws Exception{
        super(name, switchState, time);
        setMbRate(mbRate);
    }

    /**
     * Sets the megabyte usage rate of the camera after checking if it is given as positive.
     * @param mbRate the megabyte rate in megabytes per minute
     * @throws UnacceptedValueException if the megabyte rate is negative
     */
    public void setMbRate(double mbRate) throws UnacceptedValueException {
        if(mbRate > 0) this.mbRate = mbRate;
        else throw new UnacceptedValueException(6);
    }

    /**
     * Updates the storage usage of the camera based on the duration it has been switched on.
     * @param time the current time
     */
    public void updateMbUsage(LocalDateTime time){
        LocalDateTime lastOn = getLastSwitchedOn();
        Duration duration = Duration.between(lastOn, time);
        if(getSwitchState() == 0) mbUsage += duration.getSeconds()/60.0 * mbRate;
    }

    /**
     * Returns the current storage usage of the camera.
     * @return the storage usage in megabytes
     */
    public double getMbUsage(){
        return mbUsage;
    }

    /**
     * Switches the status of the camera and updates its storage usage accordingly.
     * @param time the current time
     * @throws Exception if there is an error switching the camera's status
     */
    public void statusSwitcher(LocalDateTime time) throws Exception{
        if(getSwitchState() == 1) {
            setSwitchState("Off", time);
            updateMbUsage(time);
        }
        else setSwitchState("On", time);
    }
    /**
     * An override of the abstract method in the superclass that writes information about the SmartCamera to the output file.
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
        w.write(String.format("Smart Camera %s is %s and used %.2f MB of storage so far (excluding current status), and its time to switch its status is %s.\n", getName(),sState,getMbUsage(),stringSTime));
    }
}
