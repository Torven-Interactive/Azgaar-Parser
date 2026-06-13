package tvi.azgaar.parser.models.geography;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MapNode {
    private final String id;
    private final int x;
    private final int y;
    private final Polygon geometryPolygon;

    // Configurable visual parameters driven entirely by variables
    private Color renderColor;
    private String labelName;
    private boolean isVisible = true;

    // 🌟 THE COMPOSITION KEY: A generic property bag to hold game-specific metadata
    private final Map<String, Object> customProperties = new HashMap<>();

    public MapNode(String id, int x, int y, Polygon geometryPolygon, String initialColorHex) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.geometryPolygon = geometryPolygon;
        this.renderColor = Color.decode(initialColorHex);
        this.labelName = id; // Default to ID if no specific name is passed
    }

    // Standardized vector drawing loop driven purely by internal variables
    public void render(Graphics2D g2d, boolean isSelected) {
        if (!isVisible) return;

        if (geometryPolygon != null) {
            g2d.setColor(renderColor);
            g2d.fillPolygon(geometryPolygon);

            g2d.setColor(isSelected ? Color.YELLOW : new Color(30, 30, 30, 40));
            g2d.setStroke(new BasicStroke(isSelected ? 2.5f : 0.5f));
            g2d.drawPolygon(geometryPolygon);
        }
    }

    // Getters and Setters to mutate properties dynamically from the game loop
    public void setRenderColor(Color color) { this.renderColor = color; }
    public void setLabelName(String labelName) { this.labelName = labelName; }

    public void setProperty(String key, Object value) { customProperties.put(key, value); }
    public Object getProperty(String key) { return customProperties.get(key); }

    public String getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Polygon getGeometryPolygon() { return geometryPolygon; }
    public Color getRenderColor() { return renderColor; }
    public String getLabelName() { return labelName; }
    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { isVisible = visible; }
}
