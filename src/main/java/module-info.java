module LogicMaster {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.rmi;
    requires com.h2database;
    requires com.google.gson;
    requires ormlite.jdbc;
    requires java.sql;
    opens GUI;
    opens GUI.utilities;
    opens GUI.player;
    opens GUI.game;
    opens GUI.controller;
    opens GUI.player.algorithm;
    opens GUI.game.timecontrol;
    opens GUI.game.gamestate;
    opens GUI.game.move;
    opens database;
}