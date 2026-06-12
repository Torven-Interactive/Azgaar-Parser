package tvi.azgaar.parser.models.linguistic;

public class Religion {
    private int id;
    private String name;
    private String type; // Monotheistic, Polytheistic, Shamanism, etc.

    public Religion() {}

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
}
