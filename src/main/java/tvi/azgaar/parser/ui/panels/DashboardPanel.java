package tvi.azgaar.parser.ui.panels;

import tvi.azgaar.parser.EventBus;
import tvi.azgaar.parser.TaskSystem;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

/**
 * 📊 UNIFIED DASHBOARD PANEL COMPONENT
 * Stacks your progress indicator bar directly on top of the dark rolling text terminal.
 * Groups both indicators into a single, low-contrast container panel.
 */
public class DashboardPanel extends JPanel {
    private final TaskSystem taskSystem;
    private static final Color BG_MIDNIGHT  = new Color(18, 18, 18);
    private static final Color BG_PANEL     = new Color(24, 25, 26);
    private static final Color TEXT_SOFT    = new Color(220, 220, 220);
    private static final Color COLOR_ACCENT = new Color(40, 167, 69); // Low-intensity Strategy Green
    private static final Font MONO_FONT     = new Font("Consolas", Font.PLAIN, 12);

    private JProgressBar progressBar;
    private JTextArea consoleArea;

    public DashboardPanel(TaskSystem taskSystem) {
        this.taskSystem = taskSystem;

        setBackground(BG_PANEL);
        initializePanelLayout();
        initComponents();
        assembleView();
    }

    /**
     * 📐 Establishes the clean vertical stacking rules
     */
    private void initializePanelLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BG_MIDNIGHT, 1),
                "Extraction Dashboard Feed", 0, 0, MONO_FONT, TEXT_SOFT
        ));
    }

    /**
     * 📐 Instantiates the tracking bar and console canvas
     */
    private void initComponents() {
        // Upper Component Element: Horizontal Progress Ticker
        progressBar = new JProgressBar(0, 13); // 13 core extraction tracking milestones
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("Standing By...");
        progressBar.setBackground(BG_MIDNIGHT);
        progressBar.setForeground(COLOR_ACCENT);
        progressBar.setBorder(BorderFactory.createLineBorder(BG_PANEL, 2));

        // Lower Component Element: Terminal Text Board
        consoleArea = new JTextArea();
        consoleArea.setBackground(BG_MIDNIGHT);
        consoleArea.setForeground(TEXT_SOFT);
        consoleArea.setFont(MONO_FONT);
        consoleArea.setEditable(false);
        consoleArea.setLineWrap(true);
        consoleArea.setMargin(new Insets(8, 8, 8, 8));

        // Lock the viewport caret to snap to latest logs automatically
        DefaultCaret caret = (DefaultCaret) consoleArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    /**
     * 📐 Stacks the indicators tightly together inside the box layout container
     */
    private void assembleView() {
        add(progressBar);
        add(Box.createVerticalStrut(6)); // Flat structural gap spacing line

        JScrollPane scrollConsole = new JScrollPane(consoleArea);
        scrollConsole.setBorder(BorderFactory.createLineBorder(BG_MIDNIGHT, 1));
        add(scrollConsole);
    }

    // --- EXTERNAL CONTROLLER UPDATE HOOKS ---

    public void appendConsoleLog(String text) {
        consoleArea.append(text + "\n");
    }

    public void clearConsole() {
        consoleArea.setText("");
    }

    public void updateProgress(int step) {
        progressBar.setValue(step);
        progressBar.setString("Processing File Channel: " + step + " / 13");
    }

    public void finalizeProgressState() {
        progressBar.setString("🟢 Compilation 100% Baked Successfully!");
        appendConsoleLog("🎉 [SUCCESS] Open-source JSON package generated and dumped safely to directory.");
    }

    /**
     * 📝 Dictionary map listing out your 13 neutral file output targets
     */
    public String getStepProgressString(int step) {
        switch(step) {
            case 1:  return "📐 [MeshTask] Extracting raw geographic Voronoi cell data -> cells.json";
            case 2:  return "📐 [VertexTask] Sifting structural polygon outline nodes -> vertices.json";
            case 3:  return "🌿 [BiomeTask] Parsing localized terrain/climate climate grids -> biomes.json";
            case 4:  return "👑 [StateTask] Segregating political faction blocks -> states.json";
            case 5:  return "🎨 [ProvinceTask] Slicing regional cell cluster groupings -> provinces.json";
            case 6:  return "⚓ [BurgTask] Harvesting coordinate locations and point flags -> burgs.json";
            case 7:  return "🔤 [CultureTask] Processing master societal tradition sets -> cultures.json";
            case 8:  return "⛪ [ReligionTask] Map tracking baseline spiritual vectors -> religions.json";
            case 9:  return "📝 [NamebaseTask] Caching linguistic procedural generation syllables -> namebases.json";
            case 10: return "💧 [RiverTask] Isolating freshwater grid flow networks -> rivers.json";
            case 11: return "⚔️ [MilitaryTask] Compiling starting tactical regiment point tables -> military.json";
            case 12: return "🛣️ [RouteTask] Merging land highways and transoceanic shipping routes -> routes.json";
            case 13: return "🗺️ [ZoneTask] Finalizing map configuration layers -> zones.json";
            default: return "⚙️ [Parser Engine] Indexing extraction channel step " + step + "...";
        }
    }
}
