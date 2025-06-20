import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Main {
    final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
    static LocalDateTime time = null;

    /**
     Checks if the input file contains a valid initial time command as the first command, and terminates the program if it doesn't.
     @param lines an array of Strings containing the lines read from the input file
     @param w a FileWriter object used to write feedback to the output file
     @throws IOException if an error occurs while writing to the output file
     */
    public static void setInitTimeChecker(String[] lines, FileWriter w) throws IOException {
        if(lines == null){
            w.write("COMMAND: \n");
            w.write("ERROR: First command must be set initial time! Program is going to terminate!\n");
            System.exit(0);
        }
        for(String line : lines){
            String[] partialCom = line.split("\t");
            if((!partialCom[0].equals("SetInitialTime") && !line.trim().isEmpty()) || partialCom[0].equals("SetInitialTime") && partialCom.length != 2) {
                w.write("COMMAND: " + lines[0]);
                w.write("ERROR: First command must be set initial time! Program is going to terminate!");
                System.exit(0);
            }else if (partialCom[0].equals("SetInitialTime")) {
                return;
            }
        }
        w.write("COMMAND: \n");
        w.write("ERROR: First command must be set initial time! Program is going to terminate!\n");
        System.exit(0);
    }

    /**
     Updates the status of a SmartDevice.
     @param dev the SmartDevice object whose status is to be updated
     @throws Exception if an error occurs while updating the status
     */
    public static void deviceUpdater(SmartDevice dev) throws Exception{
        if(dev instanceof SmartPlug){
            SmartPlug dev_ = (SmartPlug) dev;
            dev_.statusSwitcher(time);
        } else if (dev instanceof SmartCamera) {
            SmartCamera dev_ = (SmartCamera) dev;
            dev_.statusSwitcher(time);
        } else dev.statusSwitcher();
    }

    /**
     Sorts the main list of SmartDevice objects based on their switch dates in ascending order and keeps objects with
     null switchdates at the bottom.
     @param mainList the list of SmartDevice objects to be sorted
     @return the sorted list of SmartDevice objects
     */
    public static List<SmartDevice> listSorter(List<SmartDevice> mainList) {
        boolean swap = true;
        while (swap) {
            swap = false;
            for (int i = 0; i < mainList.size() - 1; i++) {
                if(mainList.get(i).getSwitchDate() == null && mainList.get(i+1).getSwitchDate() == null || mainList.get(i + 1).getSwitchDate() == null && mainList.get(i).getSwitchDate() != null){
                    continue;
                }
                if(mainList.get(i).getSwitchDate() == null && mainList.get(i+1).getSwitchDate() != null){
                    Collections.swap(mainList, i, i + 1);
                    swap = true;
                }
                else if (mainList.get(i).getSwitchDate().isAfter(mainList.get(i + 1).getSwitchDate())) {
                    Collections.swap(mainList, i, i + 1);
                    swap = true;
                }
            }
        }
        return mainList;
    }

    /**
     Has 2 modes, first is to return whether a device is added before or not, and the other is to get the index of the
     needed SmartDevice in the mainList based on its name.
     @param mainList the List of SmartDevices to search through
     @param testedDeviceName the name of the SmartDevice object to find
     @param mode a String indicating the mode of the method. If "add", it returns whether the object was found (0) or
     not (1). If any other String, it returns the index of the object in the List.
     @return if mode is "add", it returns 0 if the object was found or 1 if it was not found. Otherwise, it returns the
     index of the object in the List. If the List is empty, it returns 1.
     */
    public static int deviceFinder(List<SmartDevice> mainList, String testedDeviceName, String mode){
        int objNotFound = 1;
        if(mainList.size() == 0) return 1;
        int deviceIndex = 0;
        for(SmartDevice dev : mainList){
            if (dev.getName().equals(testedDeviceName)) {
                objNotFound = 0;
                break;
            }
            deviceIndex++;
        }
        if(objNotFound == 1) deviceIndex = mainList.size() + 1;
        if(mode.equals("add")) return objNotFound;
        return deviceIndex;
    }

    /**
     Adds a new SmartDevice object to the mainList after checking the type it is going to be based on the given command,
     and checks if the command is valid and the parameters in the command are checked by constructors of devices.
     This part was a bit challenging since there are many valid ways to give a command with various lengths
     but this problem was solved by giving the default value of the command part that is missing if it was valid
     for it to be missing, and then calls constructors of devices with these values.
     @param command an array of Strings representing the command to be executed
     @param mainList the List of SmartDevice objects to add the new device to
     @return the updated mainList with the new device added and sorted based on switch date
     @throws Exception if the command is invalid or a device with the same name already exists
     */
    public static List<SmartDevice> deviceAdder(String[] command, List<SmartDevice> mainList) throws Exception{
        String name = command[2];
        String switchState;
        int kelvin;
        int brightness;
        SmartDevice tempDevice;
        if(command[1].equals("SmartPlug")){
            if(command.length >= 6) throw new Exception();
            if(deviceFinder(mainList, name, "Add") == 0) throw new ObjectAlreadyCreatedException();
            switchState = command.length >= 4 ? command[3] : "Off";
            double ampere = command.length == 5 ? Double.parseDouble(command[4]) : Double.NaN;
            tempDevice = new SmartPlug(name, switchState, ampere, time);
            mainList.add(tempDevice);
        } else if (command[1].equals("SmartCamera")) {
            if(command.length >= 6 || command.length == 3) throw new Exception();
            if(deviceFinder(mainList, name, "Add") == 0) throw new ObjectAlreadyCreatedException();
            double mbRate = Double.parseDouble(command[3]);
            switchState = command.length == 5 ? command[4] : "Off";
            tempDevice = new SmartCamera(name, switchState, mbRate, time);
            mainList.add(tempDevice);
        } else if (command[1].equals("SmartLamp")) {
            if(command.length >= 7 || command.length == 5) throw new Exception();
            if(deviceFinder(mainList, name, "Add") == 0) throw new ObjectAlreadyCreatedException();
            switchState = command.length >= 4 ? command[3] : "Off";
            kelvin = command.length == 6 ? Integer.parseInt(command[4]) : 4000;
            brightness = command.length == 6 ? Integer.parseInt(command[5]) : 100;
            tempDevice = new SmartLamp(name, switchState, kelvin, brightness, time);
            mainList.add(tempDevice);
        } else if (command[1].equals("SmartColorLamp")) {
            if(command.length >= 7 || command.length == 5) throw new Exception();
            if(deviceFinder(mainList, name, "Add") == 0) throw new ObjectAlreadyCreatedException();
            switchState = command.length >= 4 ? command[3] : "Off";
            brightness = command.length == 6 ? Integer.parseInt(command[5]) : 100;
            if(command.length == 6){
                tempDevice = new SmartColorLamp(name, switchState, command[4], brightness, time);
            } else {
                tempDevice = new SmartColorLamp(name, switchState, "4000", 100, time);
            }
            mainList.add(tempDevice);
        } else{
            throw new Exception();
        }
        mainList = listSorter(mainList);
        return mainList;
    }

    /**
     Loops through a copy of the mainList checking whether a switchtime was skipped or is equal to newTime when the time
     is updated, if so calls the deviceUpdater method to update and switch it/them while creating 2 temptimes to update
     the devices with equalswitch time accordingly and not 1 by 1, so when temp2 isn't equal to temp1 list is updated
     since a new switchTime has arrived, then nullifies the switchTime of updated devices and the list is going to
     be sorted later on, this part was also challenging since the list needs to be updated not by device but by time,
     but was solved by the method just mentioned.
     @param newTime the new LocalDateTime to be compared to the switchDate of each SmartDevice in mainList since time
     might change.
     @param mainList the List of SmartDevices to be updated
     @throws Exception if there is an error finding or updating a SmartDevice, or if an ObjectAlreadyCreatedException is thrown
     */
    public static void time_UpdateCoordinator(LocalDateTime newTime, List<SmartDevice> mainList) throws Exception{
        List<SmartDevice> tempList = new ArrayList<SmartDevice>(mainList);
        LocalDateTime tempTime = tempList.get(0).getSwitchDate();
        for(SmartDevice dev : tempList){
            if(dev.getSwitchDate() == null || dev.getSwitchDate().isAfter(newTime)){
            continue;
            }
            int devIndex = deviceFinder(mainList, dev.getName(), "find");
            LocalDateTime tempTime2 = mainList.get(devIndex).getSwitchDate();
            deviceUpdater(mainList.get(devIndex));
            if(!tempTime2.equals(tempTime)){
                listSorter(mainList);
                tempTime = tempTime2;
            }
            dev.switchDateNullifier();
        }
    }

    /**
     A really important method in the program, Identifies the given command, checks whether its faulty and executes/calls
     the needed methods based on that command.
     @param command the command to be executed
     @param mainList the list of smart devices to be updated
     @param w the FileWriter object for writing to the log file
     @return the updated list of smart devices
     @throws Exception if the command or its parameters are invalid or if an error occurs during execution
     */
    public static List<SmartDevice> commandExecuter(String command, List<SmartDevice> mainList, FileWriter w) throws Exception {
        String[] partialCom = command.trim().split("\t");
        Duration duration;
        LocalDateTime newTime;
        w.write("COMMAND: " + command + "\n");
        if(partialCom[0].equals("SetInitialTime")){
            if(time != null) throw new Exception();
            try{
                formatter.parse(partialCom[1]);
            } catch(Exception e){
                w.write("ERROR: Format of the initial date is wrong! Program is going to terminate!\n");
                System.exit(0);
            }
            time = LocalDateTime.parse(partialCom[1],formatter);
            w.write(String.format("SUCCESS: Time has been set to %s!\n", time.format(formatter)));
        } else if (partialCom[0].equals("SetTime")) {
            if(partialCom.length != 2) throw new Exception();
            newTime = LocalDateTime.parse(partialCom[1],formatter);
            if(newTime.isBefore(time)) throw new UnacceptedValueException(0);
            duration = Duration.between(time, newTime);
            if(duration.toMinutes() == 0) throw new UnacceptedValueException(5);
            time = newTime;
            time_UpdateCoordinator(time, mainList);
        } else if (partialCom[0].equals("SkipMinutes")) {
            if(partialCom.length != 2) throw new Exception();
            else if (Integer.parseInt(partialCom[1]) < 0) throw new UnacceptedValueException(0);
            else if (Integer.parseInt(partialCom[1]) == 0) throw new UnacceptedValueException(1);
            time = time.plusMinutes(Integer.parseInt(partialCom[1]));
            time_UpdateCoordinator(time, mainList);
        } else if (partialCom[0].equals("Add")){
            mainList = deviceAdder(partialCom, mainList);
        } else if (partialCom[0].equals("SetSwitchTime")){
            if(partialCom.length != 3) throw new Exception();
            int devIndex = deviceFinder(mainList, partialCom[1], "find");
            if(devIndex == mainList.size() + 1) throw new DeviceNotFoundException(0);
            if(LocalDateTime.parse(partialCom[2], formatter).isBefore(time)) throw new UnacceptedValueException(4);
            SmartDevice dev = mainList.get(devIndex);
            dev.setSwitchDate(partialCom[2], time);
            if(time.equals(dev.getSwitchDate())) {
                dev.statusSwitcher();
                listSorter(mainList);
                dev.switchDateNullifier();
            }
        } else if (partialCom[0].equals("ZReport")) {
            if(partialCom.length != 1) throw new Exception();
            w.write(String.format("Time is:\t%s\n", time.format(formatter)));
            for(SmartDevice dev : mainList){
                dev.infoTyper(w);
            }
        } else if(partialCom[0].equals("Nop")){
            if(partialCom.length != 1) throw new Exception();
            listSorter(mainList);
            if(mainList.size() == 0 || mainList.get(0).getSwitchDate() == null) throw new DeviceNotFoundException(1);
            else{
                time = mainList.get(0).getSwitchDate();
                time_UpdateCoordinator(time, mainList);
            }

        } else if (partialCom[0].equals("Remove")) {
            if(partialCom.length != 2) throw new Exception();
            int devIndex = deviceFinder(mainList, partialCom[1], "find");
            if(devIndex == mainList.size() + 1) throw new DeviceNotFoundException(0);
            else {
                w.write("SUCCESS: Information about removed smart device is as follows:\n");
                deviceUpdater(mainList.get(devIndex));
                mainList.get(devIndex).switchOffer();
                mainList.get(devIndex).infoTyper(w);
                mainList.remove(devIndex);
            }
        } else if (partialCom[0].equals("Switch")) {
            if(partialCom.length != 3) throw new Exception();
            String newState = partialCom[2];
            if (!newState.equals("On") && !newState.equals("Off")) throw new Exception();
            int newStateInt = (newState.equals("On")) ? 1 : 0;
            int devIndex = deviceFinder(mainList, partialCom[1], "find");
            if(devIndex == mainList.size() + 1) throw new DeviceNotFoundException(0);
            SmartDevice dev = mainList.get(devIndex);
            if(newStateInt == dev.getSwitchState()){
                if(newStateInt == 1){
                    throw new DeviceStatusUnchangedException(0);
                } else if (newStateInt == 0) {
                    throw new DeviceStatusUnchangedException(1);
                }
            }
            deviceUpdater(dev);
        } else if (partialCom[0].equals("PlugIn")) {
            if(partialCom.length != 3) throw new Exception();
            int devIndex = deviceFinder(mainList, partialCom[1], "find");
            if(devIndex == mainList.size() + 1) throw new DeviceNotFoundException(0);
            SmartDevice dev = mainList.get(devIndex);
            if(!(dev instanceof SmartPlug)) throw new DeviceTypeException(0);
            ((SmartPlug) dev).setAmpere(Double.parseDouble(partialCom[2]));
        } else if (partialCom[0].equals("PlugOut")) {
            if(partialCom.length != 2) throw new Exception();
            int devIndex = deviceFinder(mainList, partialCom[1], "find");
            if(devIndex == mainList.size() + 1) throw new DeviceNotFoundException(0);
            SmartDevice dev = mainList.get(devIndex);
            if(!(dev instanceof SmartPlug)) throw new DeviceTypeException(0);
            ((SmartPlug)dev).plugOut(0, time);
        } else if (partialCom[0].equals("ChangeName")) {
            if(partialCom.length != 3) throw new Exception();
            if(partialCom[1].equals(partialCom[2])) throw new DeviceStatusUnchangedException(3);
            int devIndex = deviceFinder(mainList, partialCom[1], "find");
            if(devIndex == mainList.size() + 1) throw new DeviceNotFoundException(0);
            int dev2Index = deviceFinder(mainList, partialCom[2], "find");
            if(!(dev2Index == mainList.size() + 1)) throw new ObjectAlreadyCreatedException();
            SmartDevice dev = mainList.get(devIndex);
            dev.setName(partialCom[2]);
        } else if (partialCom[0].equals("SetKelvin")) {
            if(partialCom.length != 3) throw new Exception();
            int devIndex = deviceFinder(mainList, partialCom[1], "find");
            if(devIndex == mainList.size() + 1) throw new DeviceNotFoundException(0);
            SmartDevice dev = mainList.get(devIndex);
            if(!(dev instanceof SmartLamp)) throw new DeviceTypeException(1);
            ((SmartLamp) dev).setkelvin(Integer.parseInt(partialCom[2]));
        } else if (partialCom[0].equals("SetBrightness")) {
            if(partialCom.length != 3) throw new Exception();
            int devIndex = deviceFinder(mainList, partialCom[1], "find");
            if(devIndex == mainList.size() + 1) throw new DeviceNotFoundException(0);
            SmartDevice dev = mainList.get(devIndex);
            if(!(dev instanceof SmartLamp)) throw new DeviceTypeException(1);
            ((SmartLamp) dev).setBrightness(Integer.parseInt(partialCom[2]));
        } else if (partialCom[0].equals("SetColorCode")) {
            if(partialCom.length != 3) throw new Exception();
            int devIndex = deviceFinder(mainList, partialCom[1], "find");
            if(devIndex == mainList.size() + 1) throw new DeviceNotFoundException(0);
            SmartDevice dev = mainList.get(devIndex);
            if(!(dev instanceof SmartColorLamp)) throw new DeviceTypeException(2);
            ((SmartColorLamp) dev).setHexColor(partialCom[2]);
        } else if (partialCom[0].equals("SetColor")) {
            if(partialCom.length != 4) throw new Exception();
            int devIndex = deviceFinder(mainList, partialCom[1], "find");
            if(devIndex == mainList.size() + 1) throw new DeviceNotFoundException(0);
            SmartDevice dev = mainList.get(devIndex);
            if(!(dev instanceof SmartColorLamp)) throw new DeviceTypeException(2);
            if(!(Integer.parseInt(partialCom[3]) >= 0 && Integer.parseInt(partialCom[3]) <= 100)) throw new UnacceptedValueException(9);
            ((SmartColorLamp) dev).setHexColor(partialCom[2]);
            ((SmartColorLamp) dev).setBrightness(Integer.parseInt(partialCom[3]));
        } else if (partialCom[0].equals("SetWhite")) {
            if(partialCom.length != 4) throw new Exception();
            int devIndex = deviceFinder(mainList, partialCom[1], "find");
            if(devIndex == mainList.size() + 1) throw new DeviceNotFoundException(0);
            SmartDevice dev = mainList.get(devIndex);
            if(!(dev instanceof SmartLamp)) throw new DeviceTypeException(1);
            if(!(Integer.parseInt(partialCom[2]) >= 2000 && Integer.parseInt(partialCom[2]) <= 6500)) throw new UnacceptedValueException(8);
            ((SmartLamp) dev).setkelvin(Integer.parseInt(partialCom[2]));
            ((SmartLamp) dev).setBrightness(Integer.parseInt(partialCom[3]));
            if(dev instanceof SmartColorLamp) ((SmartColorLamp) dev).setColorMode(partialCom[2]);
        } else throw new Exception();
        return listSorter(mainList);
    }

    /**
     The main method of the program, which takes in an array of arguments as input.It creates a FileWriter to write
     output to a file, initializes a SmartDevice list, and reads commands from an input file. It iterates through each
     line of the input file, skipping empty lines, and attempts to execute the command on the SmartDevice list using
     the commandExecuter method. If an exception is thrown during command execution, the program catches the exception
     and writes an appropriate error message to the output file. If the last line of the input file is not "ZReport",
     the program writes a final report to the output file, including the current time and status information for all
     SmartDevices in the list.
     @param args the array of arguments passed to the main method for filenames
     @throws Exception if an exception occurs while executing commands or writing to the output file
     */
    public static void main(String[] args) throws Exception {
        File output = new File(args[1]);
        FileWriter w = new FileWriter(output);
        String[] lines = ReadFromFile.readFile(args[0]);
        List<SmartDevice> deviceList = new ArrayList<>();
        setInitTimeChecker(lines, w);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if(line.isEmpty()) continue;
            try {
                deviceList = commandExecuter(line, deviceList,w);
            } catch (UnacceptedValueException e) {
                if (e.getNumber() == 0) w.write("ERROR: Time cannot be reversed!\n");
                else if (e.getNumber() == 1) w.write("ERROR: There is nothing to skip!\n");
                else if (e.getNumber() == 5) w.write("ERROR: There is nothing to change!\n");
                else if (e.getNumber() == 4) w.write("ERROR: Switch time cannot be in the past!\n");
                else if (e.getNumber() == 2) w.write("ERROR: Ampere value must be a positive number!\n");
                else if (e.getNumber() == 3) w.write("ERROR: Color code value must be in range of 0x0-0xFFFFFF!\n");
                else if (e.getNumber() == 6) w.write("ERROR: Megabyte value must be a positive number!\n");
                else if (e.getNumber() == 8) w.write("ERROR: Kelvin value must be in range of 2000K-6500K!\n");
                else if (e.getNumber() == 9) w.write("ERROR: Brightness must be in range of 0%-100%!\n");
            } catch(DateTimeException e){
                w.write("ERROR: Time format is not correct!\n");
            } catch(ObjectAlreadyCreatedException e){
                w.write("ERROR: There is already a smart device with same name!\n");
            } catch(DeviceNotFoundException e){
                if(e.getMode() == 0) w.write("ERROR: There is not such a device!\n");
                else if (e.getMode() == 1) w.write("ERROR: There is nothing to switch!\n");
            } catch(DeviceStatusUnchangedException e){
                if (e.getMode() == 0) w.write("ERROR: This device is already switched on!\n");
                if (e.getMode() == 1) w.write("ERROR: This device is already switched off!\n");
                if (e.getMode() == 2) w.write("ERROR: There is already an item plugged in to that plug!\n");
                if (e.getMode() == 3) w.write("ERROR: Both of the names are the same, nothing changed!\n");
                if (e.getMode() == 4) w.write("ERROR: This plug has no item to plug out from that plug!\n");
            } catch (DeviceTypeException e){
                if (e.getMode() == 0) w.write("ERROR: This device is not a smart plug!\n");
                if (e.getMode() == 1) w.write("ERROR: This device is not a smart lamp!\n");
                if (e.getMode() == 2) w.write("ERROR: This device is not a smart color lamp!\n");
            }catch(Exception e){
                w.write("ERROR: Erroneous command!\n");
            }
        }
        if(!lines[lines.length -1].equals("ZReport")){
            w.write("ZReport:\n");
            w.write("Time is:\t" + time.format(formatter) + "\n");
            for(SmartDevice dev : deviceList){
                dev.infoTyper(w);
            }
        }
        w.close();
    }
}
