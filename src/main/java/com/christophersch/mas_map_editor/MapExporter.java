package com.christophersch.mas_map_editor;

import java.io.FileWriter;
import java.util.ArrayList;

public class MapExporter {
    public static void exportToTextFile(MapEditor app, String mapName) {
        ArrayList<MapObject> objects = app.getMapObjects();

        int width = app.getMapWidth();
        int height = app.getMapHeight();

        try {
            FileWriter writer = new FileWriter("output/" + (mapName.isEmpty() ? "map.txt" : mapName + ".txt"));

            writer.write("width = " + width + System.lineSeparator());
            writer.write("height = " + height + System.lineSeparator());
            writer.write("numGuards = " + app.gui.agentCount.getValue() + System.lineSeparator());

            // Write actual map objects to file
            for (MapObject o : objects) {
                writer.write(o + System.lineSeparator());
            }
            writer.close();
        } catch(Exception e) {
            System.out.println("Write error");
        }
    }
}
