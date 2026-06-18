package tvi.azgaar.parser.ui;

import tvi.azgaar.AzgaarParser;
import tvi.azgaar.parser.EventBus;
import tvi.azgaar.parser.ui.panels.*;
import tvi.azgaar.parser.TaskSystem;

import javax.swing.*;
import java.awt.*;


/**
 * 🏛️ MASTER GRAPHICAL CORE FRAME
 * Coordinates layout placement, frame size boundaries, and acts as the
 * top-level orchestration window shell over your sub-panels.
 */
public class Window extends JFrame {
    private final AzgaarParser instance;
    private final TaskSystem taskSystem;
    private static final Color BG_PANEL = new Color(24, 25, 26);

    private ControlPanel controlPanel;
    private DashboardPanel dashboardPanel;

    public Window(AzgaarParser parser) {
        instance = parser;
        taskSystem = instance.getTaskSystem();

        taskSystem.getEventBus().subscribe(message ->
                SwingUtilities.invokeLater(() -> dashboardPanel.appendConsoleLog(message))
        );

        initializeFrame();
        assembleSubPanels();
    }

    /**
     * 📐 Configures structural window frame rules
     */
    private void initializeFrame() {
        setTitle("AzgaarParser v1.1.3 - Data Refinery Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(650, 480));
        setLocationRelativeTo(null); // Clear placement padding, center on screen
        getContentPane().setBackground(BG_PANEL);
    }

    /**
     * 📐 Declares and mounts your specialized panel views
     */
    private void assembleSubPanels() {
        setLayout(new BorderLayout(12, 12));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // Instantiate the individual panel segments
        controlPanel = new ControlPanel(taskSystem);
        dashboardPanel = new DashboardPanel(taskSystem);

        // Bind the compile button trigger inside ControlPanel straight to the execution loops
        controlPanel.getCompileButton().addActionListener(e -> triggerCompilationPass());

        // Lock panels into layout perimeter anchors
        add(controlPanel, BorderLayout.NORTH);
        add(dashboardPanel, BorderLayout.CENTER);
    }

    /**
     * 🗲 Launches the safe background worker thread pass
     */
    private void triggerCompilationPass() {
        String input = controlPanel.getInputPath();
        String output = controlPanel.getOutputPath() + "\\definitions\\";

        if (input.isEmpty() || output.isEmpty()) {
            dashboardPanel.appendConsoleLog("🔴 [UI Error] Input map file and output directory paths cannot be blank!");
            return;
        }

        controlPanel.getCompileButton().setEnabled(false);
        dashboardPanel.clearConsole();

        // Fire background streaming worker
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("📐 [TaskSystem] Initializing open-source data refinery engine pass...");

                instance.setInputPath(input);
                instance.setOutputPath(output);
                instance.parse();

                return null;
            }

            @Override
            protected void process(java.util.List<String> logs) {
                for (String logLine : logs) {
                    dashboardPanel.appendConsoleLog(logLine);
                }
                dashboardPanel.updateProgress(getProgress());
            }

            @Override
            protected void done() {
                controlPanel.getCompileButton().setEnabled(true);
                dashboardPanel.finalizeProgressState();
            }
        };

        worker.execute();
    }
}

