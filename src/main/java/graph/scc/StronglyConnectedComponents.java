package graph.scc;

import graph.Metrics;
import java.util.*;


public class StronglyConnectedComponents {
    private Map<String, List<String>> graph;
    private Map<String, Integer> indices;
    private Map<String, Integer> lowLinks;
    private Set<String> onStack;
    private Stack<String> stack;
    private int index;
    private List<List<String>> sccs;
    private Metrics metrics;

    public StronglyConnectedComponents(Metrics metrics) {
        this.metrics = metrics;
    }

    public List<List<String>> findSCCs(Map<String, List<String>> graph) {
        this.graph = graph;
        indices = new HashMap<>();
        lowLinks = new HashMap<>();
        onStack = new HashSet<>();
        stack = new Stack<>();
        sccs = new ArrayList<>();
        index = 0;

        metrics.startTimer();

        for (String node : graph.keySet()) {
            if (!indices.containsKey(node)) {
                strongConnect(node);
            }
        }

        metrics.stopTimer();
        return sccs;
    }

    private void strongConnect(String node) {
        metrics.incrementDfsVisits();

        indices.put(node, index);
        lowLinks.put(node, index);
        index++;
        stack.push(node);
        onStack.add(node);

        List<String> neighbors = graph.getOrDefault(node, new ArrayList<>());
        for (String neighbor : neighbors) {
            metrics.incrementEdgesTraversed();

            if (!indices.containsKey(neighbor)) {
                strongConnect(neighbor);
                lowLinks.put(node, Math.min(lowLinks.get(node), lowLinks.get(neighbor)));
            } else if (onStack.contains(neighbor)) {
                lowLinks.put(node, Math.min(lowLinks.get(node), indices.get(neighbor)));
            }
        }

        if (lowLinks.get(node).equals(indices.get(node))) {
            List<String> scc = new ArrayList<>();
            String popNode;
            do {
                popNode = stack.pop();
                onStack.remove(popNode);
                scc.add(popNode);
            } while (!popNode.equals(node));
            sccs.add(scc);
        }
    }

    public Map<Integer, List<Integer>> buildCondensationGraph(
            List<List<String>> sccs,
            Map<String, List<String>> originalGraph) {

        Map<String, Integer> nodeToComponent = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            for (String node : sccs.get(i)) {
                nodeToComponent.put(node, i);
            }
        }

        Map<Integer, List<Integer>> condensationGraph = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            condensationGraph.put(i, new ArrayList<>());
        }

        for (Map.Entry<String, List<String>> entry : originalGraph.entrySet()) {
            int fromComp = nodeToComponent.get(entry.getKey());
            for (String neighbor : entry.getValue()) {
                int toComp = nodeToComponent.get(neighbor);
                if (fromComp != toComp) {
                    List<Integer> edges = condensationGraph.get(fromComp);
                    if (!edges.contains(toComp)) {
                        edges.add(toComp);
                    }
                }
            }
        }

        return condensationGraph;
    }
}