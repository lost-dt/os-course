import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class ProcessorQueue {
    public final int N;
    private final int length;
    private Queue<Task>[] queues;
    private int[] times;


    public ProcessorQueue(int n, int length){
        N = n;
        this.length = length;
        init();
    }

    private void init(){
        queues = new Queue[N];
        queues[0] = new LinkedList<Task>();
        for(int i = 1; i < queues.length; i++){
            queues[i] = new PriorityQueue<Task>(length, new Task());
        }

        times = new int[N];
        for(int i = 0; i < times.length; i++){
            times[i] = 2*(i+1) - 1;
        }
        times[times.length-1] = -1;
    }

    public void addTask(Task t){
        if(t != null){
            queues[0].add(t);
        }
    }

    public void addTaskInNextQueue(Task t, int n){
        queues[n+1].add(t);
    }

    public ProcessorTask getTask(){
        ProcessorTask pt = null;
        for(int i = 0; i < queues.length; i++){
            if(queues[i].size() == 0){
                continue;
            } else {
                Task res = queues[i].remove();
                pt = new ProcessorTask();
                pt.task = res;
                pt.time = times[i];
                pt.queueNumber = i;
                return pt;
            }
        }
        return pt;
    }

    public boolean notEmpty(){
        boolean res = false;
        for(int i = 0; i < queues.length; i++){
            if(queues[i].size() == 0){
                continue;
            } else {
                res = true;
                return res;
            }
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("---- Processor Queue ----- \n"+"number of queues: "+N+"\n");
        for(int i = 0; i < N; i++){
            res.append((i+1));
            res.append(":   " + queues[i].toString() + "\n");
        }
        res.append("---- /Processor Queue ----");

        return res.toString();
    }
}