module LogicMaster {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.rmi;
    opens GUI;
    opens GUI.utilities;
}