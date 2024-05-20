import java.io.Serializable;
import java.util.*;

class UrbanTransportationApp implements Serializable {
    static final long serialVersionUID = 99L;
    
    public HyperloopTrainNetwork readHyperloopTrainNetwork(String filename) {
        HyperloopTrainNetwork hyperloopTrainNetwork = new HyperloopTrainNetwork();
        hyperloopTrainNetwork.readInput(filename);
        return hyperloopTrainNetwork;
    }

    static class pqEntry {
        public pqEntry(int i, double t){
            index = i;
            time = t;
        }
        public int index;
        public double time;

        public double getTime() {
            return time;
        }
    }

    /**
     * Function calculate the fastest route from the user's desired starting point to 
     * the desired destination point, taking into consideration the hyperloop train
     * network. 
     * @return List of RouteDirection instances
     */
    public List<RouteDirection> getFastestRouteDirections(HyperloopTrainNetwork network) {
        List<RouteDirection> routeDirections = new ArrayList<>();
        
        // TODO: Your code goes here
        List<Integer> idList = new ArrayList<>();
        List<Station> initialStations = new ArrayList<>();
        int stationCount = 2;
        int id = 1;

        idList.add(0);
        initialStations.add(network.startPoint);
        for(TrainLine line : network.lines){
            stationCount += line.trainLineStations.size();
            for(Station station : line.trainLineStations){
                initialStations.add(station);
                idList.add(id);
            }
            id++;
        }
        idList.add(id);
        initialStations.add(network.destinationPoint);

        Comparator<pqEntry> timeComparator = Comparator.comparingDouble(pqEntry::getTime);
        PriorityQueue<pqEntry> vertecisTime = new PriorityQueue<>(timeComparator);
        boolean[] visited = new boolean[stationCount];
        double[] curTimes = new double[stationCount];
        Station[] source = new Station[stationCount];
        for(int i = 1; i < curTimes.length; i++){
            curTimes[i] = Double.MAX_VALUE;
        }


        for(int i = 0; i < stationCount; i++){
            for(int k = 0; k < curTimes.length; k++){
                if(visited[k]){
                    vertecisTime.add(new pqEntry(k, Double.MAX_VALUE));
                    continue;
                }
                vertecisTime.add(new pqEntry(k, curTimes[k]));
            }

            int stationIndex = vertecisTime.poll().index;
            Station startStation = initialStations.get(stationIndex);
            visited[stationIndex] = true;
            for(int j = 0; j < stationCount; j++){
                if(stationIndex == j || visited[j]) continue;
                Station toStation = initialStations.get(j);
                if(Objects.equals(idList.get(stationIndex), idList.get(j))){
                    if(Math.abs(j - stationIndex) > 1) continue;
                    if(calculateDistance(startStation, toStation) / network.averageTrainSpeed + curTimes[stationIndex] < curTimes[j]){
                        curTimes[j] = calculateDistance(startStation, toStation) / network.averageTrainSpeed + curTimes[stationIndex];
                        source[j] = startStation;
                    }
                } else{
                    if(calculateDistance(startStation, toStation) / network.averageWalkingSpeed + curTimes[stationIndex] <= curTimes[j]){
                        curTimes[j] = calculateDistance(startStation, toStation) / network.averageWalkingSpeed + curTimes[stationIndex];
                        source[j] = startStation;
                    }
                }
            }
            vertecisTime.clear();
        }

        Station curStation = initialStations.get(stationCount - 1);
        Station sourceStation = source[stationCount - 1];
        Stack<RouteDirection> stack = new Stack<>();
        while(sourceStation != null){
            int index = 0;
            int curIndex = 0;
            for(int i = 0; i < initialStations.size(); i++){
                if(initialStations.get(i).coordinates.equals(sourceStation.coordinates)){
                    sourceStation = initialStations.get(i);
                    index = i;
                } else if (initialStations.get(i).coordinates.equals(curStation.coordinates)) {
                    curStation = initialStations.get(i);
                    curIndex = i;
                }
            }

            boolean trainRide = (Objects.equals(idList.get(index), idList.get(curIndex)));
            RouteDirection routeDirection = new RouteDirection(sourceStation.description, curStation.description, curTimes[curIndex] - curTimes[index], trainRide);
            stack.add(routeDirection);

            curStation = sourceStation;
            sourceStation = source[index];
        }


        while(!stack.isEmpty()){
            routeDirections.add(stack.pop());
        }
        return routeDirections;
    }

    private double calculateDistance(Station from, Station to){
        return Math.sqrt(Math.pow(to.coordinates.x - from.coordinates.x, 2) + Math.pow(to.coordinates.y - from.coordinates.y, 2));
    }

    /**
     * Function to print the route directions to STDOUT
     */
    public void printRouteDirections(List<RouteDirection> directions) {
        
        // TODO: Your code goes here
        double sum = 0;
        for(RouteDirection dir : directions){
            sum += dir.duration;
        }

        int roundedSum = (int) Math.round(sum);
        System.out.printf("The fastest route takes %d minute(s).\nDirections\n----------%n", roundedSum);

        int step = 0;
        for(RouteDirection dir : directions){
            step++;
            if(dir.trainRide){
                System.out.printf("%d. Get on the train from \"%s\" to \"%s\" for %.2f minutes.%n", step, dir.startStationName, dir.endStationName, dir.duration);
            } else{
                System.out.printf("%d. Walk from \"%s\" to \"%s\" for %.2f minutes.%n", step, dir.startStationName, dir.endStationName, dir.duration);
            }
        }


    }
}