package tvi.azgaar.parser.models.geopol;

public class MilitaryUnit {
    private int id;
    private String type;
    private String name;
    private int nodeId; // MapNode ID where the regiment is stationed
    private int size;

    public MilitaryUnit() {}

    public int getId() { return id; }
    public String getType() { return type; }
    public String getName() { return name; }
    public int getNodeId() { return nodeId; }
    public int getSize() { return size; }
}
