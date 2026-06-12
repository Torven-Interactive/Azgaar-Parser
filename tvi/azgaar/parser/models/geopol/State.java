package tvi.azgaar.parser.models.geopol;

import java.util.ArrayList;
import java.util.List;

public class State {
    private int id;
    private String name;
    private String color;
    private int cultureId;   // Points to global society table
    private int religionId;  // Points to global society table
    private List<Integer> ownedNodes; // Every MapNode ID inside this nation's borders

    // Nested Ownership Lists
    public List<Province> provinces = new ArrayList<>();
    public List<Burg> burgs = new ArrayList<>();
    public List<MilitaryUnit> military = new ArrayList<>();

    public State() {}

    public int getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public int getCultureId() { return cultureId; }
    public int getReligionId() { return religionId; }
    public List<Integer> getOwnedNodes() { return ownedNodes; }
}
