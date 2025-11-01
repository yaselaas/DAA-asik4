package graph.topo;

import graph.Metrics;
import java.util.*;

/**
 * Kahn's algorithm for topological sorting
 */
public class TopologicalSort {
    private Metrics metrics;

    public TopologicalSort(Metrics metrics) {
        this.metrics = metrics;
    }

    public List<Integer> kahnTopologicalSort(Map<Integer, List<Integer>> graph) {
        metrics.startTimer();

        Map<Integer, Integer> inDegree = new HashMap<>();

        // Initialize in-degree for all nodes
        for (Integer node : graph.keySet()) {
            inDegree.putIfAbsent(node, 0);
            for (Integer neighbor : graph.get(node)) {
                inDegree.put(neighbor, inDegree.getOrDefault(neighbor, 0) + 1);
            }
        }

        // Queue for nodes with zero in-degree
        Queue<Integer> queue = new LinkedList<>();
        for (Map.Entry<Integer, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
                metrics.incrementStackPushes();
            }
        }

        List<Integer> topologicalOrder = new ArrayList<>();

        while (!queue.isEmpty()) {
            metrics.incrementStackPops();
            Integer node = queue.poll();
            topologicalOrder.add(node);

            for (Integer neighbor : graph.getOrDefault(node, new ArrayList<>())) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                    metrics.incrementStackPushes();
                }
            }
        }

        metrics.stopTimer();

        // Check for cycles
        if (topologicalOrder.size() != graph.size()) {
            throw new IllegalArgumentException("Graph has cycles, topological sort not possible");
        }

        return topologicalOrder;
    }
}