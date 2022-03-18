package com.christophersch.mas_map_editor;

import com.christophersch.mas_map_editor.Enums.ObjectType;
import javafx.scene.paint.Color;

public class MapObject {
    ObjectType object_type;
    int x1;
    int y1;
    int x2;
    int y2;

    int teleport_exit_x = -999;
    int teleport_exit_y = -999;
    double teleport_exit_theta = 0.0;

    // Mainly used by teleports
    String idText = "";

    public MapObject(ObjectType object_type, int x1, int y1, int x2, int y2) {
        this.object_type = object_type;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        if (object_type == ObjectType.TELEPORT)
            idText = MapEditor.teleportCount + "";

    }

    public Color getColor() {
        return switch(object_type) {
            case WALL -> Color.BLACK;
            case TELEPORT -> Color.ORANGE;
            case INTRUDER_SPAWN -> Color.RED;
            case GUARD_SPAWN -> Color.BLUE;
        };
    }

    public boolean doLines() {
        return (object_type == ObjectType.WALL);
    }

    public String toString() {
        String s = "";

        s += switch(object_type) {
            case WALL -> "wall";
            case GUARD_SPAWN -> "spawnAreaGuards";
            case INTRUDER_SPAWN -> "spawnAreaIntruders";
            case TELEPORT -> "teleport";
        };

        s += " = " + x1 + " " + y1 + " " + x2 + " " + y2;

        if (object_type == ObjectType.TELEPORT)
            s += " " + teleport_exit_x + " " + teleport_exit_y + " " + teleport_exit_theta;

        return s;
    }
}
