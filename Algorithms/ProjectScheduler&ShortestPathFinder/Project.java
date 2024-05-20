import java.io.Serializable;
import java.util.*;

public class Project implements Serializable {
    static final long serialVersionUID = 33L;
    private final String name;
    private final List<Task> tasks;
    private int[] timeList;

    public Project(String name, List<Task> tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    /**
     * @return the total duration of the project in days
     */
    public int getProjectDuration() {
        int projectDuration;

        // TODO: YOUR CODE HERE
        getEarliestSchedule();
        int max = -1;
        for(int i = 0; i < timeList.length; i++){
            if(timeList[i] + tasks.get(i).getDuration() > max) max = timeList[i] + tasks.get(i).getDuration();
        }
        projectDuration = max;

        return projectDuration;
    }

    /**
     * Schedule all tasks within this project such that they will be completed as early as possible.
     *
     * @return An integer array consisting of the earliest start days for each task.
     */
    public int[] getEarliestSchedule() {

        // TODO: YOUR CODE HERE
        int [] timeList = new int[tasks.size()];
        int[] sortedTasks = getTopSort();

        for(int i : sortedTasks){
            Task curTask = tasks.get(i);
            List<Integer> dependancies = curTask.getDependencies();
            int maxTime = 0;
            for(int j : dependancies){
                if(timeList[j] + tasks.get(j).getDuration() > maxTime) maxTime = timeList[j] + tasks.get(j).getDuration();
            }
            timeList[i] = maxTime;
        }
        this.timeList = timeList;
        return timeList;
    }

    private int[] getTopSort(){
        int[] sort = new int[tasks.size()];
        Stack<Integer> stack = new Stack<>();
        boolean[] visited = new boolean[tasks.size()];

        dfs(stack, visited, 0);
        for(int i = 0; i < tasks.size(); i++){
            sort[i] = stack.pop();
        }

        return sort;
    }

    private void dfs(Stack<Integer> stack, boolean[] visited, int curTask){
        visited[curTask] = true;
        for(int i = 0; i < tasks.size(); i++){
            if(visited[i]) continue;
            if(tasks.get(i).getDependencies().contains(tasks.get(curTask).getTaskID())){
                dfs(stack, visited, tasks.get(i).getTaskID());
            }
        }
        stack.add(tasks.get(curTask).getTaskID());
    }

    public static void printlnDash(int limit, char symbol) {
        for (int i = 0; i < limit; i++) System.out.print(symbol);
        System.out.println();
    }

    /**
     * Some free code here. YAAAY! 
     */
    public void printSchedule(int[] schedule) {
        getProjectDuration();
        int limit = 65;
        char symbol = '-';
        printlnDash(limit, symbol);
        System.out.println(String.format("Project name: %s", name));
        printlnDash(limit, symbol);

        // Print header
        System.out.println(String.format("%-10s%-45s%-7s%-5s","Task ID","Description","Start","End"));
        printlnDash(limit, symbol);
        for (int i = 0; i < schedule.length; i++) {
            Task t = tasks.get(i);
            System.out.println(String.format("%-10d%-45s%-7d%-5d", i, t.getDescription(), schedule[i], schedule[i]+t.getDuration()));
        }
        printlnDash(limit, symbol);
        System.out.println(String.format("Project will be completed in %d days.", tasks.get(schedule.length-1).getDuration() + schedule[schedule.length-1]));
        printlnDash(limit, symbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;

        int equal = 0;

        for (Task otherTask : ((Project) o).tasks) {
            if (tasks.stream().anyMatch(t -> t.equals(otherTask))) {
                equal++;
            }
        }

        return name.equals(project.name) && equal == tasks.size();
    }

}
