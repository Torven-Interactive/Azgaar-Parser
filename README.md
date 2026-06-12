# Azgaar Fantasy Map Parser API (Java) `v0.5-beta`

A lightweight, decoupled, open-source Java API designed to ingest massive procedurally generated maps from **Azgaar's Fantasy Map Generator (v1.122+)** and compile them into unbloated, structured, game-engine-ready JSON files.

Instead of forcing your engine to parse a single 1-million-line browser export, this utility executes a state-machine-controlled pipelined sequence to split, clean, and bake data into three independent functional domain layers.

---

## ❤️ Standing on the Shoulders of Giants: Tribute to Azgaar

This project is a companion API built entirely out of deep respect, gratitude, and admiration for **Azgaar (Ilya Atkin)**, the brilliant mind behind **Azgaar's Fantasy Map Generator**. What Ilya has created over years of open-source dedication is nothing short of a masterpiece—providing worldbuilders, writers, and indie game developers with arguably the most sophisticated procedural world-generation sandbox on the web.

This Java parser exists solely to help developers bridge the gap between Azgaar's stunning front-end web client and native desktop game development pipelines. If this tool helps your game project succeed, it is because of the foundational genius of Azgaar's generation engine. 

### 🌟 Support the Original Creator & Tool
If you use this parser, please take a moment to support, star, and join the official community surrounding the main generator:

*   **🌐 Run the Web Generator**: [Azgaar's Fantasy Map Generator](https://azgaar.github.io/Fantasy-Map-Generator/)
*   **💻 Explore the Core Engine Source**: [Azgaar's GitHub Repository](https://github.com/Azgaar/Fantasy-Map-Generator)
*   **💬 Join the Community**: [/r/FantasyMapGenerator on Reddit](https://www.reddit.com/r/FantasyMapGenerator/)
*   **☕ Support Ilya's Work**: [Azgaar's Patreon / Buy Me A Coffee](https://www.patreon.com/azgaar)

---

## 🚀 The 3-Layer Data Architecture

The parser flattens and groups data into three highly-focused production profiles located inside the `[Azgaar Parser]` destination root:

1. **`map.json` (The Physical World)**
   * **`biomes`**: Global climate tables and vegetation index sheets.
   * **`nodes`**: The raw vector cell mesh grid containing pre-cached, high-precision sewn boundary polygon coordinate vectors (`vx`, `vy`), heights, and relational state ownership indices.
   * **`rivers`**: Hydrological freshwater flow directions and network structures.
   * **`routes`**: Global trade pathways, maritime ocean highways, and country connections.
   * **`features`**: Landmass continent, ocean basin, and geographic boundary shape definitions.

2. **`states.json` (The Living World)**
   * **`states`**: Sovereign political faction registers, diplomatic metadata, and empire hexes.
   * **`provinces`**: Localized administrative state sub-divisions (duchies, counties).
   * **`zones`**: Custom danger bubbles, tactical perimeters, or localized risk grids.
   * **`military`**: Recruitment configurations, armed regiments, and local task force allocations.
   * **`burgs`**: Populated city matrices, urban coordinates, defensive wall flags, and population metrics.

3. **`society.json` (Linguistics & Lore)**
   * **`cultures`**: Human expanding cultural sprawl boundaries and architectural heritages.
   * **`religions`**: Global clerical matrix arrays and belief systems.
   * **`nameBases`**: Raw linguistic syllables used for infinite procedural title text generation.
   * **`notes`**: Custom timeline logs, lore, and wiki article write-ups attached to landmarks.
   * **`markers`**: Localized points of interest (ruins, holy sites, active volcanoes).

---

## ⚡ Architectural Pipeline Sequence

The pipeline utilizes a sequential gate counter (`loadingSteps`) to run specific task extractions. This acts as a memory valve—releasing heavy geometric variables via Garbage Collection hooks before initiating the next file IO layer pass:

```text
[Raw Azgaar Export] 
       │
       ▼ (Pass 0)
 📐 [Layer 1: Geography]  ──> bakes map.json ──> GC Memory Flush
       │
       ▼ (Pass 1)
 👑 [Layer 2: Geopolitics] ──> bakes states.json ──> GC Memory Flush
       │
       ▼ (Pass 2)
 🔤 [Layer 3: Society]    ──> bakes society.json ──> Complete
```

## 🛠️ Usage Example

```java
import tvi.azgaar.parser.AzgaarParser;

public class Main {
    public static void main(String[] args) {
        String inputPath = "C:/maps/my_fantasy_world.json";
        String outputPath = "C:/game_project/assets/";

        // Instantiates and auto-generates the [Azgaar Parser] output matrix
        AzgaarParser parser = new AzgaarParser(inputPath, outputPath);
        
        // Initiates the sequential 3-layer parsing loop
        parser.parse();
    }
}
```

## 📋 Road to v1.0
- [x] High-precision geometric vertex-stitching cell math (`v0.1`)
- [x] Modular Interface-driven `TaskSystem` decoupling (`v0.3`)
- [x] Stateful Multi-Layer compilation and path flattening (`v0.5`)
- [ ] Implement user-facing symmetrical incremental data-loading classes (`v1.0`)

## 📄 License
This project is open-source and available under the MIT License.
