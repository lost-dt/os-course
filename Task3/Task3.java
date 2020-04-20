import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.*;
import java.util.Arrays;

public class Task3 {
    public static void main(String[] args) {
        int N = 32;
        int length = 5000;
        double intensity = 0.01;
        double step = 0.01;
        int mint = 9;
        int maxt = 10;
        int maxp = 10;
        int cntr = 0;
        double[] intensities = new double[100];
        double[] koefs = new double[100];
        double[] averagesWait = new double[100];
        while(intensity <= 1){
            cntr++;
            Processor processor = new Processor(N, length, intensity, mint, maxt, maxp);
            processor.modelStepsUntilQueueNotEmpty(1000);
            int countBusy = 0;
            int coutnFree = 0;
            int all = processor.procStatistics.size();
            for(boolean b : processor.procStatistics){
                if(b){
                    countBusy++;
                }else{
                    coutnFree++;
                }
            }

            double koef = (double)(coutnFree)/all;
            koef = Math.round(koef*1000);
            koef = koef/1000;
            koefs[cntr-1] = koef;
            int allWait = 0;
            for(Task t : processor.taskStatistics){
                allWait += t.overallWaitTime;
            }
            double averageWait = (double)(allWait)/all;
            averageWait = Math.round(averageWait*1000);
            averageWait = averageWait/1000;
            averagesWait[cntr-1] = averageWait;
            intensity = Math.round(intensity*1000);
            intensity = intensity/1000;
            intensities[cntr-1] = intensity;

            intensity += step;
        }

        try {
            PrintWriter writer = new PrintWriter("Graph1.txt");
            writer.print("");
            writer.close();
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("Graph1.txt", true)));
            for(int i =0; i < koefs.length; i++){
                String s = Double.toString(koefs[i]);
                s = s.replace('.', ',');
                pw.println(s);
            }
            pw.println();
            for(int i =0; i < averagesWait.length; i++){
                String s = Double.toString(averagesWait[i]);
                s = s.replace('.', ',');
                pw.println(s);
            }
            pw.println();
            for(int i =0; i < intensities.length; i++){
                String s = Double.toString(intensities[i]);
                s = s.replace('.', ',');
                pw.println(s);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        intensity = 0.1;

        Processor processor = new Processor(N, length, intensity, mint, maxt, maxp);
        int minTakts = 10000;
        processor.modelStepsUntilQueueNotEmpty(minTakts);
        double[] averagesWaitByPriority = new double[maxp];
        int[] countersOfTaskTheSamePriority = new int[maxp];
        Arrays.fill(averagesWaitByPriority, 0);
        Arrays.fill(countersOfTaskTheSamePriority, 0);
        for(Task t : processor.taskStatistics){
            averagesWaitByPriority[t.priority] += t.overallWaitTime;
            countersOfTaskTheSamePriority[t.priority]++;
        }
        for(int i = 0; i < maxp; i++){
            if(countersOfTaskTheSamePriority[i] != 0)
                averagesWaitByPriority[i] /= countersOfTaskTheSamePriority[i];
        }

        try {
            PrintWriter writer = new PrintWriter("Graph2.txt");
            writer.print("");
            writer.close();
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("Graph2.txt", true)));
            for(int i =0; i < maxp; i++){
                double d = averagesWaitByPriority[i];
                d = Math.round(d*1000)/(double)1000;
                String s = Double.toString(d);
                s = s.replace('.', ',');
                pw.println(s);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        int countBusy = 0;
        int coutnFree = 0;
        int all = processor.procStatistics.size();
        for(boolean b : processor.procStatistics){
            if(b){
                countBusy++;
            }else{
                coutnFree++;
            }
        }
        System.out.println("count free: "+coutnFree);
        System.out.println("count busy: "+countBusy);
        System.out.println("all: " + all);
        System.out.println("count of tasks: " + processor.taskStatistics.size());
        System.out.println("tasks:");
        System.out.println(processor.taskStatistics.toString());
        int allWait = 0;
        for(Task t : processor.taskStatistics){
            allWait += t.overallWaitTime;
        }
        double averageWait = (double)(allWait)/processor.taskStatistics.size();
        System.out.println("Average wait: " + averageWait);

        try {
            PrintWriter pw = new PrintWriter("RESULTS.txt");
            pw.println(processor.taskStatistics.size());
            for(Task t : processor.taskStatistics){
                pw.println(t.priority);
            }
            for(Task t : processor.taskStatistics){
                pw.println(t.startTime);
            }
            for(Task t : processor.taskStatistics){
                pw.println(t.execTime);
            }
            for(Task t : processor.taskStatistics){
                pw.println(t.overallWaitTime);
            }
            pw.close();
            System.out.println("Writing finished");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}