package tvi;

import tvi.azgaar.AzgaarParser;
import tvi.azgaar.parser.TaskType;

public class Main {
    public static void main(String[] args) {
        String base = "C:\\Users\\gam3r\\OneDrive\\Desktop\\Torven Interactive\\projects\\Azgaar Parser\\testing\\";
        String json ="azgaar.json";

        AzgaarParser parser = new AzgaarParser(base + json, base);
        parser.setVisible(true);

        // parser.getTaskSystem().printTask(TaskType.MARKERS, base + json);
    }
}