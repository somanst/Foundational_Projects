import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 A subclass of SmartDevice representing Smart Lamps in the system.
 */
public class SmartLamp extends SmartDevice{
    private int kelvin;
    private int brightness;
    public SmartLamp(){

    }

    /**
     Constructs a SmartLamp object with the given parameters.
     @param name the name of the Smart Lamp
     @param switchState the switch state of the Smart Lamp
     @param kelvin the kelvin value of the Smart Lamp
     @param brightness the brightness value of the Smart Lamp
     @param time the time of the Smart Lamp's lastSwitchedOn value if it is on
     @throws Exception if an invalid kelvin or brightness value is given
     */
    public SmartLamp(String name, String switchState, int kelvin, int brightness, LocalDateTime time) throws Exception{
        super(name, switchState, time);
        setkelvin(kelvin);
        setBrightness(brightness);
    }

    /**
     Returns the kelvin value of the Smart Lamp.
     @return the kelvin value of the Smart Lamp
     */

    public int getKelvin() {
        return kelvin;
    }

    /**
     Sets the kelvin value of the Smart Lamp and checks if its valid.
     @param kelvin the kelvin value to set
     @throws UnacceptedValueException if the given kelvin value is less than 2000 or greater than 6500
     */
    public void setkelvin(int kelvin) throws Exception{
        if(kelvin >= 2000 && kelvin <= 6500) this.kelvin = kelvin;
        else throw new UnacceptedValueException(8);
    }

    /**
     Sets the brightness value of the Smart Lamp and checks if its valid
     @param brightness the brightness value to set
     @throws UnacceptedValueException if the given brightness value is less than 0 or greater than 100
     */
    public void setBrightness(int brightness) throws Exception{
        if(brightness >= 0 && brightness <= 100) this.brightness = brightness;
        else throw new UnacceptedValueException(9);
    }

    /**
     Returns the brightness value of the Smart Lamp.
     @return the brightness value of the Smart Lamp
     */
    public int getBrightness() {
        return brightness;
    }

    /**
     * An override of the abstract method in the superclass that writes information about the SmartLamp to the output file.
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
        w.write(String.format("Smart Lamp %s is %s and its kelvin value is %dK with %d%s brightness, and its time to switch its status is %s.\n", getName(),sState,getKelvin(),getBrightness(),"%",stringSTime));
    }
}
