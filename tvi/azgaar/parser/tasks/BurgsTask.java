package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.Task;

import java.io.FileReader;

public class BurgsTask implements Task {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🏙️ [BurgsTask] Commencing settlement and urban center extraction pass...");

        try {
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                if (!root.has("pack")) {
                    System.err.println("🔴 [BurgsTask Error] Source file lacks the core 'pack' object node branch!");
                    return new JsonArray();
                }
                JsonObject pack = root.getAsJsonObject("pack");

                if (!pack.has("burgs")) {
                    System.err.println("⚠️ [BurgsTask Warning] No 'burgs' data array found inside 'pack'. Skipping.");
                    return new JsonArray();
                }
                JsonArray burgsArray = pack.getAsJsonArray("burgs");

                JsonObject burgsOutput = new JsonObject();
                burgsOutput.add("burgs", burgsArray);

                System.out.println("🟢 [BurgsTask Success] Extracted settlement registry array");

                return burgsOutput;
            }
        } catch (Exception e) {
            System.err.println("🔴 [BurgsTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }

        return new JsonArray();
    }
}

