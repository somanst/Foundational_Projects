import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
/**
 * This is a subclass that Inherits from the SmartLamp class and adds the ability to set the lamp's color using a hex
 * code or a kelvin value depending on the mode occurring.
 */
public class SmartColorLamp extends SmartLamp{

    /**
     * The hex code for the lamp's color.
     */
    private String hexColor;

    /**
     * Whether the lamp is in color mode or white mode.
     */
    private Boolean colorMode;
    /**
     * Creates a new SmartColorLamp object with the specified name, switch state, color or
     * temperature value, brightness, and switch time.
     * @param name          The name of the lamp.
     * @param switchState   The initial switch state of the lamp, either "On" or "Off".
     * @param hexOrKelvin   The initial color or kelvin value of the lamp, as a hex code or an integer respectively.
     * @param brightness    The initial brightness level of the lamp, as an integer between 0 and 100.
     * @param time          The time when the lamp was switched on or off for the first time, as a LocalDateTime object.
     * @throws Exception    If the hex code, kelvin value, or brightness value are invalid.
     */
    public SmartColorLamp(String name, String switchState, String hexOrKelvin, int brightness, LocalDateTime time) throws Exception{
        setName(name);
        setSwitchState(switchState, time);
        setColorMode(hexOrKelvin);
        setBrightness(brightness);
    }
    /**
     * Sets the lamp's color mode based on the specified hex code or kelvin value.
     * @param hexOrKelvin   The hex code or kelvin value to set the color mode to.
     * @throws Exception    If the hex code or kelvin    value is invalid.
     */

    public void setColorMode(String hexOrKelvin) throws Exception{
        String hexPattern = "^0x[0-9A-Fa-f]+$|^[0-9A-Fa-f]+$";
        String intPattern = "^[0-9]+$";
        int hexValue = Integer.parseInt(hexOrKelvin.substring(2) , 16);
        if(hexOrKelvin.matches(hexPattern) && !(hexOrKelvin.matches(intPattern)) && hexValue >= 0x0 && hexValue <= 0xFFFFFF){
            colorMode = true;
            this.setHexColor(hexOrKelvin);
        } else if (hexOrKelvin.matches(hexPattern) && (hexValue < 0x0 || hexValue > 0xFFFFFF)) {
            throw new UnacceptedValueException(3);
        } else {
            colorMode = false;
            this.setkelvin(Integer.parseInt(hexOrKelvin));
        }
    }

    /**
     * Sets the hex code for the lamp's color.
     * @param hexColor      The hex code for the lamp's color.
     * @throws Exception    If the hex code is invalid.
     */
    public void setHexColor(String hexColor) throws Exception{
        String hexPattern = "^0x[0-9A-Fa-f]+$|^[0-9A-Fa-f]+$";
        String intPattern = "^[0-9]+$";
        int hexValue = Integer.parseInt(hexColor.substring(2) , 16);
        if(hexColor.matches(hexPattern) && !(hexColor.matches(intPattern)) && hexValue >= 0x0 && hexValue <= 0xFFFFFF){
            this.hexColor = hexColor;
            colorMode = true;
        } else if (hexColor.matches(hexPattern) && (hexValue < 0x0 || hexValue > 0xFFFFFF)) {
            throw new UnacceptedValueException(3);
        } else throw new Exception();
    }

    /**
     * Gets whether the lamp is currently in color mode or white mode.
     * @return  True if the lamp is in color mode, false if it is in white mode.
     */
    public Boolean getColorMode() {
        return colorMode;
    }

    /**
     * An override of the abstract method in the superclass that writes information about the SmartColorLamp to the
     * output file.
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
        String kelvin = (getColorMode()) ? hexColor : Integer.toString(getKelvin()).concat("K");
        w.write(String.format("Smart Color Lamp %s is %s and its color value is %s with %d%s brightness, and its time to switch its status is %s.\n", getName(),sState,kelvin,getBrightness(),"%",stringSTime));
    }
}
