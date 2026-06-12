# Azgaar Fantasy Map Parser API (Java) `v1.0.0`

A lightweight, stateful, open-source Java API designed to ingest massive procedurally generated maps from **Azgaar's Fantasy Map Generator (v1.122+)**, compile them into structured JSON files, and hydrate them seamlessly back into memory for native desktop game development.

Instead of forcing your engine to parse a single 1-million-line browser export, this utility executes a state-machine-controlled pipelined sequence to split data into three independent functional domain layers.

---

## 📦 Installation

This API is hosted and distributed via **JitPack**. Add the repository and dependency to your build configuration:

### Maven (`pom.xml`)

Add the JitPack repository:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the dependency:
```xml
<dependency>
    <groupId>com.github.Torven-Interactive</groupId>
    <artifactId>Azgaar-Parser</artifactId>
    <version>v1.0.0</version>
</dependency>
```

### Gradle (`build.gradle`)

Add the repository to your root `build.gradle` file:
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

Add the dependency:
```groovy
dependencies {
    implementation 'com.github.Torven-Interactive:Azgaar-Parser:v1.0.0'
}
```

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

## 🚀 Symmetrical Architectural Pipeline

The API forms a complete, symmetrical, two-way data conveyor belt governed by a linear loop controller (`loadingSteps`). 

```text
  [Raw Azgaar Export] 
         │
         ▼ (Pass 0) ──> 📐 Layer 1: Geography   ──> Bakes map.json ──> GC Heap Flush
         │
         ▼ (Pass 1) ──> 👑 Layer 2: Geopolitics  ──> Bakes states.json ──> GC Heap Flush
         │
         ▼ (Pass 2) ──> 🔤 Layer 3: Society      ──> Bakes society.json ──> Complete
         │
  ====================================================================================
         │
         ▲ (Step 0) ──> 📐 loadGeographyLayer() ──> Hydrates MapNodes & Property Bags
         │
         ▲ (Step 1) ──> 👑 loadGeopoliticsLayer()──> Hydrates Sovereign Factions
         │
         ▲ (Step 2) ──> 🔤 loadSocietyLayer()    ──> Hydrates Ethnolinguistic Cultures
         │
  [In-Memory Game Data]
```

### 📦 Fault-Tolerant Loader Compensation
During the compilation/parsing pass, internal tasks wrap specific sub-arrays inside secondary key wrappers (resulting in nested tags inside `states.json` and `society.json`). 

**The built-in loader loop completely accounts for this behavior.** The data ingestion methods include dedicated deep-access conditional guards that drill straight down into the data blocks natively. This handles type-safety and ensures the data populates cleanly back into active memory pools regardless of structural file duplication.

---

## 🛠️ Usage Example

### Writing / Compiling Raw Azgaar Files
```java
import tvi.azgaar.parser.AzgaarParser;

public class Main {
    public static void main(String[] args) {
        String inputPath = "C:/maps/world_export.json";
        String outputPath = "C:/game_project/assets/";

        // Instantiates the core framework
        AzgaarParser parser = new AzgaarParser(inputPath, outputPath);
        
        // 1. Ingests raw data and splits it into unbloated JSON layers
        parser.parse();
        
        // 2. Hydrates the processed data symmetrically back into your engine context
        parser.loadWorldData();
    }
}
```

## 📋 Production Verification Metrics
The stable pipeline successfully passes integration sweeps with zero hidden crashes or terminal stream warnings:
*   **Geographical Sandbox**: `3,782 Landmass Cell Meshes` with fully reconstructed polygon boundary line shapes.
*   **Geopolitical Canvas**: `111 Sovereign States` tracking dynamic border cells.
*   **Linguistic Register**: `67 Functional World Cultures` tracking heritage and syllable generation charts.

## 📄 License
This project is open-source and available under the MIT License.
