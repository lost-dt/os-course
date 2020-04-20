import java.util.LinkedList;

public class Processor {
    private ProcessorQueue queue;
    private Generator generator;
    private int systemTime = 0;
    private int deltaTime = 1;
    int interval = 1000;
    public LinkedList<Task> taskStatistics = new LinkedList<Task>();
    public LinkedList<Boolean> procStatistics = new LinkedList<Boolean>();

    public Processor(int n, int length, double intensity, int mint, int maxt, int maxp){
        queue = new ProcessorQueue(n, length);
        generator = new Generator(intensity, mint, maxt, maxp);
    }

    public void modelStepsUntilQueueNotEmpty(int mt){
        interval = mt;
        while((systemTime < interval) || (queue.notEmpty())){
            nextStep();
        }
    }

    public void modelSteps(int n){
        for(int i = 0; i < n; i++){
            nextStep();
        }
    }

    public void nextStep(){
        if(systemTime < interval) {
            while (deltaTime > 0) {
                deltaTime--;
                queue.addTask(generator.generate(systemTime));
            }
        }
        ProcessorTask currentTask = queue.getTask();
        if(currentTask != null){
            if(currentTask.queueNumber == queue.N-1){
                systemTime += currentTask.task.timeOfExecute;
                deltaTime = currentTask.task.timeOfExecute;
                currentTask.task.timeOfExecute = 0;
                currentTask.task.overallWaitTime = systemTime - currentTask.task.startTime;
                taskStatistics.add(currentTask.task);
            } else {
                if (currentTask.time < currentTask.task.timeOfExecute) {
                    systemTime += currentTask.time;
                    deltaTime = currentTask.time;
                    currentTask.task.timeOfExecute -= currentTask.time;
                    queue.addTaskInNextQueue(currentTask.task, currentTask.queueNumber);
                } else {
                    systemTime +=currentTask.task.timeOfExecute;
                    deltaTime = currentTask.task.timeOfExecute;
                    currentTask.task.timeOfExecute = 0;
                    currentTask.task.overallWaitTime = systemTime - currentTask.task.startTime;
                    taskStatistics.add(currentTask.task);
                }
            }
            addUsedTime(deltaTime);
        } else {
            systemTime++;
            deltaTime++;
            procStatistics.add(false);
        }
    }
    private void addUsedTime(int counter){
        for(int i = 0; i < counter; i++){
            procStatistics.add(true);
        }
    }
}