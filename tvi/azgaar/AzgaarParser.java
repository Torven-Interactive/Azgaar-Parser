package tvi.azgaar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import tvi.azgaar.parser.TaskSystem;
import tvi.azgaar.parser.models.geography.MapNode;
import tvi.azgaar.parser.models.geopol.State;
import tvi.azgaar.parser.models.linguistic.Culture;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class AzgaarParser {
    private String inputPath;
    private String outputPath;
    private TaskSystem taskSystem;

    private int loadingSteps = 0;


    public AzgaarParser(String in, String out) {
        this.inputPath = in;
        this.outputPath = out + File.separator + "Azgaar Parser" + File.separator;

        if (!new File(outputPath).exists()) {
            try {
                Files.createDirectory(Paths.get(outputPath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        taskSystem = new TaskSystem();

    }

    public TaskSystem getTaskSystem() {
        return taskSystem;
    }

    public void parse() {
        while(loadingSteps <= 2) {
            File rawFile = new File(inputPath);
            if (!rawFile.exists()) {
                System.err.println("🔴 [AzgaarParser Error] Specified source map file does not exist at -> " + inputPath);
                return;
            }

            try {
                // 🔄 STEP 0: Build the Physical Geography Sandbox Layer
                if (this.loadingSteps == 0) {
                    taskSystem.compileGeographyLayer(inputPath, outputPath);

                    // Forces an aggressive clear of heavy map node vectors from memory before step 1
                    System.gc();
                    loadingSteps++;
                }

                // 🔄 STEP 1: Build the Nested Faction and Geopolitical Ownership Layer
                if (this.loadingSteps == 1) {
                    taskSystem.compileGeopoliticsLayer(inputPath, outputPath);

                    System.gc();
                    loadingSteps++;
                }

                // 🔄 STEP 2: Build the Global Heritage and Social Databases Layer
                if (this.loadingSteps == 2) {
                    taskSystem.compileSocietyLayer(inputPath, outputPath);

                    System.gc();
                    loadingSteps++;
                }

                System.out.println("🎉 [SUCCESS] All sequential map parsing layers successfully processed and baked!");

            } catch (Exception e) {
                System.err.println("🔴 [CRITICAL ERROR] Master parser utility encountered an unhandled execution fault!");
                e.printStackTrace();
            }
        }

        loadingSteps = 0;
    }

    public void loadWorldData() {
        System.out.println("🤖 [Azgaar Parser] Initiating sequential master dataset hydration pipeline...");

        // Explicitly reset our gate counter to step 0 before reading files

        // Local workspace collection buckets
        List<MapNode> loadedNodes = new ArrayList<>();
        List<State> loadedStates = new ArrayList<>();
        List<Culture> loadedCultures = new ArrayList<>();

        while (loadingSteps <= 2) {
            switch (loadingSteps) {

                case 0:
                    // 📥 STEP 0: Hydrate the entire map mesh grid and cell property bags
                    loadedNodes = taskSystem.loadGeographyLayer(outputPath);
                    System.gc(); // Clear any intermediate JSON stream allocations from the heap
                    loadingSteps++;
                    break;

                case 1:
                    // 📥 STEP 1: Hydrate your clean sovereign state faction lists
                    loadedStates = taskSystem.loadGeopoliticsLayer(outputPath);
                    System.gc();
                    loadingSteps++;
                    break;

                case 2:
                    // 📥 STEP 2: Hydrate your global ethnolinguistic culture models
                    loadedCultures = taskSystem.loadSocietyLayer(outputPath);
                    System.gc();
                    loadingSteps++;
                    break;

                default:
                    System.err.println("🔴 [Azgaar Parser Loader Warning] Unknown loading step checkpoint state reached!");
                    break;
            }
        }

        System.out.println("🎉 [SUCCESS] All engine-ready map and faction profiles fully loaded!");
        System.out.println("📊 Final Registry Stats: [Cells: " + loadedNodes.size() + "] [States: " + loadedStates.size() + "] [Cultures: " + loadedCultures.size() + "]");

        loadingSteps = 0;
        // From here, your engine can map these datasets straight into your active game loops!
    }

    public List<MapNode> loadOrganicAzgaarMap(String gameDataPath) {
        List<MapNode> masterVisualNodes = new ArrayList<>();
        String filePath = gameDataPath + "data" + File.separator + "definitions" + File.separator + "world_map_data.json";
        File file = new File(filePath);

        if (!file.exists()) {
            // EngineLogger.log("[Azgaar Parser] Error: world_map_data.json missing at: " + filePath);
            return masterVisualNodes;
        }

        try (FileReader reader = new FileReader(file)) {
            com.google.gson.JsonObject root = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();

            JsonArray geoCells = root.getAsJsonArray("geography_cells");
            if (geoCells == null) {
                // EngineLogger.log("[Azgaar Parser] Error: 'geography_cells' array key was missing or null in baked database!");
                return masterVisualNodes;
            }

            // EngineLogger.log("[Azgaar Parser] Map Layer Found " + geoCells.size() + " cell elements. Filtering ocean geometry meshes...");

            for (JsonElement cellElem : geoCells) {
                JsonObject cell = cellElem.getAsJsonObject();

                // 🌟 THE MESH CULLER FIX:
                // If the cell's height value indicates it is an ocean/water tile,
                // skip it completely! This prevents the empty ocean mesh from blobbing up the screen.
                if (cell.has("height") && cell.get("height").getAsInt() < 20) {
                    continue;
                }

                int cellId = cell.has("id") ? cell.get("id").getAsInt() : -1;

                int centerX = cell.has("x") ? (int) Math.round(cell.get("x").getAsDouble() * 15) : 0;
                int centerY = cell.has("y") ? (int) Math.round(cell.get("y").getAsDouble() * 15) : 0;

                String targetHexColor = "#4E8752";
                String polityName = "Terra Incognita";

                Polygon organicPolygon = new Polygon();
                JsonArray pointsArray = cell.getAsJsonArray("polygon_points");

                if (pointsArray != null && pointsArray.size() > 0) {
                    for (JsonElement ptElem : pointsArray) {
                        JsonObject pt = ptElem.getAsJsonObject();

                        double rawX = pt.has("vx") ? pt.get("vx").getAsDouble() : 0.0;
                        double rawY = pt.has("vy") ? pt.get("vy").getAsDouble() : 0.0;

                        int scaledVX = (int) Math.round(rawX * 15);
                        int scaledVY = (int) Math.round(rawY * 15);
                        organicPolygon.addPoint(scaledVX, scaledVY);
                    }
                }

                // Instantiates the concrete engine class directly!
                MapNode cellTile = new MapNode(
                        "CELL_" + cellId, centerX, centerY, organicPolygon, targetHexColor
                );
                cellTile.setLabelName(polityName);
                cellTile.setProperty("geometry_polygon", organicPolygon);
                cellTile.setProperty("cell_id", cellId); // Keep this for your click ID reporting!


                masterVisualNodes.add(cellTile);
            }
            // EngineLogger.log("[Azgaar Parser] Success! Loaded " + masterVisualNodes.size() + " landmass cell polygons.");

        } catch (Exception e) {
            // EngineLogger.log("[Azgaar Parser] Error: Failed to expand map pack vectors.");
            e.printStackTrace();
        }
        return masterVisualNodes;
    }
}