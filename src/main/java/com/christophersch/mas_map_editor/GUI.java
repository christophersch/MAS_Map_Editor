package com.christophersch.mas_map_editor;

import com.christophersch.mas_map_editor.Enums.EditorMode;
import com.christophersch.mas_map_editor.Enums.ObjectType;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class GUI {
    Canvas canvas;
    VBox contents;
    ToolBar toolbar;

    Button deleteLastObjectButton;
    Button exportToTxt;

    Spinner<Integer> mapWidth;
    Spinner<Integer> mapHeight;

    Spinner<Integer> agentCount;

    ComboBox<ObjectType> objectTypeComboBox = new ComboBox<>();

    MapEditor parent;

    // Used by the mouse when placing new objects
    int new_object_x1;
    int new_object_y1;
    int new_object_x2;
    int new_object_y2;

    int mouse_x;
    int mouse_y;

    boolean mouse_down = false;

    Label message;

    String text_message_default = "";
    String text_message_portalexit = "Click on teleport exit position";
    String text_message_portaldir = "Click in facing direction after teleporting from the exit position";

    public GUI(Stage primary_stage, MapEditor parent) {
        this.parent = parent;

        contents = new VBox();

        toolbar = new ToolBar();
        toolbar.setPadding(new Insets(15, 12, 15, 12));
        canvas = new Canvas(120, 80);

        TextField mapNameTextfield;

        // toolbar items
        objectTypeComboBox.getItems().setAll(ObjectType.values());
        objectTypeComboBox.setValue(ObjectType.WALL);
        objectTypeComboBox.setOnAction( e -> {
            if (parent.mode != EditorMode.EDIT) {
                parent.deleteLast();
                parent.mode = EditorMode.EDIT;
            }
        });

        deleteLastObjectButton = new Button("Undo");
        deleteLastObjectButton.setOnAction(e -> {
            parent.deleteLast();
            parent.mode = EditorMode.EDIT;

            drawGUI();
        });

        message = new Label();


        final Pane spacer1 = new Pane();
        HBox.setHgrow(
                spacer1,
                Priority.ALWAYS
        );

        final Pane spacer2 = new Pane();
        HBox.setHgrow(
                spacer2,
                Priority.ALWAYS
        );

        mapWidth = new Spinner<>(10,1000,120);
        mapWidth.setEditable(true);
        mapWidth.valueProperty().addListener( e -> {
            parent.mapWidth = mapWidth.getValue();
            drawGUI();
        });
        mapWidth.setPrefWidth(70);

        mapHeight = new Spinner<>(10,1000,80);
        mapHeight.setEditable(true);
        mapHeight.valueProperty().addListener( e -> {
            parent.mapHeight = mapHeight.getValue();
            drawGUI();
        });
        mapHeight.setPrefWidth(70);

        agentCount = new Spinner<>(1,99,5);
        agentCount.setPrefWidth(55);
        agentCount.setEditable(true);

        mapNameTextfield = new TextField();

        exportToTxt = new Button("Export to .txt");
        exportToTxt.setOnAction( e ->
                MapExporter.exportToTextFile(parent, mapNameTextfield.getText())
        );

        toolbar.getItems().addAll(objectTypeComboBox, deleteLastObjectButton, new Separator());
        toolbar.getItems().addAll(new Label("Map name"), mapNameTextfield, new Label("Width"), mapWidth, new Label("Height"), mapHeight, new Label("# Agents"), agentCount, new Separator());
        toolbar.getItems().addAll(spacer1, message, spacer2, exportToTxt);

        // assemble the scene
        contents.getChildren().addAll(toolbar, canvas);
        Scene scene = new Scene(contents, MapEditor.INITIAL_WINDOW_WIDTH, MapEditor.INITIAL_WINDOW_HEIGHT);

        addMouseControls(scene);

        primary_stage.setTitle("M.A.S. Group 3 - Map Editor");
        primary_stage.setScene(scene);
        primary_stage.show();

        primary_stage.show();

        deleteLastObjectButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN),
                () -> {
                    if (parent.mode != EditorMode.EDIT)
                        parent.deleteLast();
                    deleteLastObjectButton.fire();
                }
        );

        primary_stage.widthProperty().addListener((obs, oldVal, newVal) ->
            drawGUI()
        );
        primary_stage.heightProperty().addListener((obs, oldVal, newVal) ->
            drawGUI()
        );
    }

    private int[] translateMouseToCanvas(double x, double y) {
        int[] pos = {0,0};

        pos[0] = (int)(parent.mapWidth * (x / contents.getWidth()));
        pos[1] = (int)(parent.mapHeight * (y - toolbar.getHeight())/(contents.getHeight() - toolbar.getHeight()));

        return pos;
    }

    boolean exitedTeleporterMode = false;

    private void addMouseControls(Scene scene) {

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {

            if (parent.mode == EditorMode.EDIT) {
                int[] mouse_pos_canvas = translateMouseToCanvas(e.getSceneX(), e.getSceneY());
                new_object_x1 = mouse_pos_canvas[0];
                new_object_y1 = mouse_pos_canvas[1];
                new_object_x2 = new_object_x1;
                new_object_y2 = new_object_y1;

                mouse_down = true;
            } else if (parent.mode == EditorMode.PLACE_TELEPORT_EXIT && mouse_y > 0) {
                int[] mouse_pos_canvas = translateMouseToCanvas(e.getSceneX(), e.getSceneY());

                parent.getLastObject().teleport_exit_x = mouse_pos_canvas[0];
                parent.getLastObject().teleport_exit_y = mouse_pos_canvas[1];

                parent.mode = EditorMode.SET_TELEPORT_EXIT_DIRECTION;
            } else if (parent.mode == EditorMode.SET_TELEPORT_EXIT_DIRECTION && mouse_y > 0) {

                parent.mode = EditorMode.EDIT;

                exitedTeleporterMode = true;
            }

            drawGUI();
        });

        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            int[] mouse_pos_canvas = translateMouseToCanvas(e.getSceneX(), e.getSceneY());
            mouse_x = mouse_pos_canvas[0];
            mouse_y = mouse_pos_canvas[1];

            if (parent.mode == EditorMode.EDIT && !exitedTeleporterMode) {
                new_object_x2 = mouse_x;
                new_object_y2 = mouse_y;
            }
            drawGUI();
        });

        scene.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            int[] mouse_pos_canvas = translateMouseToCanvas(e.getSceneX(), e.getSceneY());
            mouse_x = mouse_pos_canvas[0];
            mouse_y = mouse_pos_canvas[1];

            if (parent.mode == EditorMode.SET_TELEPORT_EXIT_DIRECTION) {
                int teleport_x = parent.getLastObject().teleport_exit_x;
                int teleport_y = parent.getLastObject().teleport_exit_y;

                parent.getLastObject().teleport_exit_theta = Math.atan2(mouse_y - teleport_y, mouse_x - teleport_x);

            }

            drawGUI();
        });

        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if (parent.mode == EditorMode.EDIT && !exitedTeleporterMode) {
                if (new_object_x1 >= 0 && new_object_y1 >= 0)
                    parent.addArea(objectTypeComboBox.getValue(),
                            Math.min(new_object_x1, new_object_x2),
                            Math.min(new_object_y1, new_object_y2),
                            Math.max(new_object_x1, new_object_x2),
                            Math.max(new_object_y1, new_object_y2)
                    );

                mouse_down = false;
            }

            exitedTeleporterMode = false;

            drawGUI();
        });
    }

    void drawGUI() {
        switch(parent.mode) {
            case EDIT -> message.setText(text_message_default);
            case PLACE_TELEPORT_EXIT -> message.setText(text_message_portalexit);
            case SET_TELEPORT_EXIT_DIRECTION -> message.setText(text_message_portaldir);
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();

        double scale_x = contents.getWidth()/parent.mapWidth;
        double scale_y = (contents.getHeight() - toolbar.getHeight())/parent.mapHeight;

        canvas.setWidth(parent.mapWidth*scale_x);
        canvas.setHeight(parent.mapHeight*scale_y);

        gc.setFill(Color.WHITESMOKE);
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());

        gc.setStroke(Color.LIGHTBLUE);
        gc.strokeLine(0,mouse_y*scale_y,canvas.getWidth(),mouse_y*scale_y);
        gc.strokeLine(mouse_x*scale_x,0,mouse_x*scale_x,canvas.getHeight());


        // rectangle preview
        if (mouse_down) {
            gc.setLineWidth(1);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(
                    Math.min(new_object_x1,new_object_x2) * scale_x,
                    Math.min(new_object_y1,new_object_y2) * scale_y,
                    Math.abs(new_object_x2 - new_object_x1) * scale_x,
                    Math.abs(new_object_y2 - new_object_y1) * scale_y);
        }

        for(MapObject mobject : parent.getMapObjects()){
            boolean drawObject = true;

            // Teleport exit
            if (mobject.object_type == ObjectType.TELEPORT) {
                gc.setStroke(Color.ORANGE);
                // Draw exit if it exists
                if (mobject.teleport_exit_x > 0) {
                    gc.setLineWidth(2);
                    gc.strokeOval(
                            (mobject.teleport_exit_x - .5) * scale_x,
                            (mobject.teleport_exit_y - .5) * scale_y,
                            (1) * scale_x,
                            (1) * scale_y);

                    gc.setLineWidth(1);
                    gc.setLineDashes(10);

                    gc.strokeLine(
                            (mobject.x1 + mobject.x2) / 2.0 * scale_x,
                            (mobject.y1 + mobject.y2) / 2.0 * scale_y,
                            mobject.teleport_exit_x * scale_x,
                            mobject.teleport_exit_y * scale_y);
                }

                gc.setLineWidth(2);
                gc.setLineDashes(0);

                // Exit theta line
                double dir = mobject.teleport_exit_theta;
                double len = 5;

                int dir_x = (int)(mobject.teleport_exit_x + Math.cos(dir) * len);
                int dir_y = (int)(mobject.teleport_exit_y + Math.sin(dir) * len);

                gc.strokeLine(mobject.teleport_exit_x*scale_x, mobject.teleport_exit_y*scale_y,
                        dir_x*scale_x,dir_y*scale_y);
            } else if (mobject.object_type == ObjectType.SHADED) {
                gc.setGlobalAlpha(.25);
                gc.setFill(Color.BLACK);
                gc.fillRect(mobject.x1*scale_x,mobject.y1*scale_y,(mobject.x2-mobject.x1)*scale_x,(mobject.y2-mobject.y1)*scale_y);
                gc.setGlobalAlpha(1);
                drawObject = false;
            }

            if (drawObject) {
                gc.setStroke(mobject.getColor());
                if (mobject.doLines()) {
                    gc.setLineWidth(1);
                    gc.strokeLine(scale_x * mobject.x1, scale_y * mobject.y1, scale_x * mobject.x2, scale_y * mobject.y2);
                    gc.strokeLine(scale_x * mobject.x1, scale_y * mobject.y2, scale_x * mobject.x2, scale_y * mobject.y1);
                } else {
                    gc.setLineWidth(3);
                }
                gc.strokeRect(mobject.x1 * scale_x, mobject.y1 * scale_y, (mobject.x2 - mobject.x1) * scale_x, (mobject.y2 - mobject.y1) * scale_y);
            }
        }
    }
}
