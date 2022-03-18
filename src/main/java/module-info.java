module com.christophersch.mas_map_editor {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.christophersch.mas_map_editor to javafx.fxml;
    exports com.christophersch.mas_map_editor;
    exports com.christophersch.mas_map_editor.Enums;
    opens com.christophersch.mas_map_editor.Enums to javafx.fxml;
}