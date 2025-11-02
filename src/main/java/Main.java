import graph.Metrics;
import graph.scc.StronglyConnectedComponents;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPath;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            Metrics metrics = new Metrics();

            Map<String, List<String>> dependencyGraph = generateSampleGraph();

            System.out.println(" Smart Campus Scheduling Analysis \n");

            System.out.println("1. STRONGLY CONNECTED COMPONENTS");
            StronglyConnectedComponents scc = new StronglyConnectedComponents(metrics);
            List<List<String>> sccs = scc.findSCCs(dependencyGraph);

            System.out.println("Found " + sccs.size() + " SCCs:");
            for (int i = 0; i < sccs.size(); i++) {
                System.out.println("  SCC " + i + ": " + sccs.get(i) + " (size: " + sccs.get(i).size() + ")");
            }
            System.out.println("SCC Metrics - Time: " + metrics.getElapsedTime() + "ns, DFS Visits: " +
                    metrics.getDfsVisits() + ", Edges: " + metrics.getEdgesTraversed());

            System.out.println("\n2. CONDENSATION GRAPH & TOPOLOGICAL SORT");
            Map<Integer, List<Integer>> condensationGraph =
                    scc.buildCondensationGraph(sccs, dependencyGraph);

            metrics.reset();
            TopologicalSort topo = new TopologicalSort(metrics);
            List<Integer> topologicalOrder = topo.kahnTopologicalSort(condensationGraph);

            System.out.println("Topological Order: " + topologicalOrder);
            System.out.println("Topo Sort Metrics - Time: " + metrics.getElapsedTime() + "ns, Pushes: " +
                    metrics.getStackPushes() + ", Pops: " + metrics.getStackPops());

            System.out.println("\n3. SHORTEST AND LONGEST PATHS IN DAG");
            DAGShortestPath dagSP = new DAGShortestPath(metrics);
            DAGShortestPath.GraphWithWeights weightedGraph = generateWeightedGraph(condensationGraph);

            metrics.reset();

            if (!topologicalOrder.isEmpty()) {
                Map<Integer, Integer> shortestDistances =
                        dagSP.shortestPaths(weightedGraph, topologicalOrder, topologicalOrder.get(0));
                System.out.println("Shortest distances from node " + topologicalOrder.get(0) + ": " + shortestDistances);
            }

            metrics.reset();
            DAGShortestPath.CriticalPathResult criticalPath =
                    dagSP.longestPath(weightedGraph, topologicalOrder);

            System.out.println("Critical Path: " + criticalPath.criticalPath);
            System.out.println("Critical Path Length: " + criticalPath.criticalPathLength);
            System.out.println("DAG SP Metrics - Time: " + metrics.getElapsedTime() + "ns, Relaxations: " +
                    metrics.getRelaxations());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, List<String>> generateSampleGraph() {
        Map<String, List<String>> graph = new HashMap<>();

        graph.put("A", Arrays.asList("B", "C"));
        graph.put("B", Arrays.asList("D", "E"));
        graph.put("C", Arrays.asList("F"));
        graph.put("D", Arrays.asList("B"));
        graph.put("E", Arrays.asList("F"));
        graph.put("F", Arrays.asList("G"));
        graph.put("G", Arrays.asList("C"));
        graph.put("H", Arrays.asList("I"));
        graph.put("I", new ArrayList<>());

        return graph;
    }

    private static DAGShortestPath.GraphWithWeights generateWeightedGraph(
            Map<Integer, List<Integer>> condensationGraph) {

        DAGShortestPath.GraphWithWeights weightedGraph = new DAGShortestPath.GraphWithWeights();
        weightedGraph.graph = condensationGraph;

        Random rand = new Random(42);

        for (Map.Entry<Integer, List<Integer>> entry : condensationGraph.entrySet()) {
            for (Integer neighbor : entry.getValue()) {
                String edgeKey = entry.getKey() + "-" + neighbor;
                weightedGraph.edgeWeights.put(edgeKey, rand.nextInt(10) + 1); // Weights 1-10
            }
        }

        return weightedGraph;
    }
}