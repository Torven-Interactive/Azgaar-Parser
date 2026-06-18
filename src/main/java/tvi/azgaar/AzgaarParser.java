package tvi.azgaar;

import com.google.gson.JsonElement;
import tvi.azgaar.parser.ui.Window;
import tvi.azgaar.parser.TaskSystem;
import tvi.azgaar.parser.models.geography.Cell;
import tvi.azgaar.parser.models.geopol.State;
import tvi.azgaar.parser.models.linguistic.Culture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class AzgaarParser {
    private Window extractor;
    private String inputPath;
    private String outputPath;
    private TaskSystem taskSystem;

    private int loadingSteps = 0;

    private List<Cell> loadedNodes = new ArrayList<>();
    private List<State> loadedStates = new ArrayList<>();
    private List<Culture> loadedCultures = new ArrayList<>();

    public AzgaarParser(String in, String out) {
        this.inputPath = in;
        this.outputPath = out + File.separator + "definitions" + File.separator;

        if (!new File(outputPath).exists()) {
            try {
                Files.createDirectory(Paths.get(outputPath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        taskSystem = new TaskSystem();
        extractor = new Window(this);

    }

    public TaskSystem getTaskSystem() {
        return taskSystem;
    }

    public void parse() {
        while(taskSystem.loadSteps <= 2) {
            File rawFile = new File(inputPath);
            if (!rawFile.exists()) {
                System.err.println("🔴 [AzgaarParser Error] Specified source map file does not exist at -> " + inputPath);
                return;
            }

            try {
                // 🔄 STEP 0: Build the Physical Geography Sandbox Layer
                if (taskSystem.loadSteps == 0) {
                    taskSystem.compileGeographyLayer(inputPath, outputPath);

                    // Forces an aggressive clear of heavy map node vectors from memory before step 1
                    System.gc();
                    taskSystem.loadSteps++;
                }

                // 🔄 STEP 1: Build the Nested Faction and Geopolitical Ownership Layer
                if (taskSystem.loadSteps == 1) {
                    taskSystem.compileGeopoliticsLayer(inputPath, outputPath);

                    System.gc();
                    taskSystem.loadSteps++;
                }

                // 🔄 STEP 2: Build the Global Heritage and Social Databases Layer
                if (taskSystem.loadSteps == 2) {
                    taskSystem.compileSocietyLayer(inputPath, outputPath);
                    JsonElement map = taskSystem.getGson().toJsonTree(taskSystem.getMapData());
                    taskSystem.writeJsonToFile(map, outputPath + "mesh.json");

                    System.gc();
                    taskSystem.loadSteps++;
                }

                System.out.println("🎉 [SUCCESS] All sequential map parsing layers successfully processed and baked!");

            } catch (Exception e) {
                System.err.println("🔴 [CRITICAL ERROR] Master parser utility encountered an unhandled execution fault!");
                e.printStackTrace();

                taskSystem.loadSteps = 0;
                return;
            }
        }

        taskSystem.loadSteps = 0;
    }

    public void loadWorldData() {
        System.out.println("🤖 [Azgaar Parser] Initiating sequential master dataset hydration pipeline...");

        // Explicitly reset our gate counter to step 0 before reading files

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

    public List<Cell> getLoadedNodes() {
        return loadedNodes;
    }

    public List<State> getLoadedStates() {
        return loadedStates;
    }

    public List<Culture> getLoadedCultures() {
        return loadedCultures;
    }

    public void setVisible(boolean visible) {
        extractor.setVisible(visible);
    }

    public void setInputPath(String path) {
        inputPath = path;
    }

    public void setOutputPath(String path) {
        outputPath = path;
    }
}