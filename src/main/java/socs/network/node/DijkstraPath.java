package socs.network.node;

import socs.network.message.LinkDescription;

import java.util.ArrayList;
import java.util.List;

public class DijkstraPath {
    private DijkstraGraph graph;
    private List<String> nodes;
    private List<LinkDescription> edges;

    public DijkstraPath(DijkstraGraph newGraph, List<String> addNodes) {
        graph = newGraph;
        nodes = new ArrayList<String>(addNodes);
        edges = new ArrayList<LinkDescription>();
        String parentNode, childNode;
        LinkDescription e;
        for (int i = 0; i < nodes.size()-1; i++) {
            parentNode = nodes.get(i);
            childNode = nodes.get(i+1);
            e = graph.getEdge(parentNode, childNode);
            if (e != null) {
                edges.add(e);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = true;
        DijkstraPath thisPath;
        if (obj instanceof DijkstraPath) {
            thisPath = (DijkstraPath) obj;
            return (result && thisPath.nodes.equals(nodes) && thisPath.edges.equals(edges));
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < nodes.size(); i++) {
            str = str + nodes.get(i);
            if (i < edges.size()) {
                str = str + "->(" + graph.getWeight(edges.get(i)) + ") ";
            }
        }
        return str;
    }
}
