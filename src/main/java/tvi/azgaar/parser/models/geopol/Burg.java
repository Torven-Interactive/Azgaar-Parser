package tvi.azgaar.parser.models.geopol;

public class Burg {
    private int id;
    private String name;
    private int nodeId; // The MapNode ID where this city physically sits
    private int population;
    private boolean isCapital;

    public Burg() {}

    public int getId() { return id; }
    public String getName() { return name; }
    public int getNodeId() { return nodeId; }
    public int getPopulation() { return population; }
    public boolean isCapital() { return isCapital; }
}