package tvi;

import tvi.azgaar.AzgaarParser;

public class Main {
    public static void main(String[] args) {
        String testing = "C:\\Users\\gam3r\\OneDrive\\Desktop\\Torven Interactive\\projects\\Azgaar Parser\\testing\\";
        String json ="azgaar.json";

        AzgaarParser parser = new AzgaarParser(testing + json, testing);
        parser.parse(true);
        parser.loadWorldData();

        parser.parse(false);
        parser.loadWorldData();
    }
}