package graph;


public class Metrics {
    private long startTime;
    private long endTime;
    private int dfsVisits;
    private int edgesTraversed;
    private int stackPushes;
    private int stackPops;
    private int relaxations;

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public void stopTimer() {
        this.endTime = System.nanoTime();
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }

    public void incrementDfsVisits() { dfsVisits++; }
    public void incrementEdgesTraversed() { edgesTraversed++; }
    public void incrementStackPushes() { stackPushes++; }
    public void incrementStackPops() { stackPops++; }
    public void incrementRelaxations() { relaxations++; }

    public int getDfsVisits() { return dfsVisits; }
    public int getEdgesTraversed() { return edgesTraversed; }
    public int getStackPushes() { return stackPushes; }
    public int getStackPops() { return stackPops; }
    public int getRelaxations() { return relaxations; }

    public void reset() {
        dfsVisits = 0;
        edgesTraversed = 0;
        stackPushes = 0;
        stackPops = 0;
        relaxations = 0;
    }

    @Override
    public String toString() {
        return String.format(
                "Metrics{time=%dns, dfsVisits=%d, edges=%d, pushes=%d, pops=%d, relaxations=%d}",
                getElapsedTime(), dfsVisits, edgesTraversed, stackPushes, stackPops, relaxations
        );
    }
}