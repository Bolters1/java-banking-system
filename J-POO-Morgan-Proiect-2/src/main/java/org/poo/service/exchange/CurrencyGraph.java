package org.poo.service.exchange;
import java.util.*;

public class CurrencyGraph {
    private final Map<String, Map<String, Double>> graph;

    public CurrencyGraph() {
        this.graph = new HashMap<>();
    }

    public void addEdge(String from, String to, double rate) {
        graph.putIfAbsent(from, new HashMap<>());
        graph.get(from).put(to, rate);
        graph.putIfAbsent(to, new HashMap<>());
        graph.get(to).put(from, 1 / rate);
    }

    public Double getRate(String from, String to) {
        return graph.get(from).get(to);
    }

    public Map<String, Map<String, Double>> getGraph() {
        return graph;
    }
    public Double findExchangeRate(String start, String end) {
        Set<String> visited = new HashSet<>();
        return dfsRate(start, end, visited, 1.0);
    }

    private Double dfsRate(String current, String end, Set<String> visited, double cumulativeRate) {
        visited.add(current);

        if (current.equals(end)) {
            return cumulativeRate;
        }

        for (Map.Entry<String, Double> neighbor : graph.getOrDefault(current, Collections.emptyMap()).entrySet()) {
            if (!visited.contains(neighbor.getKey())) {
                Double result = dfsRate(neighbor.getKey(), end, visited, cumulativeRate * neighbor.getValue());
                if (result != null) {
                    return result;
                }
            }
        }

        return null; // No path found
    }
}
