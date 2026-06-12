package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.Task;

import java.io.FileReader;

public class NameBasesTask implements Task {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🔤 [NameBasesTask] Commencing root-level linguistic name base extraction pass...");
        try {
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                if (!root.has("nameBases")) {
                    System.err.println("⚠️ [NameBasesTask Warning] No 'nameBases' array found at the root level.");
                    return new JsonArray();
                }
                JsonArray nameBasesArray = root.getAsJsonArray("nameBases");

                JsonObject nameBasesOutput = new JsonObject();
                nameBasesOutput.add("nameBases", nameBasesArray);

                System.out.println("🟢 [NameBasesTask Success] Extracted root linguistic data profiles");

                return nameBasesOutput;
            }
        } catch (Exception e) {
            System.err.println("🔴 [NameBasesTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }

        return new JsonArray();
    }
}

