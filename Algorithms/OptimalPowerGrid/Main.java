import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

/**
 * Main class
 */
// FREE CODE HERE
public class Main {
    public static void main(String[] args) throws IOException {

        /** MISSION POWER GRID OPTIMIZATION BELOW **/

        System.out.println("##MISSION POWER GRID OPTIMIZATION##");
        // TODO: Your code goes here
        // You are expected to read the file given as the first command-line argument to read 
        // the energy demands arriving per hour. Then, use this data to instantiate a 
        // PowerGridOptimization object. You need to call getOptimalPowerGridSolutionDP() method
        // of your PowerGridOptimization object to get the solution, and finally print it to STDOUT.

        File file = new File(args[0]);
        Scanner scanner = new Scanner(file);
        String line = scanner.nextLine();
        String[] split = line.split(" ");
        ArrayList<Integer> demandList = new ArrayList<>();
        int demand = 0;
        for(String x : split){
            int num = Integer.parseInt(x);
            demandList.add(num);
            demand += num;
        }

        PowerGridOptimization dynamic = new PowerGridOptimization(demandList);
        OptimalPowerGridSolution opt = dynamic.getOptimalPowerGridSolutionDP();
        ArrayList<Integer> hoursList = opt.getHoursToDischargeBatteriesForMaxEfficiency();



        System.out.print("The total number of demanded gigawatts: " + demand +
                            "\nMaximum number of satisfied gigawatts: " + opt.getmaxNumberOfSatisfiedDemands() +
                            "\nHours at which the battery bank should be discharged: ");
        System.out.print(hoursList.get(0));
        for (int i = 1; i < hoursList.size(); i++) {
            System.out.print(", " + hoursList.get(i));
        }
        System.out.println("\nThe number of unsatisfied gigawatts: " + (demand - opt.getmaxNumberOfSatisfiedDemands()));


        System.out.println("##MISSION POWER GRID OPTIMIZATION COMPLETED##");

        /** MISSION ECO-MAINTENANCE BELOW **/

        System.out.println("##MISSION ECO-MAINTENANCE##");
        // TODO: Your code goes here
        // You are expected to read the file given as the second command-line argument to read
        // the number of available ESVs, the capacity of each available ESV, and the energy requirements 
        // of the maintenance tasks. Then, use this data to instantiate an OptimalESVDeploymentGP object.
        // You need to call getMinNumESVsToDeploy(int maxNumberOfAvailableESVs, int maxESVCapacity) method
        // of your OptimalESVDeploymentGP object to get the solution, and finally print it to STDOUT.
        File file2 = new File(args[1]);
        Scanner scanner2 = new Scanner(file2);
        String req = scanner2.nextLine();
        String line_ = scanner2.nextLine();
        String[] split_ = line_.split(" ");
        ArrayList<Integer> demandListy = new ArrayList<>();
        for(String x : split_){
            int num = Integer.parseInt(x);
            demandListy.add(num);
        }
        String[] reqList = req.split(" ");
        OptimalESVDeploymentGP esv = new OptimalESVDeploymentGP(demandListy);

        esv.getMinNumESVsToDeploy(Integer.parseInt(reqList[0]), Integer.parseInt(reqList[1]));
        ArrayList<ArrayList<Integer>> esvassign = esv.getMaintenanceTasksAssignedToESVs();

        if(esvassign.isEmpty()) System.out.println("Warning: Mission Eco-Maintenance Failed.");
        else System.out.println("The minimum number of ESVs to deploy: " + esvassign.size());

        int i = 1;
        for(ArrayList<Integer> esvList : esvassign){
            System.out.println("ESV " + i + " tasks: " + esvList);
            i++;
        }

        System.out.println("##MISSION ECO-MAINTENANCE COMPLETED##");
    }
}
