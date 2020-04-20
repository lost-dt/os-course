import java.util.Random;

public class Generator {
    private final int minTimeOfExecute;
    private final int maxTimeOfExecute;

    private final int maxPriority;
    private final double intensity;
    public Generator(double intensity, int mint, int maxt, int maxp){
        this.intensity = intensity;
        minTimeOfExecute = mint;
        maxTimeOfExecute = maxt;
        maxPriority = maxp;
    }
    public Task generate(int currentSystemTime){
        Random r = new Random();
        double posibility = r.nextDouble();
        if(posibility < intensity){
            int priority = r.nextInt(maxPriority);
            int timeOfExecution = r.nextInt(maxTimeOfExecute - minTimeOfExecute) + minTimeOfExecute;
            return new Task(priority, timeOfExecution, currentSystemTime);
        }
        return null;
    }
}