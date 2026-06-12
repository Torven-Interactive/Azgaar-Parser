package tvi.azgaar.parser.registry;

import tvi.azgaar.parser.models.linguistic.Culture;
import tvi.azgaar.parser.models.linguistic.NameBase;
import tvi.azgaar.parser.models.linguistic.Religion;

import java.util.ArrayList;
import java.util.List;

public class SocietyRegistry {
    public List<Culture> cultures = new ArrayList<>();
    public List<Religion> religions = new ArrayList<>();
    public List<NameBase> nameBases = new ArrayList<>();

    public SocietyRegistry() {}
}
