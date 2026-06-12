package tvi.azgaar.parser.tasks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tvi.azgaar.parser.Task;
import java.io.FileReader;

public class MeshTask implements Task {

    @Override
    public JsonElement execute(String inputPath) {
        System.out.println("📐 [MeshTask] Commencing high-precision geometric cell & vertex stitching pass...");

        try (FileReader reader = new FileReader(inputPath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            if (!root.has("pack")) {
                System.err.println("🔴 [MeshTask Error] Export file is missing the core root 'pack' object node branch!");
                return new JsonArray();
            }
            JsonObject pack = root.getAsJsonObject("pack");

            if (!pack.has("cells") || !pack.has("vertices")) {
                System.err.println("🔴 [MeshTask Error] Source file lacks the required cells or vertices matrix blocks!");
                return new JsonArray();
            }

            JsonArray cells = pack.getAsJsonArray("cells");
            JsonArray verticesArray = pack.getAsJsonArray("vertices");

            // 1. PRE-CACHE THE HIGH-PRECISION VERTICES FOR BLAZING FAST LOOKUPS
            int maxVertexId = 0;
            for (JsonElement vElem : verticesArray) {
                JsonObject vObj = vElem.getAsJsonObject();
                if (vObj.has("i")) {
                    maxVertexId = Math.max(maxVertexId, vObj.get("i").getAsInt());
                }
            }

            // Restored: Your exact 2D matrix allocation track [id][0=x, 1=y]
            double[][] vertexLookup = new double[maxVertexId + 1][2];
            for (JsonElement vElem : verticesArray) {
                JsonObject vObj = vElem.getAsJsonObject();
                int id = vObj.get("i").getAsInt();
                JsonArray pArr = vObj.getAsJsonArray("p");
                if (pArr != null && pArr.size() >= 2) {
                    vertexLookup[id][0] = pArr.get(0).getAsDouble(); // X Coordinate Vector
                    vertexLookup[id][1] = pArr.get(1).getAsDouble(); // Y Coordinate Vector
                }
            }

            // 2. STITCH COORDINATES DIRECTLY INTO YOUR ENGINE CELL MATRIX
            JsonArray compiledCells = new JsonArray();
            for (JsonElement cellElem : cells) {
                JsonObject cell = cellElem.getAsJsonObject();
                JsonObject bakedCell = new JsonObject();

                // Map clean primitive values matching your design parameters
                bakedCell.addProperty("id", cell.has("i") ? cell.get("i").getAsInt() : -1);

                JsonArray pArr = cell.getAsJsonArray("p");
                bakedCell.addProperty("x", (pArr != null && pArr.size() >= 1) ? pArr.get(0).getAsDouble() : 0.0);
                bakedCell.addProperty("y", (pArr != null && pArr.size() >= 2) ? pArr.get(1).getAsDouble() : 0.0);

                bakedCell.addProperty("height", cell.has("h") ? cell.get("h").getAsInt() : 0);

                // Handling alternate biome naming key variants cleanly
                if (cell.has("biome")) {
                    bakedCell.addProperty("biome", cell.get("biome").getAsInt());
                } else if (cell.has("b")) {
                    bakedCell.addProperty("biome", cell.get("b").getAsInt());
                } else {
                    bakedCell.addProperty("biome", 0);
                }

                bakedCell.addProperty("state", cell.has("state") ? cell.get("state").getAsInt() : 0);

                // STITCH THE GEOMETRY LINE BOUNDARY VECTORS
                JsonArray polygonPoints = new JsonArray();
                JsonArray vertexIndices = cell.getAsJsonArray("v");
                if (vertexIndices != null) {
                    for (JsonElement indexElem : vertexIndices) {
                        int vertexIndex = indexElem.getAsInt();
                        if (vertexIndex >= 0 && vertexIndex <= maxVertexId) {
                            // Restored: Your exact 2D coordinates extraction tracks
                            double realX = vertexLookup[vertexIndex][0];
                            double realY = vertexLookup[vertexIndex][1];

                            JsonObject pointObj = new JsonObject();
                            pointObj.addProperty("vx", realX);
                            pointObj.addProperty("vy", realY);
                            polygonPoints.add(pointObj);
                        }
                    }
                }

                bakedCell.add("polygon_points", polygonPoints);
                compiledCells.add(bakedCell);
            }

            System.out.println("🟢 [MeshTask Success] Finalized high-precision geography grid stitching pass.");
            return compiledCells;

        } catch (Exception e) {
            System.err.println("🔴 [MeshTask Failure] Encountered an exception compiling vertex meshes!");
            e.printStackTrace();
        }

        return new JsonArray();
    }
}


