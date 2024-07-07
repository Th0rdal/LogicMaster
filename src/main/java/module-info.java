module GUI {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.rmi;
    requires com.h2database;
    requires com.google.gson;
    opens GUI;
    opens GUI.utilities;
    opens GUI.Player;
    opens GUI.game;
    opens GUI.Controller;
}