package tvi.azgaar.parser.models.linguistic;

public class Culture {
    private int id;
    private String name;
    private String nameBase; // The name base profile used for generating language strings

    public Culture() {}

    public int getId() { return id; }
    public String getName() { return name; }
    public String getNameBase() { return nameBase; }
}
