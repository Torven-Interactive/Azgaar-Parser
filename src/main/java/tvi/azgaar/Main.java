package tvi.azgaar;

import tvi.azgaar.parser.AzgaarParser;
import tvi.azgaar.parser.TaskSystem;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        String testing = "C:\\Users\\gam3r\\OneDrive\\Desktop\\Torven Interactive\\projects\\Azgaar Parser\\testing\\";
        String input = testing + "azgaar.json";

        AzgaarParser parser = new AzgaarParser(input, testing);
        parser.parse();

    }
}