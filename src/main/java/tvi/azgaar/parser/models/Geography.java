package tvi.azgaar.parser.models;

import tvi.azgaar.parser.models.geography.Biome;
import tvi.azgaar.parser.models.geography.Cell;
import tvi.azgaar.parser.models.geography.River;

import java.util.ArrayList;
import java.util.List;

public class Geography {
    public List<Biome> biomes = new ArrayList<>();
    public List<Cell> nodes = new ArrayList<>();
    public List<River> rivers = new ArrayList<>();

    public Geography() {

    }
}
