package tvi.azgaar.parser.models.geopol;

import java.util.List;

public class Province {
    private int id;
    private String name;
    private List<Integer> ownedNodes; // MapNode IDs within this sub-region

    public Province() {}

    public int getId() { return id; }
    public String getName() { return name; }
    public List<Integer> getOwnedNodes() { return ownedNodes; }
}
