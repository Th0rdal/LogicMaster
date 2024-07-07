module GUI {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.rmi;
    requires com.h2database;
    requires com.google.gson;
    opens GUI;
    opens GUI.utilities;
    opens GUI.player;
    opens GUI.game;
    opens GUI.controller;
    opens GUI.player.Algorithm;
    opens GUI.game.timecontrol;
    opens GUI.game.gamestate;
    opens GUI.game.move;
}