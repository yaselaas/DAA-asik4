package graph.dagsp;

import graph.Metrics;
import java.util.*;

/**
 * Shortest and longest paths in Directed Acyclic Graphs
 */
public class DAGShortestPath {
    private Metrics metrics;

    public DAGShortestPath(Metrics metrics) {
        this.metrics = metrics;
    }

    public static class GraphWithWeights {
        public Map<Integer, List<Integer>> graph;
        public Map<String, Integer> edgeWeights; // Key: "from-to", Value: weight

        public GraphWithWeights() {
            graph = new HashMap<>();
            edgeWeights = new HashMap<>();
        }
    }

    /**
     * Single-source shortest paths using topological order
     */
    public Map<Integer, Integer> shortestPaths(
            GraphWithWeights graphWithWeights,
            List<Integer> topologicalOrder,
            int source) {

        metrics.startTimer();

        Map<Integer, Integer> dist = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();

        // Initialize distances
        for (Integer node : graphWithWeights.graph.keySet()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(source, 0);

        // Process nodes in topological order
        for (Integer node : topologicalOrder) {
            if (dist.get(node) != Integer.MAX_VALUE) {
                for (Integer neighbor : graphWithWeights.graph.getOrDefault(node, new ArrayList<>())) {
                    metrics.incrementRelaxations();

                    String edgeKey = node + "-" + neighbor;
                    int weight = graphWithWeights.edgeWeights.getOrDefault(edgeKey, 1);

                    int newDist = dist.get(node) + weight;
                    if (newDist < dist.get(neighbor)) {
                        dist.put(neighbor, newDist);
                        prev.put(neighbor, node);
                    }
                }
            }
        }

        metrics.stopTimer();
        return dist;
    }

    /**
     * Find longest path (critical path) using sign inversion
     */
    public CriticalPathResult longestPath(
            GraphWithWeights graphWithWeights,
            List<Integer> topologicalOrder) {

        metrics.startTimer();

        Map<Integer, Integer> dist = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();

        // Initialize distances to negative infinity for longest path
        for (Integer node : graphWithWeights.graph.keySet()) {
            dist.put(node, Integer.MIN_VALUE);
        }

        // Find nodes with zero in-degree as potential starts
        Set<Integer> startNodes = findStartNodes(graphWithWeights.graph);
        for (Integer start : startNodes) {
            dist.put(start, 0);
        }

        // Process in topological order
        for (Integer node : topologicalOrder) {
            if (dist.get(node) != Integer.MIN_VALUE) {
                for (Integer neighbor : graphWithWeights.graph.getOrDefault(node, new ArrayList<>())) {
                    metrics.incrementRelaxations();

                    String edgeKey = node + "-" + neighbor;
                    int weight = graphWithWeights.edgeWeights.getOrDefault(edgeKey, 1);

                    int newDist = dist.get(node) + weight;
                    if (newDist > dist.get(neighbor)) {
                        dist.put(neighbor, newDist);
                        prev.put(neighbor, node);
                    }
                }
            }
        }

        // Find the critical path (longest path)
        int maxDist = Integer.MIN_VALUE;
        int endNode = -1;
        for (Map.Entry<Integer, Integer> entry : dist.entrySet()) {
            if (entry.getValue() > maxDist) {
                maxDist = entry.getValue();
                endNode = entry.getKey();
            }
        }

        // Reconstruct critical path
        List<Integer> criticalPath = reconstructPath(prev, endNode, startNodes);

        metrics.stopTimer();

        return new CriticalPathResult(criticalPath, maxDist, dist);
    }

    private Set<Integer> findStartNodes(Map<Integer, List<Integer>> graph) {
        Set<Integer> allNodes = new HashSet<>(graph.keySet());
        Set<Integer> hasIncoming = new HashSet<>();

        for (List<Integer> neighbors : graph.values()) {
            hasIncoming.addAll(neighbors);
        }

        allNodes.removeAll(hasIncoming);
        return allNodes;
    }

    private List<Integer> reconstructPath(Map<Integer, Integer> prev, int endNode, Set<Integer> startNodes) {
        List<Integer> path = new ArrayList<>();
        Integer current = endNode;

        while (current != null) {
            path.add(0, current);
            current = prev.get(current);
            if (current != null && startNodes.contains(current)) {
                path.add(0, current);
                break;
            }
        }

        return path;
    }

    public static class CriticalPathResult {
        public final List<Integer> criticalPath;
        public final int criticalPathLength;
        public final Map<Integer, Integer> longestDistances;

        public CriticalPathResult(List<Integer> criticalPath, int criticalPathLength,
                                  Map<Integer, Integer> longestDistances) {
            this.criticalPath = criticalPath;
            this.criticalPathLength = criticalPathLength;
            this.longestDistances = longestDistances;
        }
    }
}