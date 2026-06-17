package tvi.azgaar.parser;

import com.google.gson.*;
import tvi.azgaar.parser.models.geography.Cell;
import tvi.azgaar.parser.models.geopol.State;
import tvi.azgaar.parser.models.linguistic.Culture;
import tvi.azgaar.parser.tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskSystem {
    private final HashMap<String, Task> tasks;
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
        writeJsonToFile((JsonObject) tasks.get("BIOMES").execute(inputPath), outputPath + "biomes.json");
        writeJsonToFile((JsonObject) tasks.get("MESH").execute(inputPath), outputPath + "map.json");
        writeJsonToFile((JsonObject) tasks.get("RIVERS").execute(inputPath), outputPath + "rivers.json");
        writeJsonToFile((JsonObject) tasks.get("FEATURES").execute(inputPath), outputPath + "features.json");
        writeJsonToFile((JsonObject) tasks.get("ROUTES").execute(inputPath), outputPath + "routes.json");
        writeJsonToFile((JsonObject) tasks.get("MARKERS").execute(inputPath), outputPath + "markers.json");
        /*  JsonObject mapOutput = new JsonObject();
            mapOutput.add("biomes", tasks.get("BIOMES").execute(inputPath));
            mapOutput.add("nodes", tasks.get("MESH").execute(inputPath));
            mapOutput.add("rivers", tasks.get("RIVERS").execute(inputPath));
            mapOutput.add("features", tasks.get("FEATURES").execute(inputPath));
            mapOutput.add("routes", tasks.get("ROUTES").execute(inputPath));*/
    }

    // 🔄 Layer 2: Geopolitics Compilation (loadSteps = 1)
    public void compileGeopoliticsLayer(String inputPath, String outputPath) {
        System.out.println("👑 [TaskSystem] Compiling Layer 2: Geopolitics...");
        writeJsonToFile((JsonObject) tasks.get("STATES").execute(inputPath), outputPath + "states.json");
        writeJsonToFile((JsonObject) tasks.get("PROVINCES").execute(inputPath), outputPath + "provinces.json");
        writeJsonToFile((JsonObject) tasks.get("BURGS").execute(inputPath), outputPath + "burgs.json");
        writeJsonToFile((JsonObject) tasks.get("ZONES").execute(inputPath), outputPath + "zones.json");
        writeJsonToFile((JsonObject) tasks.get("MILITARY").execute(inputPath), outputPath + "military.json");
    }

    // 🔄 Layer 3: Society/Linguistics Compilation (loadSteps = 2)
    public void compileSocietyLayer(String inputPath, String outputPath) {
        System.out.println("🔤 [TaskSystem] Compiling Layer 3: Society & Linguistics...");
        writeJsonToFile((JsonObject) tasks.get("CULTURES").execute(inputPath), outputPath + "cultures.json");
        writeJsonToFile((JsonObject) tasks.get("RELIGIONS").execute(inputPath), outputPath + "religions.json");
        writeJsonToFile((JsonObject) tasks.get("NAMEBASES").execute(inputPath), outputPath + "namebases.json");
        writeJsonToFile((JsonObject) tasks.get("NOTES").execute(inputPath), outputPath + "notes.json");

        System.out.println("🟢 [TaskSystem Success] All 3 data compilation layers successfully baked!");
    }

    // 📥 Layer 1: Geography & Property Ingestion (loadSteps = 0)
    public List<Cell> loadGeographyLayer(String outputPath) {
        System.out.println("📥 [TaskSystem] Loading Layer 1: Geography & Cell Properties...");
        String mapPath = outputPath + "map.json";
        // MeshTask.load returns your fully hydrated List<MapNode> containing all your property bags!
        MeshTask meshTask = (MeshTask) tasks.get("MESH");
        List<Cell> masterNodes = (List<Cell>) meshTask.load(mapPath);
        return masterNodes;
    }

    // 📥 Layer 2: Geopolitics Ingestion (loadSteps = 1)
    public List<State> loadGeopoliticsLayer(String outputPath) {
        System.out.println("📥 [TaskSystem] Loading Layer 2: Geopolitics...");
        List<State> masterStates = new ArrayList<>();
        StateTask stateTask = (StateTask) tasks.get("STATES");

        // Point directly to the split folder location
        File splitStatesFolder = new File(outputPath + "states");

        // --- AUTOMATIC FORMAT SELECTION ---
        if (splitStatesFolder.exists() && splitStatesFolder.isDirectory()) {
            System.out.println("📂 [TaskSystem] Split Mode Detected. Reconstructing states dataset from directory...");

            File[] individualFiles = splitStatesFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
            if (individualFiles != null) {
                for (File file : individualFiles) {
                    // Pass each individual file path down to your existing task loader method
                    Object loadedResult = stateTask.load(file.getAbsolutePath());

                    // Handle whatever output shape your custom load logic returns (List or single Object)
                    if (loadedResult instanceof List) {
                        masterStates.addAll((List<State>) loadedResult);
                    } else if (loadedResult instanceof State) {
                        masterStates.add((State) loadedResult);
                    }
                }
            }
        } else {
            // Fallback: Read straight from your original single flat file layout
            String statesPath = outputPath + "states.json";
            System.out.println("📄 [TaskSystem] Single File Mode Detected. Hydrating from: " + statesPath);

            masterStates = (List<State>) stateTask.load(statesPath);
        }

        return masterStates;
    }

    // 📥 Layer 3: Society & Linguistics Ingestion (loadSteps = 2)
    public List<Culture> loadSocietyLayer(String outputPath) {
        System.out.println("📥 [TaskSystem] Loading Layer 3: Society & Linguistics...");
        String societyPath = outputPath + "society.json";
        // CultureTask.load handles reading the clean culture arrays
        CultureTask cultureTask = (CultureTask) tasks.get("CULTURES");
        List<Culture> masterCultures = (List<Culture>) cultureTask.load(societyPath);
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
}
