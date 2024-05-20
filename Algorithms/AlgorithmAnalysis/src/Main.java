import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Scanner;
import java.util.Random;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

public class Main {


    public static void main(String[] args) throws FileNotFoundException {
        File file = new File(args[0]); //Path to file here
        Scanner scanner = new Scanner(file);
        List<Integer> list = new ArrayList<>();
        scanner.nextLine();
        while(scanner.hasNext()){
            String line = scanner.nextLine();
            String[] subLine = line.split(",");
            list.add(Integer.parseInt(subLine[6]));
        }

        int[] sizes = {500, 1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000, 250000};

        System.out.println("RANDOM SORTED:");
        int[][] runTimesRandom = testFunc(list, 0);

        System.out.println("NATURALLY SORTED:");
        int[][] runTimesNatural = testFunc(list, 1);

        System.out.println("REVERSE SORTED:");
        int[][] runTimesReverse = testFunc(list, 2);

        System.out.println("RANDOM SORTED:");
        int[][] searchTimesRandom = searchTestFunc(list, 0);

        System.out.println("NATURALLY SORTED:");
        int[][] searchTimesNatural = searchTestFunc(list, 1);

        createAndDisplayChart("Sorting With Random Input", sizes, runTimesRandom, 0, null);

        createAndDisplayChart("Sorting With Sorted Input", sizes, runTimesNatural, 0, null);

        createAndDisplayChart("Sorting With Reversely Sorted Input", sizes, runTimesReverse, 0, null);

        createAndDisplayChart("Searching With Random and Sorted Input", sizes, searchTimesRandom, 1, searchTimesNatural);

    }

    private static void createAndDisplayChart(String title, int[] inputSizes, int[][] runningTimes, int mode, int[][] searchTimes) {
        String unit = (mode == 0) ? "ms" : "ns";
        XYChart chart = new XYChartBuilder().width(800).height(600).title(title).xAxisTitle("Input Size").yAxisTitle("Average Running Time (" + unit + ")").build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

        if(mode == 0){
            chart.addSeries("Insertion Sort",inputSizes, runningTimes[0]);
            chart.addSeries("Merge Sort", inputSizes, runningTimes[1]);
            chart.addSeries("Counting Sort", inputSizes, runningTimes[2]);
        }

        else if(mode == 1){
            chart.addSeries("Linear Search (Random)",inputSizes, runningTimes[0]);
            chart.addSeries("Linear Search (Sorted)", inputSizes, searchTimes[0]);
            chart.addSeries("Binary Search", inputSizes, searchTimes[1]);
        }
        new SwingWrapper<>(chart).displayChart();

        try {
            BitmapEncoder.saveBitmap(chart, "./" + title + ".png", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[][] testFunc(List<Integer> mainList, int mode){
        int[] sizes = {500, 1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000, 250000};
        List<Integer>[] list = new List[10];

        for(int i = 0; i < 10; i++){
            List<Integer> testList = new ArrayList<>(mainList.subList(0, sizes[i]));
            list[i] = testList;
        }
        int[][] runTimes = new int[3][10];

        if(mode == 1){
            for(int i = 0; i < 10; i++){
                list[i] = countingSort(list[i]);
            }
        } else if (mode == 2) {
            for(int i = 0; i < 10; i++){
                list[i] = countingSort(list[i]);
                Collections.reverse(list[i]);
            }

        }

        System.out.println("Insertion Sort:");

        for (int i = 0; i < sizes.length; i++) {
            int size = sizes[i];
            int averageTime = 0;
            for (int j = 0; j < 10; j++) {
                List<Integer> sublistCopy = new ArrayList<>(list[i]);
                long start = System.currentTimeMillis();
                insertionSort(sublistCopy);
                long end = System.currentTimeMillis();
                averageTime += end - start;
            }
            averageTime /= 10;
            System.out.println("Average time with size " + size + ": " + averageTime);
            runTimes[0][i] = averageTime;
        }

        System.out.println("Merge Sort:");

        for (int i = 0; i < sizes.length; i++) {

            int size = sizes[i];
            int averageTime = 0;
            for (int j = 0; j < 10; j++) {
                List<Integer> sublistCopy = new ArrayList<>(list[i]);
                long start = System.currentTimeMillis();
                mergeSort(sublistCopy);
                long end = System.currentTimeMillis();
                averageTime += end - start;
            }
            averageTime /= 10;
            System.out.println("Average time with size " + size + ": " + averageTime);
            runTimes[1][i] = averageTime;

        }

        System.out.println("Counting Sort:");

        for(int i = 0; i < sizes.length; i++){
            int averageTime = 0;
            for(int j = 0; j < 10; j++){
                List sublistCopy = new ArrayList<>(list[i]);
                long start = System.currentTimeMillis();
                sublistCopy = countingSort(sublistCopy);
                long end = System.currentTimeMillis();
                averageTime += (end - start);
            }
            averageTime /= 10;
            System.out.println("Average time with size " + sizes[i] + ": " + averageTime);
            runTimes[2][i] = averageTime;
        }

        return runTimes;

    }

    public static int[][] searchTestFunc(List<Integer> mainList, int mode){
        int[] sizes = {500, 1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000, 250000};
        List<Integer>[] list = new List[10];

        for(int i = 0; i < 10; i++){
            List<Integer> testList = new ArrayList<>(mainList.subList(0, sizes[i]));
            list[i] = testList;
        }

        int[][] runTimes = new int[mode + 1][10];

        if(mode == 1) {
            for (int i = 0; i < 10; i++) {
                list[i] = countingSort(list[i]);
            }
        }

        System.out.println("Linear Search:");

        Random random = new Random();

        for (int i = 0; i < sizes.length; i++) {
            int size = sizes[i];
            int averageTime = 0;
            for (int j = 0; j < 1000; j++) {
                int index = random.nextInt(size);
                int key = list[i].get(index);
                long start = System.nanoTime();
                linearSearch(list[i], key);
                long end = System.nanoTime();
                averageTime += end - start;
            }
            averageTime /= 1000;
            System.out.println("Average time with size " + size + ": " + averageTime);
            runTimes[0][i] = averageTime;
        }

        if(mode == 0) return runTimes;

        System.out.println("Binary Search:");

        for (int i = 0; i < sizes.length; i++) {
            int size = sizes[i];
            int averageTime = 0;
            for (int j = 0; j < 1000; j++) {
                int index = random.nextInt(size);
                int key = list[i].get(index);
                long start = System.nanoTime();
                binarySearch(list[i], key);
                long end = System.nanoTime();
                averageTime += end - start;
            }
            averageTime /= 1000;
            System.out.println("Average time with size " + size + ": " + averageTime);
            runTimes[1][i] = averageTime;
        }

        return runTimes;
    }

    public static void insertionSort(List<Integer> list)
    {
        for(int i = 1; i < list.size(); i++){
            int key = list.get(i);
            for(int j = i - 1; j >= 0; j--){
                if(key < list.get(j)){
                    list.set(j + 1, list.get(j));
                    list.set(j, key);
                }
                else break;
            }
        }
    }
    public static List<Integer> mergeSort(List<Integer> list){
        int n = list.size();
        if(n <= 1){
            return list;
        }
        List<Integer> left = new ArrayList<>(list.subList(0, n/2));
        List<Integer> right = new ArrayList<>(list.subList(n/2, n));
        left = mergeSort(left);
        right = mergeSort(right);
        return merge(left,right);
    }

    public static List<Integer> merge(List<Integer> left, List<Integer> right){
        List<Integer> merged = new ArrayList<>();
        while(left.size() != 0 && right.size() != 0){
            if(left.get(0) <= right.get(0)){
                merged.add(left.get(0));
                left.remove(0);
            }
            else{
                merged.add(right.get(0));
                right.remove(0);
            }
        }

        while(left.size() > 0){
            merged.add(left.get(0));
            left.remove(0);
        }
        while(right.size() > 0){
            merged.add(right.get(0));
            right.remove(0);
        }
        return merged;
    }

   public static List countingSort(List list){
        int k = getMax(list);
        int[] count = new int[k + 1];
        Integer[] output = new Integer[list.size()];
        int size = list.size();
        for(int i = 0; i < size; i++){
            int j = (int) list.get(i);
            count[j] =  count[j] + 1;
        }
        for(int i = 2; i < k+1; i++){
            count[i] = count[i] + count[i -1];
       }
        for(int i = size - 1; i >= 0; i--){
            int j = (int) list.get(i);
            count[j] = count[j] - 1;
            output[count[j]] = (int) list.get(i);
        }

       List<Integer> returnable = new ArrayList<>(Arrays.asList(output));
       return returnable;
   }

   public static int getMax(List<Integer> list){
        int size = list.size();
        int max = 0;
        for(int i = 0; i < size; i++){
            if(list.get(i) > max) max = list.get(i);
        }
        return max;
   }

   public static int linearSearch(List<Integer> A, int x){
        for(int i = 0; i < A.size(); i++){
            if(A.get(i) == x) return i;
        }
        return -1;
   }

   public static int binarySearch(List<Integer> A, int x){
        int high = A.size() - 1;
        int low = 0;
        while(high - low > 1){
            int mid = (high + low)/2;
            if(A.get(mid) < x){
                low = mid;
            }
            else{
                high = mid;
            }
        }
        if(A.get(low) == x) return low;
        else if(A.get(high) == x) return high;
        else return -1;
   }
}