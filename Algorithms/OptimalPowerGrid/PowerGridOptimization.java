import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class accomplishes Mission POWER GRID OPTIMIZATION
 */
public class PowerGridOptimization {
    private ArrayList<Integer> amountOfEnergyDemandsArrivingPerHour;

    public PowerGridOptimization(ArrayList<Integer> amountOfEnergyDemandsArrivingPerHour){
        this.amountOfEnergyDemandsArrivingPerHour = amountOfEnergyDemandsArrivingPerHour;
    }

    public ArrayList<Integer> getAmountOfEnergyDemandsArrivingPerHour() {
        return amountOfEnergyDemandsArrivingPerHour;
    }
    /**
     *     Function to implement the given dynamic programming algorithm
     *     SOL(0) <- 0
     *     HOURS(0) <- [ ]
     *     For{j <- 1...N}
     *         SOL(j) <- max_{0<=i<j} [ (SOL(i) + min[ E(j), P(j − i) ] ]
     *         HOURS(j) <- [HOURS(i), j]
     *     EndFor
     *
     * @return OptimalPowerGridSolution
     */
    public OptimalPowerGridSolution getOptimalPowerGridSolutionDP(){
        // TODO: YOUR CODE HERE
        ArrayList<Integer> sol = new ArrayList<>();
        ArrayList<ArrayList<Integer>> hours = new ArrayList<>();
        int n  = amountOfEnergyDemandsArrivingPerHour.size();

        sol.add(0);
        hours.add(new ArrayList<>(1));

        for(int j = 1; j <= n; j++){
            int max_i = 0;
            int maxHours = 0;
            for(int i = 0; i < j; i++){
                double f = Math.pow(j - i, 2);
                double s = amountOfEnergyDemandsArrivingPerHour.get(j-1);
                double value = sol.get(i) + Math.min(s,f);
                if(value > maxHours){
                    maxHours = (int) value;
                    max_i = i;
                }
            }
            sol.add(maxHours);
            ArrayList<Integer> list = new ArrayList<>(hours.get(max_i));
            list.add(j);
            hours.add(list);
        }

        int max = Collections.max(sol);
        int index = sol.indexOf(max);

        OptimalPowerGridSolution opt = new OptimalPowerGridSolution(max, hours.get(index));

        return opt;
    }
}
