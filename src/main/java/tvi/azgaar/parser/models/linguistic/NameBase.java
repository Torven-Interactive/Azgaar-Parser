package tvi.azgaar.parser.models.linguistic;

import java.util.List;

public class NameBase {
    private int id;
    private String name;
    private List<String> bails; // Raw syllables or text fragments used by generation routines

    public NameBase() {}

    public int getId() { return id; }
    public String getName() { return name; }
    public List<String> getBails() { return bails; }
}