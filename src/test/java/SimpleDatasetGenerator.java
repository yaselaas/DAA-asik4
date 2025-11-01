import java.io.*;
import java.util.*;

public class SimpleDatasetGenerator {
    private static final Random random = new Random(42);

    public static void main(String[] args) throws Exception {
        generateAllDatasets();
        System.out.println("Все датасеты созданы успешно!");
    }

    public static void generateAllDatasets() throws Exception {

        generateDataset("small_acyclic", 8, 12, false);
        generateDataset("small_cyclic", 10, 15, true);
        generateDataset("small_mixed", 9, 14, true);

        generateDataset("medium_acyclic", 15, 25, false);
        generateDataset("medium_cyclic", 18, 30, true);
        generateDataset("medium_mixed", 20, 35, true);

        generateDataset("large_acyclic", 35, 60, false);
        generateDataset("large_cyclic", 45, 80, true);
        generateDataset("large_mixed", 50, 90, true);
    }

    private static void generateDataset(String name, int nodes, int edges, boolean allowCycles)
            throws Exception {

        new File("data").mkdirs();

        Map<String, List<String>> graph = new HashMap<>();
        List<String> nodeList = new ArrayList<>();
        
        for (int i = 0; i < nodes; i++) {
            String nodeName = "T" + i;
            nodeList.add(nodeName);
            graph.put(nodeName, new ArrayList<>());
        }

        // Добавить рёбра
        int edgesAdded = 0;
        int attempts = 0;
        int maxAttempts = edges * 10;

        while (edgesAdded < edges && attempts < maxAttempts) {
            String from = nodeList.get(random.nextInt(nodes));
            String to = nodeList.get(random.nextInt(nodes));

            if (!from.equals(to) && !graph.get(from).contains(to)) {
                if (allowCycles || !wouldCreateCycle(graph, from, to)) {
                    graph.get(from).add(to);
                    edgesAdded++;
                }
            }
            attempts++;
        }

        writeGraphToFile(name, nodes, edges, graph);
    }

    private static boolean wouldCreateCycle(Map<String, List<String>> graph, String from, String to) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(to);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(from)) {
                return true;
            }
            if (!visited.contains(current)) {
                visited.add(current);
                queue.addAll(graph.get(current));
            }
        }
        return false;
    }

    private static void writeGraphToFile(String name, int nodes, int edges,
                                         Map<String, List<String>> graph) throws Exception {
        File file = new File("data/" + name + ".txt");
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("# Dataset: " + name);
            writer.println("# Nodes: " + nodes + ", Edges: " + edges);
            writer.println("# Format: from -> to1,to2,to3");
            writer.println();

            for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    writer.println(entry.getKey() + " -> " + String.join(",", entry.getValue()));
                }
            }
        }

        System.out.println("Generated " + name + ": " + nodes + " nodes, " + edges + " edges");
    }
}