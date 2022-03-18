package com.christophersch.mas_map_editor;

import com.christophersch.mas_map_editor.Enums.EditorMode;
import com.christophersch.mas_map_editor.Enums.ObjectType;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MapEditor extends Application {
    public final static int INITIAL_WINDOW_WIDTH = 640 * 2;
    public final static int INITIAL_WINDOW_HEIGHT = 360 * 2;

    int mapWidth = 120;
    int mapHeight = 80;

    GUI gui;

    EditorMode mode = EditorMode.EDIT;

    // Only allow 1 spawn area
    boolean placedGuardSpawn = false;
    boolean placedIntruderSpawn = false;

    static int teleportCount = 0;

    ArrayList<MapObject> mapObjects = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        gui = new GUI(stage, this);
    }

    public void addArea(ObjectType object, int x1, int y1, int x2, int y2) {
        switch(object) {
            case GUARD_SPAWN -> {
                if (placedGuardSpawn)
                    return;
                else
                    placedGuardSpawn = true;
            }
            case INTRUDER_SPAWN -> {
                if (placedIntruderSpawn)
                    return;
                else
                    placedIntruderSpawn = true;
            }

            case TELEPORT -> {
                teleportCount++;
                mode = EditorMode.PLACE_TELEPORT_EXIT;
            }
        }

        MapObject new_object = new MapObject(object, x1, y1, x2, y2);
        mapObjects.add(new_object);

        System.out.println(new_object);
        gui.drawGUI();
    }

    public MapObject getLastObject() {
        if (mapObjects.size() > 0)
            return mapObjects.get(mapObjects.size()-1);
        else
            return null;
    }

    public void deleteLast() {
        MapObject last = getLastObject();
        if (last != null) {

            if (last.object_type == ObjectType.GUARD_SPAWN)
                placedGuardSpawn = false;
            else if (last.object_type == ObjectType.INTRUDER_SPAWN)
                placedIntruderSpawn = false;
            else if (last.object_type == ObjectType.TELEPORT)
                teleportCount--;

            mapObjects.remove(last);
        }
    }

    public ArrayList<MapObject> getMapObjects() {
        return mapObjects;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public static void startFromMain() {
        launch();
    }
}