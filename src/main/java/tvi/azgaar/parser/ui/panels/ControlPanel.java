package tvi.azgaar.parser.ui.panels;

import tvi.azgaar.parser.TaskSystem;

import javax.swing.*;
import java.awt.*;

/**
 * ⚙️ PATHWAY CONTROL PANEL COMPONENT
 * Manages directory string entries, source file navigation browsing,
 * and isolates path extraction field configurations.
 */
public class ControlPanel extends JPanel {

    private static final Color BG_MIDNIGHT  = new Color(18, 18, 18);
    private static final Color BG_PANEL     = new Color(24, 25, 26);
    private static final Color TEXT_SOFT    = new Color(220, 220, 220);
    private static final Color COLOR_ACCENT = new Color(40, 167, 69); // Strategy Console Green
    private static final Font MONO_FONT     = new Font("Consolas", Font.PLAIN, 12);

    private JTextField txtInputPath;
    private JTextField txtOutputPath;
    private JButton btnCompile;

    public ControlPanel(TaskSystem taskSystem) {
        setBackground(BG_PANEL);
        initializeComponents();
        assembleLayout();
    }

    /**
     * 📐 Declares elements and applies dark-mode styles
     */
    private void initializeComponents() {
        txtInputPath = createStyledTextField();
        txtOutputPath = createStyledTextField();

        btnCompile = new JButton("Compile Map");
        btnCompile.setBackground(COLOR_ACCENT);
        btnCompile.setForeground(Color.WHITE);
        btnCompile.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCompile.setFocusPainted(false);
        btnCompile.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }

    /**
     * 📐 Organizes the path inputs and browse buttons on a strict horizontal grid
     */
    private void assembleLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        // Row 1: Source Map Entry
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        add(createStyledLabel("Source .map File:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        add(txtInputPath, gbc);
        gbc.gridx = 2; gbc.weightx = 0.0;
        add(createBrowseButton(txtInputPath, false), gbc);

        // Row 2: Target Workspace Entry
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        add(createStyledLabel("Output Directory:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        add(txtOutputPath, gbc);
        gbc.gridx = 2; gbc.weightx = 0.0;
        add(createBrowseButton(txtOutputPath, true), gbc);

        // Row 3: Centered Launch Button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(12, 4, 4, 4);
        add(btnCompile, gbc);
    }

    // --- GETTERS FOR WINDOW ACCESS ---

    public String getInputPath() {
        return txtInputPath.getText().trim();
    }

    public String getOutputPath() {
        return txtOutputPath.getText().trim();
    }

    public JButton getCompileButton() {
        return btnCompile;
    }

    // --- COMPONENT FACTORY HELPERS ---

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setBackground(BG_MIDNIGHT);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(BG_MIDNIGHT, 5));
        field.setFont(MONO_FONT);
        return field;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_SOFT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return label;
    }

    private JButton createBrowseButton(JTextField targetField, boolean isFolderOnly) {
        JButton btn = new JButton("...");
        btn.setBackground(BG_MIDNIGHT);
        btn.setForeground(TEXT_SOFT);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(isFolderOnly ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                targetField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        return btn;
    }
}

