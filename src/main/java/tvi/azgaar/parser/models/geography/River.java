package tvi.azgaar.parser.models.geography;

import java.util.List;

public class River {
    private int id;
    private String name;
    private List<Integer> path_nodes;

    public River() {

    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getPathNodes() {
        return path_nodes;
    }

    public Integer getPathNode(int index) {
        return path_nodes.get(index);
    }
}
