package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.Task;

import java.io.FileReader;

public class CultureTask implements Task {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🎭 [CultureTask] Commencing culture extraction layer pass...");

        try {
            // Read the master file structure
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                if (!root.has("pack")) {
                    System.err.println("🔴 [CultureTask Error] File lacks the required 'pack' branch!");
                    return new JsonArray();
                }

                JsonObject pack = root.getAsJsonObject("pack");
                if (!pack.has("cultures")) {
                    System.err.println("⚠️  [CultureTask Warning] No 'cultures' array found inside 'pack'. skipping.");
                    return new JsonArray();
                }

                // Isolate the cultures array node
                JsonArray culturesArray = pack.getAsJsonArray("cultures");

                // Wrap it cleanly into a root cultures file format
                JsonObject culturesOutput = new JsonObject();
                culturesOutput.add("cultures", culturesArray);

                System.out.println("🟢 [CultureTask Success] Extracted cultures");

                return culturesOutput;
            }

        } catch (Exception e) {
            System.err.println("🔴 [CultureTask Failure] Critical exception during execution!");
            e.printStackTrace();
        }

        return new JsonArray();
    }
}
