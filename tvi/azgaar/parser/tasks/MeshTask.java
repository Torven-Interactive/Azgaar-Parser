package tvi.azgaar.parser.tasks;

import com.google.gson.*;
import tvi.azgaar.parser.ExtendedTask;
import tvi.azgaar.parser.models.geography.MapNode;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MeshTask extends ExtendedTask {

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

    @Override
    public Object load(String inputPath) {
        System.out.println("📐 [MeshTask Master Loader] Reconstructing map nodes and properties...");
        List<MapNode> fullyHydratedNodes = new ArrayList<>();

        // Correct parent container directory pathing context
        File currentFile = new File(inputPath);
        String parentDir = currentFile.getParent();
        if (parentDir == null) {
            parentDir = ".";
        }
        parentDir += File.separator;

        JsonArray routes = new JsonArray();
        JsonArray features = new JsonArray();
        JsonArray zones = new JsonArray();
        JsonArray notes = new JsonArray();
        JsonArray markers = new JsonArray();

        // 1. PRE-LOAD ALL SIBLING LAYER DATA ARRAYS STRAIGHT FROM THE FLAT FILES
        try (FileReader r = new FileReader(inputPath)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            if (root.has("routes")) routes = root.getAsJsonArray("routes");
            if (root.has("features")) features = root.getAsJsonArray("features");
        } catch (Exception e) {
            System.err.println("⚠️ Could not pre-read map.json headers");
        }

        File statesFile = new File(parentDir + "states.json");
        if (statesFile.exists()) {
            try (FileReader r = new FileReader(statesFile)) {
                JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
                if (root.has("zones")) zones = root.getAsJsonArray("zones");
            } catch (Exception e) {
                System.err.println("⚠️ Could not pre-read states.json headers");
            }
        }

        File societyFile = new File(parentDir + "society.json");
        if (societyFile.exists()) {
            try (FileReader r = new FileReader(societyFile)) {
                JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
                if (root.has("notes")) notes = root.getAsJsonArray("notes");

                // FIX: Checking and extracting from your correct flat root key string
                if (root.has("markers")) markers = root.getAsJsonArray("markers");
            } catch (Exception e) {
                System.err.println("⚠️ Could not pre-read society.json headers");
            }
        }

        // 2. RECONSTRUCT THE CELLS AND INJECT EVERY ATTRIBUTES ARRAY INTO THE MAPNODE BAG
        try (FileReader reader = new FileReader(inputPath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (!root.has("nodes")) return fullyHydratedNodes;
            JsonArray nodesArray = root.getAsJsonArray("nodes");

            for (JsonElement cellElem : nodesArray) {
                JsonObject cell = cellElem.getAsJsonObject();

                int cellId = cell.has("id") ? cell.get("id").getAsInt() : -1;
                int centerX = cell.has("x") ? (int) Math.round(cell.get("x").getAsDouble() * 15) : 0;
                int centerY = cell.has("y") ? (int) Math.round(cell.get("y").getAsDouble() * 15) : 0;

                java.awt.Polygon polygon = new java.awt.Polygon();
                if (cell.has("polygon_points")) {
                    for (JsonElement ptElem : cell.getAsJsonArray("polygon_points")) {
                        JsonObject pt = ptElem.getAsJsonObject();
                        int scaledX = (int) Math.round(pt.get("vx").getAsDouble() * 15);
                        int scaledY = (int) Math.round(pt.get("vy").getAsDouble() * 15);
                        polygon.addPoint(scaledX, scaledY);
                    }
                }

                // Instantiate your actual core engine class tile
                MapNode cellTile = new MapNode("CELL_" + cellId, centerX, centerY, polygon, "#4E8752");
                cellTile.setLabelName("Terra Incognita");

                // Assign raw cell properties
                cellTile.setProperty("cell_id", cellId);
                cellTile.setProperty("height", cell.has("height") ? cell.get("height").getAsInt() : 0);
                cellTile.setProperty("biome", cell.has("biome") ? cell.get("biome").getAsInt() : 0);
                cellTile.setProperty("state", cell.has("state") ? cell.get("state").getAsInt() : 0);
                cellTile.setProperty("geometry_polygon", polygon);

                // The true clean string keys for property bags
                cellTile.setProperty("routes", routes);
                cellTile.setProperty("features", features);
                cellTile.setProperty("zones", zones);
                cellTile.setProperty("notes", notes);
                cellTile.setProperty("markers", markers);

                fullyHydratedNodes.add(cellTile);
            }
            System.out.println("🟢 [MeshTask Loader Success] Successfully compiled cell property bags.");
        } catch (Exception e) {
            System.err.println("🔴 [MeshTask Master Loader Error] Failed to reconstruct map mesh layers.");
            e.printStackTrace();
        }
        return fullyHydratedNodes;
    }
}


