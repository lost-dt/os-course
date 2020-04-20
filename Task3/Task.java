import java.util.Comparator;

public class Task implements Comparator<Task>{
    public int priority;
    public int timeOfExecute;
    public int overallWaitTime;
    public int execTime;
    public int startTime;

    public Task(){

    }

    public Task(int pr, int te, int time){
        priority = pr;
        timeOfExecute = te;
        execTime = timeOfExecute;
        overallWaitTime = 0;
        startTime = time;
    }

    @Override
    public int compare(Task o1, Task o2) {
        if(o1.priority > o2.priority){
            return -1;
        }
        if(o1.priority < o2.priority){
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return ("(" + priority + ", " + execTime+", "+ timeOfExecute + ")"); //", "+overallWaitTime+", "+startTime+
    }
}