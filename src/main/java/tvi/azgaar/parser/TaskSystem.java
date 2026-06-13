package tvi.azgaar.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import tvi.azgaar.parser.models.geography.MapNode;
import tvi.azgaar.parser.models.geopol.State;
import tvi.azgaar.parser.models.linguistic.Culture;
import tvi.azgaar.parser.tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;

public class TaskSystem {
    private final HashMap<String, Task> tasks;
    private int loadingSteps = 0;

    private final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

    public TaskSystem() {
        tasks = new HashMap<>();
        tasks.put("MESH", new MeshTask());
        tasks.put("CULTURES", new CultureTask()); // Fixed: matches your CulturesTask class name
        tasks.put("RELIGIONS", new ReligionTask()); // Fixed: made plural to match conventions
        tasks.put("STATES", new StateTask()); // Fixed: pluralized to follow your design standard
        tasks.put("ROUTES", new RoutesTask());
        tasks.put("MILITARY", new MilitaryTask());
        tasks.put("BIOMES", new BiomeTask());
        tasks.put("ZONES", new ZoneTask());
        tasks.put("MARKERS", new MarkersTask());
        tasks.put("RIVERS", new RiversTask());
        tasks.put("BURGS", new BurgsTask());
        tasks.put("PROVINCES", new ProvincesTask());
        tasks.put("FEATURES", new FeaturesTask());
        tasks.put("NOTES", new NotesTask());
        tasks.put("NAMEBASES", new NameBasesTask()); // Fixed: plural layout convention
    }

    public Task getTask(String key) {
        return tasks.get(key);
    }
    // 🔄 Layer 1: Geography Compilation (loadSteps = 0)
    public void compileGeographyLayer(String inputPath, String outputPath) {
        System.out.println("📐 [TaskSystem] Compiling Layer 1: Geography...");
        JsonObject mapOutput = new JsonObject();

        mapOutput.add("biomes", tasks.get("BIOMES").execute(inputPath));
        mapOutput.add("nodes", tasks.get("MESH").execute(inputPath));
        mapOutput.add("rivers", tasks.get("RIVERS").execute(inputPath));
        mapOutput.add("features", tasks.get("FEATURES").execute(inputPath));
        mapOutput.add("routes", tasks.get("ROUTES").execute(inputPath));

        // Fixed: Writes directly into the flat Azgaar Parser folder with no sub-directories
        writeJsonToFile(mapOutput, outputPath + "map.json");
    }

    // 🔄 Layer 2: Geopolitics Compilation (loadSteps = 1)
    public void compileGeopoliticsLayer(String inputPath, String outputPath) {
        System.out.println("👑 [TaskSystem] Compiling Layer 2: Geopolitics...");
        JsonObject statesOutput = new JsonObject();

        // Fixed: Uses the clean, matching pluralized "STATES" key string
        statesOutput.add("states", tasks.get("STATES").execute(inputPath));
        statesOutput.add("provinces", tasks.get("PROVINCES").execute(inputPath));
        statesOutput.add("zones", tasks.get("ZONES").execute(inputPath));
        statesOutput.add("military", tasks.get("MILITARY").execute(inputPath));
        statesOutput.add("burgs", tasks.get("BURGS").execute(inputPath));

        writeJsonToFile(statesOutput, outputPath + "states.json");
    }

    // 🔄 Layer 3: Society/Linguistics Compilation (loadSteps = 2)
    public void compileSocietyLayer(String inputPath, String outputPath) {
        System.out.println("🔤 [TaskSystem] Compiling Layer 3: Society & Linguistics...");
        JsonObject societyOutput = new JsonObject();

        // Fixed: Keys align completely with the upper plural registrations
        societyOutput.add("cultures", tasks.get("CULTURES").execute(inputPath));
        societyOutput.add("religions", tasks.get("RELIGIONS").execute(inputPath));
        societyOutput.add("nameBases", tasks.get("NAMEBASES").execute(inputPath));
        societyOutput.add("notes", tasks.get("NOTES").execute(inputPath));
        societyOutput.add("markets", tasks.get("MARKERS").execute(inputPath));

        writeJsonToFile(societyOutput, outputPath + "society.json");
        System.out.println("🟢 [TaskSystem Success] All 3 data compilation layers successfully baked!");
    }

    // 📥 Layer 1: Geography & Property Ingestion (loadSteps = 0)
    public List<MapNode> loadGeographyLayer(String outputPath) {
        System.out.println("📥 [TaskSystem] Loading Layer 1: Geography & Cell Properties...");
        String mapPath = outputPath + "map.json";

        // MeshTask.load returns your fully hydrated List<MapNode> containing all your property bags!
        MeshTask meshTask = (MeshTask) tasks.get("MESH");
        List<MapNode> masterNodes = (List<MapNode>) meshTask.load(mapPath);

        loadingSteps = 1; // Explicitly advance checkpoint to step 1
        return masterNodes;
    }

    // 📥 Layer 2: Geopolitics Ingestion (loadSteps = 1)
    public List<State> loadGeopoliticsLayer(String outputPath) {
        System.out.println("📥 [TaskSystem] Loading Layer 2: Geopolitics...");
        String statesPath = outputPath + "states.json";

        // StateTask.load handles reading the clean state arrays
        StateTask stateTask = (StateTask) tasks.get("STATES");
        List<State> masterStates = (List<State>) stateTask.load(statesPath);

        loadingSteps = 2; // Explicitly advance checkpoint to step 2
        return masterStates;
    }

    // 📥 Layer 3: Society & Linguistics Ingestion (loadSteps = 2)
    public List<Culture> loadSocietyLayer(String outputPath) {
        System.out.println("📥 [TaskSystem] Loading Layer 3: Society & Linguistics...");
        String societyPath = outputPath + "society.json";

        // CultureTask.load handles reading the clean culture arrays
        CultureTask cultureTask = (CultureTask) tasks.get("CULTURES");
        List<Culture> masterCultures = (List<Culture>) cultureTask.load(societyPath);

        loadingSteps = 3; // Gate completely cleared!
        System.out.println("🟢 [TaskSystem Success] All 3 data layers successfully hydrated from your JSON products!");
        return masterCultures;
    }

    private void writeJsonToFile(JsonObject json, String destinationPath) {
        try {
            File file = new File(destinationPath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (FileWriter writer = new FileWriter(file)) {
                prettyGson.toJson(json, writer);
            }
        } catch (Exception e) {
            System.err.println("🔴 [TaskSystem Error] Failed to write file to: " + destinationPath);
            e.printStackTrace();
        }
    }

    public void executeTasks(String inputPath, String outputPath) {
        for (String key : tasks.keySet()) {
            switch (key) {
                case "MESH":
                    MeshTask meshTask = (MeshTask) tasks.get(key);
                    meshTask.execute(inputPath);
                    break;
                case "CULTURES":
                    CultureTask cultureTask = (CultureTask) tasks.get(key);
                    cultureTask.execute(inputPath);
                    break;
                case "RELIGION":
                    ReligionTask religionTask = (ReligionTask) tasks.get(key);
                    religionTask.execute(inputPath);
                    break;
                case "STATE":
                    StateTask stateTask = (StateTask) tasks.get(key);
                    stateTask.execute(inputPath);
                    break;
                case "ROUTES":
                    RoutesTask routesTask = (RoutesTask) tasks.get(key);
                    routesTask.execute(inputPath);
                    break;
                case "MILITARY":
                    MilitaryTask militaryTask = (MilitaryTask) tasks.get(key);
                    militaryTask.execute(inputPath);
                    break;
                case "MARKERS":
                    MarkersTask reliefTask = (MarkersTask) tasks.get(key);
                    reliefTask.execute(inputPath);
                    break;
                case "ZONES":
                    ZoneTask zoneTask = (ZoneTask) tasks.get(key);
                    zoneTask.execute(inputPath);
                    break;
                case "BIOMES":
                    BiomeTask biomeTask = (BiomeTask) tasks.get(key);
                    biomeTask.execute(inputPath);
                    break;
                case "RIVERS":
                    RiversTask riverTask = (RiversTask) tasks.get(key);
                    riverTask.execute(inputPath);
                    break;
                case "BURGS":
                    BurgsTask burgsTask = (BurgsTask) tasks.get(key);
                    burgsTask.execute(inputPath);
                    break;
                case "PROVINCES":
                    ProvincesTask provincesTask = (ProvincesTask) tasks.get(key);
                    provincesTask.execute(inputPath);
                    break;
                case "FEATURES":
                    FeaturesTask featuresTask = (FeaturesTask) tasks.get(key);
                    featuresTask.execute(inputPath);
                    break;
                case "NOTES":
                    NotesTask notesTask = (NotesTask) tasks.get(key);
                    notesTask.execute(inputPath);
                    break;
                case "NAMES":
                    NameBasesTask nameBasesTask = (NameBasesTask) tasks.get(key);
                    nameBasesTask.execute(inputPath);
                    break;
            }
        }
    }
}
