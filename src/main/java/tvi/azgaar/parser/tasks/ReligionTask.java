package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.Task;

import java.io.FileReader;

public class ReligionTask implements Task {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("⛪ [ReligionTask] Commencing religion extraction layer pass...");

        try {
            // Read the master file structure
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                if (!root.has("pack")) {
                    System.err.println("🔴 [ReligionTask Error] File lacks the required 'pack' branch!");
                    return new JsonArray();
                }

                JsonObject pack = root.getAsJsonObject("pack");
                if (!pack.has("religions")) {
                    System.err.println("⚠️  [ReligionTask Warning] No 'religions' array found inside 'pack'. skipping.");
                    return new JsonArray();
                }

                // Isolate the religions array node
                JsonArray religionsArray = pack.getAsJsonArray("religions");

                // Wrap it cleanly into a root religions file format
                JsonObject religionsOutput = new JsonObject();
                religionsOutput.add("religions", religionsArray);

                System.out.println("🟢 [ReligionTask Success] Extracted religions");
                return religionsOutput;
            }

        } catch (Exception e) {
            System.err.println("🔴 [ReligionTask Failure] Critical exception during execution!");
            e.printStackTrace();
        }

        return new JsonArray();
    }
}
