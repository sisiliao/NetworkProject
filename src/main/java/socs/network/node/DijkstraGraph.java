package socs.network.node;

import socs.network.message.LSA;
import socs.network.message.LinkDescription;

import java.util.*;

public class DijkstraGraph {
    private Map<String, LSA> LSAs;

    public DijkstraGraph(Map<String, LSA> newLSAs) {
        this.LSAs = newLSAs;
    }

    public Set<String> getNodes() {
        return LSAs.keySet();
    }

    public Set<LinkDescription> getEdges(String thisNode) {
        return new HashSet<LinkDescription>(LSAs.get(thisNode).links);
    }

    public int getWeight(LinkDescription thisEdge) {
        return thisEdge.tosMetrics;
    }

    public String getChild(LinkDescription thisEdge) {
        return thisEdge.linkID;
    }

    public LinkDescription getEdge(String thisParent, String thisChild) {
        LinkedList<LinkDescription> allLinks = LSAs.get(thisParent).links;
        if (thisParent.equals(thisChild)) {
            return null;
        } else {
            for (LinkDescription desc : allLinks) {
                if (desc.linkID.equals(thisChild)) {
                    return desc;
                }
            }
        }
        return null;
    }

}
