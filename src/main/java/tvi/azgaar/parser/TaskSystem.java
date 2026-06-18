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
    private final EventBus eventBus;
    private final HashMap<TaskType, Task> tasks;
    private final Gson prettyGson;

    private final HashMap<String, Object> mapData;

    public int loadSteps = 0;

    public TaskSystem() {
        eventBus = new EventBus();
        prettyGson = new GsonBuilder().setPrettyPrinting().create();

        tasks = new HashMap<>();
        registerTasks();

        mapData = new HashMap<>();

    }

    private void registerTasks() {
        for (TaskType type : TaskType.values()) {
            switch (type) {
                case MESH -> tasks.put(type, new MeshTask());
                case CULTURES -> tasks.put(type, new CultureTask());
                case RELIGIONS -> tasks.put(type, new ReligionTask());
                case STATES -> tasks.put(type, new StateTask());
                case ROUTES -> tasks.put(type, new RoutesTask());
                case MILITARY -> tasks.put(type, new MilitaryTask());
                case BIOMES -> tasks.put(type, new BiomeTask());
                case ZONES -> tasks.put(type, new ZoneTask());
                case MARKERS -> tasks.put(type, new MarkersTask());
                case RIVERS -> tasks.put(type, new RiversTask());
                case BURGS -> tasks.put(type, new BurgsTask());
                case PROVINCES -> tasks.put(type, new ProvincesTask());
                case FEATURES -> tasks.put(type, new FeaturesTask());
                case NOTES -> tasks.put(type, new NotesTask());
                case NAMES -> tasks.put(type, new NameBasesTask());
            }
        }
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public Gson getGson() {
        return prettyGson;
    }

    public Task getTask(String key) {
        return tasks.get(key);
    }

    public HashMap<String, Object> getMapData() {
        return mapData;
    }

    public void printTask(TaskType type, String inputPath) {
        JsonElement selectedTask = tasks.get(type).execute(inputPath);


        for (JsonElement object : selectedTask.getAsJsonArray()) {
            System.out.println(object);
        }
    }

    // 🔄 Layer 1: Geography Compilation (loadSteps = 0)
    public void compileGeographyLayer(String inputPath, String outputPath) {
        System.out.println("📐 [TaskSystem] Compiling Layer 1: Geography...");
        JsonElement mesh = tasks.get(TaskType.MESH).execute(inputPath);
        mapData.put("cells", mesh.getAsJsonArray());
        mapData.put("biomes", compileBiomes(inputPath, outputPath));
        mapData.put("rivers", compileRivers(inputPath, outputPath));
        mapData.put("features", compileFeatures(inputPath, outputPath));
        mapData.put("markers", compileMarkers(inputPath, outputPath));
        mapData.put("routes", compileRoutes(inputPath, outputPath));
    }

    // 🔄 Layer 2: Geopolitics Compilation (loadSteps = 1)
    public void compileGeopoliticsLayer(String inputPath, String outputPath) {
        System.out.println("👑 [TaskSystem] Compiling Layer 2: Geopolitics...");
        mapData.put("states", compileStates(inputPath, outputPath));
        mapData.put("provinces", compileProvinces(inputPath, outputPath));
        mapData.put("burgs", compileBurgs(inputPath, outputPath));
        mapData.put("zones", compileZones(inputPath, outputPath));
        mapData.put("military", compileMilitary(inputPath, outputPath));
    }

    // 🔄 Layer 3: Society/Linguistics Compilation (loadSteps = 2)
    public void compileSocietyLayer(String inputPath, String outputPath) {
        System.out.println("🔤 [TaskSystem] Compiling Layer 3: Society & Linguistics...");
        mapData.put("cultures", compileCultures(inputPath, outputPath));
        mapData.put("religions", compileReligions(inputPath, outputPath));

        compileNameBases(inputPath, outputPath);
        compileNotes(inputPath, outputPath);
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

    public void writeJsonToFile(JsonElement json, String destinationPath) {
        try {
            File file = new File(destinationPath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (FileWriter writer = new FileWriter(file)) {
                prettyGson.toJson(json, writer);
                writer.flush();
            }
        } catch (Exception e) {
            System.err.println("🔴 [TaskSystem Error] Failed to write file to: " + destinationPath);
            e.printStackTrace();
        }
    }

    private JsonElement compileBiomes(String inputPath, String outputPath) {
        eventBus.publish("Compiling Biomes....");
        JsonElement biomes = tasks.get(TaskType.BIOMES).execute(inputPath);
        JsonObject transferCache = new JsonObject();
        writeJsonToFile(biomes, outputPath + "biomes.json");
        return biomes;
    }

    private JsonElement compileRivers(String inputPath, String outputPath) {
        eventBus.publish("Compiling Rivers....");
        JsonObject riverGeos = new JsonObject();

        JsonElement rivers = tasks.get(TaskType.RIVERS).execute(inputPath);
        File riversDir = new File(outputPath + "rivers\\");
        if (!riversDir.exists()) riversDir.mkdirs();

        eventBus.publish("Writing Rivers to disk...");
        for (JsonElement object : rivers.getAsJsonArray()) {
            JsonObject river = object.getAsJsonObject();
            JsonObject transferCache = new JsonObject();

            // If it doesnt have the name field skip it
            if (!river.has("name")) continue;
            String legalName = river.get("name").getAsString() + " - " + river.get("i");

            eventBus.publish("Stripping Geo Features from River: " + legalName);
            transferCache.addProperty("i", river.get("i").getAsInt());
            transferCache.addProperty("source", river.get("source").getAsInt());
            transferCache.addProperty("mouth", river.get("mouth").getAsInt());
            transferCache.addProperty("discharge", river.get("discharge").getAsInt());
            transferCache.addProperty("width", river.get("width").getAsInt());
            transferCache.addProperty("length", river.get("length").getAsInt());
            transferCache.addProperty("widthFactor", river.get("widthFactor").getAsInt());
            transferCache.addProperty("sourceWidth", river.get("sourceWidth").getAsInt());
            riverGeos.add(river.get("name").getAsString(), transferCache);

            eventBus.publish("Cleaning up River Cache...");
            for (String key : transferCache.keySet()) {
                if (key.equals("i")) { continue; }
                river.remove(key);
            }

            eventBus.publish("Writing River to disk...");
            riverGeos.add(legalName, transferCache);
            File riverFile = new File(riversDir, legalName + ".json");
            writeJsonToFile(river, riverFile.getAbsolutePath());
        }

        eventBus.publish("River Compilation Complete!");
        return riverGeos;
    }

    private JsonElement compileFeatures(String inputPath, String outputPath) {
        eventBus.publish("Compiling Features....");
        JsonElement features = tasks.get(TaskType.FEATURES).execute(inputPath);
        JsonObject featureGeos = new JsonObject();

        for (JsonElement object : features.getAsJsonArray()) {
            JsonObject feature = object.getAsJsonObject();
            JsonObject transferCache2 = new JsonObject();

            if (!feature.has("name")) continue;
            String legalName = feature.get("name").getAsString() + " - " + feature.get("i");

            eventBus.publish("Stripping Geo Features from Feature: " + legalName);
            transferCache2.addProperty("i", feature.get("i").getAsInt());

            eventBus.publish("Cleaning up Feature Cache...");
            for (String key : transferCache2.keySet()) {
                if (key.equals("i")) { continue; }
                feature.remove(key);
            }

            eventBus.publish("Writing Feature to disk...");
            featureGeos.add(legalName, transferCache2);
            File featureFile = new File(outputPath + "features\\" + legalName + ".json");
            writeJsonToFile(feature, featureFile.getAbsolutePath());
        }

        eventBus.publish("Features Compilation Complete!...");
        return featureGeos;
    }

    private JsonElement compileMarkers(String inputPath, String outputPath) {
        eventBus.publish("Compiling Markers....");
        JsonElement markers = tasks.get(TaskType.MARKERS).execute(inputPath);
        JsonObject markerGeos = new JsonObject();

        File markersDir = new File(outputPath + "markers\\");
        if (!markersDir.exists()) markersDir.mkdirs();

        for (JsonElement object : markers.getAsJsonArray()) {
            JsonObject marker = object.getAsJsonObject();
            JsonObject transferCache = new JsonObject();

            // If it doesnt have the name field skip it
            if (!marker.has("type")) continue;
            String legalName = marker.get("type").getAsString() + " - " + marker.get("i");

            eventBus.publish("Stripping Geo Features from Marker: " + legalName);
            transferCache.addProperty("i", marker.get("i").getAsInt());

            eventBus.publish("Cleaning up Marker Cache...");
            for (String key : transferCache.keySet()) {
                if (key.equals("i")) { continue; }
                marker.remove(key);
            }

            eventBus.publish("Writing Marker to disk...");
            markerGeos.add(legalName, transferCache);
            File markerFile = new File(markersDir, legalName + ".json");
            writeJsonToFile(marker, markerFile.getAbsolutePath());
        }

        eventBus.publish("Markers Compilation Complete!...");
        return markerGeos;
    }

    private JsonElement compileRoutes(String inputPath, String outputPath) {
        eventBus.publish("Compiling Routes....");
        JsonObject routeGeos = new JsonObject();

        JsonElement routes = tasks.get(TaskType.ROUTES).execute(inputPath);
        File routesDir = new File(outputPath + "routes\\");
        if (!routesDir.exists()) routesDir.mkdirs();

        for (JsonElement object : routes.getAsJsonArray()) {
            JsonObject route = object.getAsJsonObject();
            JsonObject transferCache = new JsonObject();

            if (!route.has("group")) continue;
            String legalName = route.get("group").getAsString() + " - " + route.get("i");

            eventBus.publish("Stripping Geo Features from Route: " + legalName);
            transferCache.addProperty("i", route.get("i").getAsInt());
            transferCache.add("points", route.get("points"));

            eventBus.publish("Cleaning up Route Cache...");
            for (String key : transferCache.keySet()) {
                if (key.equals("i")) { continue; }
                route.remove(key);
            }

            routeGeos.add(legalName, transferCache);

            eventBus.publish("Writing Route to disk...");
            File routeFile = new File(routesDir, legalName + ".json");
            writeJsonToFile(route, routeFile.getAbsolutePath());
        }

        eventBus.publish("Route Compilation Complete!...");
        return routeGeos;
    }

    private JsonElement compileStates(String inputPath, String outputPath) {
        eventBus.publish("Compiling States....");
        JsonElement states = tasks.get(TaskType.STATES).execute(inputPath);
        JsonObject stateGeos = new JsonObject();

        File statesDir = new File(outputPath + "states\\");
        if (!statesDir.exists()) statesDir.mkdirs();

        for (JsonElement object : states.getAsJsonArray()) {
            JsonObject state = object.getAsJsonObject();
            JsonObject transferCache = new JsonObject();

            if (!state.has("name")) continue;
            String legalName = state.get("name").getAsString() + " - " + state.get("i");

            eventBus.publish("Stripping Geo Features from State: " + legalName);
            transferCache.addProperty("i", state.get("i").getAsInt());

            eventBus.publish("Cleaning up State Cache...");
            for (String key : transferCache.keySet()) {
                if (key.equals("i")) { continue; }
                state.remove(key);
            }

            eventBus.publish("Writing State to disk...");
            stateGeos.add(legalName, transferCache);
            File stateFile = new File(outputPath + "states\\" + legalName + ".json");
            writeJsonToFile(state, stateFile.getAbsolutePath());
        }

        eventBus.publish("States Compilation Complete!...");
        return stateGeos;
    }

    private JsonElement compileProvinces(String inputPath, String outputPath) {
        eventBus.publish("Compiling Provinces....");
        JsonElement provinces = tasks.get(TaskType.PROVINCES).execute(inputPath);
        JsonObject provinceGeos = new JsonObject();

        File provincesDir = new File(outputPath + "provinces\\");
        if (!provincesDir.exists()) provincesDir.mkdirs();

        for (JsonElement object : provinces.getAsJsonArray()) {
            JsonObject province = object.getAsJsonObject();
            JsonObject transferCache = new JsonObject();

            if (!province.has("name")) continue;
            String legalName = province.get("name").getAsString() + " - " + province.get("i");

            eventBus.publish("Stripping Geo Features from Province: " + legalName);
            transferCache.addProperty("i", province.get("i").getAsInt());

            eventBus.publish("Cleaning up Province Cache...");
            for (String key : transferCache.keySet()) {
                if (key.equals("i")) { continue; }
                province.remove(key);
            }

            eventBus.publish("Writing Province to disk...");
            provinceGeos.add(legalName, transferCache);
            File provinceFile = new File(provincesDir, legalName + ".json");
            writeJsonToFile(province, provinceFile.getAbsolutePath());
        }

        eventBus.publish("Provinces Compilation Complete!...");
        return provinceGeos;
    }


    private JsonElement compileBurgs(String inputPath, String outputPath) {
        eventBus.publish("Compiling Burgs....");
        JsonElement burgs = tasks.get(TaskType.BURGS).execute(inputPath);
        JsonObject burgGeos = new JsonObject();

        File burgsDir = new File(outputPath + "burgs\\");
        if (!burgsDir.exists()) burgsDir.mkdirs();

        for (JsonElement object : burgs.getAsJsonArray()) {
            JsonObject burg = object.getAsJsonObject();
            JsonObject transferCache = new JsonObject();

            if (!burg.has("name")) continue;
            String legalName = burg.get("name").getAsString() + " - " + burg.get("i"); // The technical indexing method we use

            eventBus.publish("Stripping Geo Features from Burg: " + legalName);
            transferCache.addProperty("i", burg.get("i").getAsInt());

            eventBus.publish("Cleaning up Burg Cache...");
            for (String key : transferCache.keySet()) {
                if (key.equals("i")) { continue; }
                burg.remove(key);
            }

            burgGeos.add(legalName, transferCache);
            File burgFile = new File(burgsDir, legalName + ".json");
            writeJsonToFile(burg, burgFile.getAbsolutePath());
        }

        eventBus.publish("Burgs Compilation Complete!...");
        return burgGeos;
    }

    private JsonElement compileZones(String inputPath, String outputPath) {
        eventBus.publish("Compiling Zones....");
        JsonElement zones = tasks.get(TaskType.ZONES).execute(inputPath);
        JsonObject zoneGeos = new JsonObject();

        File zonesDir = new File(outputPath + "zones\\");
        if (!zonesDir.exists()) zonesDir.mkdirs();

        for (JsonElement object : zones.getAsJsonArray()) {
            JsonObject zone = object.getAsJsonObject();
            JsonObject transferCache = new JsonObject();

            if (!zone.has("name")) { continue; }
            String legalName = zone.get("name").getAsString() + " - " + zone.get("i");

            eventBus.publish("Stripping Geo Features from Zone: " + legalName);
            transferCache.addProperty("i", zone.get("i").getAsInt());

            eventBus.publish("Cleaning up Zone Cache...");
            for (String key : transferCache.keySet()) {
                if (key.equals("i")) { continue; }
                zone.remove(key);
            }

            eventBus.publish("Writing Zone to disk...");
            zoneGeos.add(legalName, transferCache);
            File zoneFile = new File(zonesDir, legalName + ".json");
            writeJsonToFile(zone, zoneFile.getAbsolutePath());
        }

        eventBus.publish("Zones Compilation Complete!...");
        return zoneGeos;
    }

    private JsonElement compileMilitary(String inputPath, String outputPath) {
        eventBus.publish("Compiling Military Units....");
        JsonElement units = tasks.get(TaskType.MILITARY).execute(inputPath);
        JsonObject unitGeos = new JsonObject();

        File unitsDir = new File(outputPath + "units\\");
        if (!unitsDir.exists()) unitsDir.mkdirs();

        for (JsonElement object : units.getAsJsonArray()) {
            JsonObject unit = object.getAsJsonObject();
            JsonObject transferCache = new JsonObject();

            if (!unit.has("name")) { continue; }
            String legalName = unit.get("name").getAsString(); // + " - " + unit.get("i");

            eventBus.publish("Stripping Geo Features from Unit: " + legalName);
            //transferCache.addProperty("i", unit.get("i").getAsInt());

            eventBus.publish("Cleaning up Unit Cache...");
            for (String key : transferCache.keySet()) {
                if (key.equals("i")) { continue; }
                unit.remove(key);
            }

            eventBus.publish("Writing Unit to disk...");
            unitGeos.add(legalName, transferCache);
            File unitFile = new File(unitsDir, legalName + ".json");
            writeJsonToFile(unit, unitFile.getAbsolutePath());
        }

        eventBus.publish("Military Compilation Complete!...");
        return unitGeos;
    }

    private JsonElement compileCultures(String inputPath, String outputPath) {
        eventBus.publish("Compiling Cultures....");
        JsonElement cultures = tasks.get(TaskType.CULTURES).execute(inputPath);
        JsonObject cultureGeos = new JsonObject();

        File culturesDir = new File(outputPath + "cultures\\");
        if (!culturesDir.exists()) culturesDir.mkdirs();

        for (JsonElement object : cultures.getAsJsonArray()) {
            JsonObject culture = object.getAsJsonObject();
            JsonObject transferCache = new JsonObject();

            if (!culture.has("name")) { continue; }
            String legalName = culture.get("name").getAsString() + " - " + culture.get("i");

            eventBus.publish("Stripping Geo Features from Culture: " + legalName);
            transferCache.addProperty("i", culture.get("i").getAsInt());

            eventBus.publish("Cleaning up Culture Cache...");
            for (String key : transferCache.keySet()) {
                if (key.equals("i")) { continue; }
                culture.remove(key);
            }

            eventBus.publish("Writing Culture to disk...");
            cultureGeos.add(legalName, transferCache);
            File cultureFile = new File(culturesDir, legalName + ".json");
            writeJsonToFile(culture, cultureFile.getAbsolutePath());
        }

        eventBus.publish("Cultures Compilation Complete!...");
        return cultureGeos;
    }

    private JsonElement compileReligions(String inputPath, String outputPath) {
        eventBus.publish("Compiling Religions....");
        JsonElement religions = tasks.get(TaskType.RELIGIONS).execute(inputPath);
        JsonObject religionGeos = new JsonObject();

        File religionsDir = new File(outputPath + "religions\\");
        if (!religionsDir.exists()) religionsDir.mkdirs();

        for (JsonElement object : religions.getAsJsonArray()) {
            JsonObject religion = object.getAsJsonObject();
            JsonObject transferCache = new JsonObject();

            if (!religion.has("name")) { continue; }
            String legalName = religion.get("name").getAsString() + " - " + religion.get("i");

            eventBus.publish("Stripping Geo Features from Religion: " + legalName);
            transferCache.addProperty("i", religion.get("i").getAsInt());

            eventBus.publish("Cleaning up Religion Cache...");
            for (String key : transferCache.keySet()) {
                if (key.equals("i")) { continue; }
                religion.remove(key);
            }

            eventBus.publish("Writing Religion to disk...");
            religionGeos.add(legalName, transferCache);
            File religionFile = new File(religionsDir, legalName + ".json");
            writeJsonToFile(religion, religionFile.getAbsolutePath());
        }

        eventBus.publish("Religions Compilation Complete!...");
        return religionGeos;
    }

    private JsonElement compileNameBases(String inputPath, String outputPath) {
        eventBus.publish("Compiling Name Bases....");
        JsonElement nameBases = tasks.get(TaskType.NAMES).execute(inputPath);

        File nameBasesDir = new File(outputPath + "name bases\\");
        if (!nameBasesDir.exists()) nameBasesDir.mkdirs();

        for (JsonElement object : nameBases.getAsJsonArray()) {
            JsonObject nameBase = object.getAsJsonObject();

            if (!nameBase.has("name")) { continue; }
            String legalName = nameBase.get("name").getAsString() + " - " + nameBase.get("i");

            eventBus.publish("Writing Name Base to disk...");
            File nameBaseFile = new File(nameBasesDir, legalName + ".json");
            writeJsonToFile(nameBase, nameBaseFile.getAbsolutePath());
        }

        eventBus.publish("Name Bases Compilation Complete!...");
        return null;
    }

    private JsonElement compileNotes(String inputPath, String outputPath) {
        eventBus.publish("Compiling Notes....");
        JsonElement notes = tasks.get(TaskType.NOTES).execute(inputPath);

        File notesDir = new File(outputPath + "notes\\");
        if (!notesDir.exists()) notesDir.mkdirs();

        for (JsonElement object : notes.getAsJsonArray()) {
            JsonObject note = object.getAsJsonObject();

            if (!note.has("name")) { continue; }
            String legalName = note.get("name").getAsString() + " - " + note.get("i");

            eventBus.publish("Writing Note to disk...");
            File noteFile = new File(notesDir, legalName + ".json");
            writeJsonToFile(note, noteFile.getAbsolutePath());
        }

        eventBus.publish("Notes Compilation Complete!...");
        return null;
    }
}
