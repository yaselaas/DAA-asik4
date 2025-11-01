package graph.scc;

import graph.Metrics;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class SCCTest {

    @Test
    public void testSCCWithCycle() {
        Metrics metrics = new Metrics();
        StronglyConnectedComponents scc = new StronglyConnectedComponents(metrics);

        Map<String, List<String>> graph = new HashMap<>();
        graph.put("A", Arrays.asList("B"));
        graph.put("B", Arrays.asList("C"));
        graph.put("C", Arrays.asList("A")); // Cycle A-B-C

        List<List<String>> sccs = scc.findSCCs(graph);

        assertEquals(1, sccs.size());
        assertTrue(sccs.get(0).containsAll(Arrays.asList("A", "B", "C")));
    }

    @Test
    public void testSCCWithDAG() {
        Metrics metrics = new Metrics();
        StronglyConnectedComponents scc = new StronglyConnectedComponents(metrics);

        Map<String, List<String>> graph = new HashMap<>();
        graph.put("A", Arrays.asList("B"));
        graph.put("B", Arrays.asList("C"));
        graph.put("C", Arrays.asList("D"));

        List<List<String>> sccs = scc.findSCCs(graph);

        assertEquals(4, sccs.size()); // Each node is its own SCC in a DAG
    }
}