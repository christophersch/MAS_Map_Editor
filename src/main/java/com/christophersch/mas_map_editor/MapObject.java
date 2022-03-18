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

    public MapObject(ObjectType object_type, int x1, int y1, int x2, int y2) {
        this.object_type = object_type;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Color getColor() {
        return switch(object_type) {
            case WALL -> Color.BLACK;
            case TELEPORT -> Color.ORANGE;
            case INTRUDER_SPAWN -> Color.RED;
            case GUARD_SPAWN -> Color.BLUE;
            case SHADED -> Color.BLACK;
            case DOOR -> Color.BROWN;
            case WINDOW -> Color.AQUA;
            case TEXTURE -> Color.GREEN;
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
            case SHADED -> "shaded";
            case DOOR -> "door";
            case WINDOW -> "window";
            case TEXTURE -> "texture";
        };

        s += " = " + x1 + " " + y1 + " " + x2 + " " + y2;

        if (object_type == ObjectType.TELEPORT)
            s += " " + teleport_exit_x + " " + teleport_exit_y + " " + teleport_exit_theta;

        return s;
    }
}
