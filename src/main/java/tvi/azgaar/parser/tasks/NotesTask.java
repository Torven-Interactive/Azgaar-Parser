package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.Task;

import java.io.FileReader;

public class NotesTask implements Task {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("📝 [NotesTask] Commencing root-level lore and wiki notes extraction pass...");
        try {
            try (FileReader reader = new FileReader(inputPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                if (!root.has("notes")) {
                    System.err.println("⚠️ [NotesTask Warning] No 'notes' array found at the root level.");
                    return new JsonArray();
                }
                JsonArray notesArray = root.getAsJsonArray("notes");

                JsonObject notesOutput = new JsonObject();
                notesOutput.add("notes", notesArray);

                System.out.println("🟢 [NotesTask Success] Extracted root-level history and lore notes");

                return notesOutput;
            }
        } catch (Exception e) {
            System.err.println("🔴 [NotesTask Failure] Encountered an unhandled exception during extraction loop!");
            e.printStackTrace();
        }

        return new JsonArray();
    }
}

