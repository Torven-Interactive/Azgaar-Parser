package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.Task;

import java.io.FileReader;


public class RiversTask implements Task {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("🌊 [RiversTask] Commencing hydrological river system extraction pass...");

        try {
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                if (!root.has("pack")) {
                    System.err.println("🔴 [RiversTask Error] Source file lacks the core 'pack' object node branch!");
                    return new JsonArray();
                }
                JsonObject pack = root.getAsJsonObject("pack");

                if (!pack.has("rivers")) {
                    System.err.println("⚠️ [RiversTask Warning] No 'rivers' data array found inside 'pack'. Skipping.");
                    return new JsonArray();
                }
                JsonArray riversArray = pack.getAsJsonArray("rivers");

                JsonObject riversOutput = new JsonObject();
                riversOutput.add("rivers", riversArray);

                System.out.println("🟢 [RiversTask Success] Extracted river flow networks");

                return riversOutput;
            }
        } catch (Exception e) {
            System.err.println("🔴 [RiversTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }

        return new JsonArray();
    }
}

