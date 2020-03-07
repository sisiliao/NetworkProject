package socs.network.node;

import socs.network.message.LinkDescription;

import java.util.*;

public class DjikstraAlgorithm {
    private DijkstraGraph graph;
    private String source;
    private Map<String, String> parentChild;
    private Map<String, Integer> distances;

    public DjikstraAlgorithm(DijkstraGraph grph, String src) {
        this.graph = grph;
        this.source = src;
        this.parentChild = new HashMap<String, String>();
        this.distances = new HashMap<String, Integer>();

        String curr, child;
        curr = source;

        Set<String> nodes = new HashSet<String>(graph.getNodes());

        distances.put(source, 0);

        while (!nodes.isEmpty()) {

            Set<LinkDescription> edgeSet = graph.getEdges(curr);
            Set<Map.Entry<String, Integer>> distSet = distances.entrySet();

            if (nodes.remove(curr)) {
                for (LinkDescription e : edgeSet) {
                    child = graph.getChild(e);
                    if (nodes.contains(child)) {
                        if (!distances.containsKey(child) || distances.get(child) > (distances.get(curr) + graph.getWeight(e))) {
                            parentChild.put(child, curr);
                            distances.put(child, distances.get(curr) + graph.getWeight(e));
                        }
                    }
                }
                int min = Integer.MAX_VALUE;
                for (Map.Entry<String, Integer> d : distSet) {
                    if (d.getValue() < min && nodes.contains(d.getKey())) {
                        min = d.getValue();
                        curr = d.getKey();
                    }
                }
            } else break;

        }

    }

    public DijkstraPath shortestPath(String destination) {
        DijkstraPath pathFound = null;
        if (distances.containsKey(destination)) {
            List<String> path = new ArrayList<String>();
            String thisNode = destination;
            do {
                path.add(thisNode);
            } while ((thisNode = parentChild.get(thisNode)) != null);
            Collections.reverse(path);
            pathFound = new DijkstraPath(graph, path);
        }
        return pathFound;
    }
}
