import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * This class accomplishes Mission Eco-Maintenance
 */
public class OptimalESVDeploymentGP
{
    private ArrayList<Integer> maintenanceTaskEnergyDemands;

    /*
     * Should include tasks assigned to ESVs.
     * For the sample input:
     * 8 100
     * 20 50 40 70 10 30 80 100 10
     * 
     * The list should look like this:
     * [[100], [80, 20], [70, 30], [50, 40, 10], [10]]
     * 
     * It is expected to be filled after getMinNumESVsToDeploy() is called.
     */
    private ArrayList<ArrayList<Integer>> maintenanceTasksAssignedToESVs = new ArrayList<>();

    ArrayList<ArrayList<Integer>> getMaintenanceTasksAssignedToESVs() {
        return maintenanceTasksAssignedToESVs;
    }

    public OptimalESVDeploymentGP(ArrayList<Integer> maintenanceTaskEnergyDemands) {
        this.maintenanceTaskEnergyDemands = maintenanceTaskEnergyDemands;
    }

    public ArrayList<Integer> getMaintenanceTaskEnergyDemands() {
        return maintenanceTaskEnergyDemands;
    }

    /**
     *
     * @param maxNumberOfAvailableESVs the maximum number of available ESVs to be deployed
     * @param maxESVCapacity the maximum capacity of ESVs
     * @return the minimum number of ESVs required using first fit approach over reversely sorted items.
     * Must return -1 if all tasks can't be satisfied by the available ESVs
     */
    public int getMinNumESVsToDeploy(int maxNumberOfAvailableESVs, int maxESVCapacity)
    {
        // TODO: Your code goes here
        Collections.sort(maintenanceTaskEnergyDemands, Collections.reverseOrder());
        ArrayList<Integer> esvLefty = new ArrayList<>();
        ArrayList<ArrayList<Integer>> assignments = new ArrayList<>();
        int availEsvs = maxNumberOfAvailableESVs;

        for(int i = 0; i < maintenanceTaskEnergyDemands.size(); i++){
            int curDemand = maintenanceTaskEnergyDemands.get(i);
            boolean found = false;

            for(int j = 0; j < esvLefty.size(); j++){
                int avail = esvLefty.get(j);
                if(avail >= curDemand){
                    esvLefty.set(j, avail - curDemand);
                    assignments.get(j).add(curDemand);
                    found = true;
                    break;
                }
            }
            if(found) continue;

            if(availEsvs == 0 || curDemand > maxESVCapacity) return -1;
            availEsvs -= 1;
            esvLefty.add(maxESVCapacity - curDemand);
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(curDemand);
            assignments.add(temp);
        }
        maintenanceTasksAssignedToESVs = assignments;
        return assignments.size();
    }

}
