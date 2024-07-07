package GUI.controller;

import GUI.player.Algorithm.AIFile;
import GUI.Config;
import GUI.handler.GameHandler;
import GUI.game.timecontrol.Timecontrol;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;

public class IndexController {

     @FXML
     private Button startGameButton;
     @FXML
     private Button loadButton;
     @FXML
     private Button resetFenButton;
     @FXML
     private CheckBox timeControlCheckBox;
     @FXML
     private ChoiceBox<AIFile> whitePlayerChoiceBox;
     @FXML
     private ChoiceBox<AIFile> blackPlayerChoiceBox;
     @FXML
     private CheckBox whitePlayerHumanCheckBox;
     @FXML
     private CheckBox blackPlayerHumanCheckBox;
     @FXML
     private CheckBox whiteSideDownCheckBox;
     @FXML
     private ChoiceBox<Timecontrol> timeControlChoiceBox;
     @FXML
     private TextField timeControlTextField;
     @FXML
     private Label whiteNameLabel;
     @FXML
     private Label blackNameLabel;
     @FXML
     private TextField whitePlayerNameTextBox;
     @FXML
     private TextField blackPlayerNameTextBox;
     @FXML
     private TextField fenTextBox;
     @FXML
     private Button deleteTimeControlButton;
     @FXML
     private Button saveTimeControlButton;

     public void loadData(GameHandler gameHandler) {
         // defining actions
         this.startGameButton.setOnAction(e -> {
             Timecontrol timecontrol = null;
             String whiteName = whitePlayerHumanCheckBox.isSelected() && whitePlayerNameTextBox.getText().isEmpty() ?
                     "Anonymous" : whitePlayerNameTextBox.getText();
             String blackName = blackPlayerHumanCheckBox.isSelected() && blackPlayerNameTextBox.getText().isEmpty() ?
                     "Anonymous" : blackPlayerNameTextBox.getText();
             AIFile whitePath = whitePlayerChoiceBox.getValue();
             AIFile blackPath = blackPlayerChoiceBox.getValue();

             if (!whitePlayerHumanCheckBox.isSelected()) {
                 whiteName = "";
                 if (whitePath == null) {
                     AlertHandler.showAlert(Alert.AlertType.INFORMATION, "AI missing", "no AI chosen for white player");
                     return;
                 }
             }
             if (!blackPlayerHumanCheckBox.isSelected()) {
                 blackName = "";
                 if (blackPath == null) {
                     AlertHandler.showAlert(Alert.AlertType.INFORMATION, "AI missing", "no AI chosen for black player");
                     return;
                 }
             }

             if (timeControlCheckBox.isSelected()) {
                 if (timeControlChoiceBox.getValue().hasNoStartValue()) {
                     timecontrol = timeControlChoiceBox.getValue();
                 } else {
                     try {
                         timecontrol = new Timecontrol(timeControlTextField.getText());
                     } catch (RuntimeException ex) {
                         AlertHandler.showAlert(Alert.AlertType.ERROR, "Error", "The time control string could not be converted. It must only include numbers, '/', '.', ':' and '+'!");
                         return;
                     }
                 }
             }

             // the thread needs effectively final variables
             Timecontrol finalTimecontrol = timecontrol;
             String finalWhiteName = whiteName;
             String finalBlackName = blackName;
             if (gameHandler.isGameInitialized()) {
                 //TODO make alerthandler
                 Alert alert = new Alert(Alert.AlertType.INFORMATION);
                 alert.setTitle("old game found");
                 alert.setHeaderText(null);
                 alert.setContentText("You already have a game running. Do you wish to continue or start a new Game? The old game will be lost if it was not saved in the database");
                 ButtonType buttonYes = new ButtonType("start new game");
                 ButtonType buttonNo = new ButtonType("continue old game");
                 alert.getButtonTypes().setAll(buttonYes, buttonNo);
                 Optional<ButtonType> result = alert.showAndWait();
                 if (result.isPresent() && result.get() == buttonYes) {
                     new Thread(() -> {
                     gameHandler.startGame(finalTimecontrol,
                             finalWhiteName,
                             finalBlackName,
                             whitePath,
                             blackPath,
                             fenTextBox.getText().strip(),
                             whiteSideDownCheckBox.isSelected());
                     }).start();
                 } else {
                    new Thread(gameHandler::gameLoop);
                 }
             } else {
                 new Thread(() -> {
                     gameHandler.startGame(finalTimecontrol,
                             finalWhiteName,
                             finalBlackName,
                             whitePath,
                             blackPath,
                             fenTextBox.getText().strip(),
                             whiteSideDownCheckBox.isSelected());
                 }).start();
             }
         });
         this.timeControlCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
              @Override
              public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                   timeControlChoiceBox.setVisible(newValue);
                   deleteTimeControlButton.setVisible(newValue);
                   if (newValue && Objects.equals(timeControlChoiceBox.getValue().toString(), "custom")) {
                       saveTimeControlButton.setVisible(true);
                       timeControlTextField.setVisible(true);
                       deleteTimeControlButton.setVisible(false);
                   } else {
                       saveTimeControlButton.setVisible(false);
                       timeControlTextField.setVisible(false);
                   }

              }
         });
         this.whitePlayerHumanCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
              @Override
              public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                   whiteNameLabel.setVisible(newValue);
                   whitePlayerNameTextBox.setVisible(newValue);
                   whitePlayerChoiceBox.setVisible(!newValue);
              }
         });
         this.blackPlayerHumanCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
              @Override
              public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                   blackNameLabel.setVisible(newValue);
                   blackPlayerNameTextBox.setVisible(newValue);
                   blackPlayerChoiceBox.setVisible(!newValue);
              }
         });

         this.timeControlChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
             if (newValue == null) {
                 return;
             }
             timeControlTextField.setVisible(Objects.equals(newValue.toString(), "custom"));
             saveTimeControlButton.setVisible(Objects.equals(newValue.toString(), "custom"));
             deleteTimeControlButton.setVisible(!Objects.equals(newValue.toString(), "custom"));
         });

         this.saveTimeControlButton.setOnAction(e -> {
             Config instance = Config.getInstance();
             try {
                 Timecontrol tc = new Timecontrol("c: " + timeControlTextField.getText());
                 instance.addTimecontrol(tc);
                 instance.saveConfig();
                 timeControlChoiceBox.getItems().clear();
                 timeControlChoiceBox.getItems().addAll(instance.getTimecontrol());
                 timeControlChoiceBox.getSelectionModel().select(tc);
             } catch (RuntimeException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The time control string could not be converted. It must only include numbers, '/', ':' and '+'!");
                alert.showAndWait();
             }
         });

         this.deleteTimeControlButton.setOnAction(e -> {
             Timecontrol timecontrol = timeControlChoiceBox.getValue();
             if (timecontrol.isCustom()) {
                 Config instance = Config.getInstance();
                 instance.removeTimecontrol(timecontrol);
                 timeControlChoiceBox.getItems().clear();
                 timeControlChoiceBox.getItems().addAll(instance.getTimecontrol());
                 timeControlChoiceBox.getSelectionModel().select(0);
                 instance.saveConfig();
             } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The time control cannot be removed. Only custom time controls (marked with a 'c: ') are removable");
                alert.showAndWait();
             }
         });

         // time control choice box configuration
         List<Timecontrol> tcArray = new ArrayList<>(Config.getInstance().getTimecontrol());
         tcArray.add(Timecontrol.zeroTimecontrol);
         this.timeControlChoiceBox.getItems().addAll(tcArray);
         this.timeControlChoiceBox.getSelectionModel().select(0);

         // fen text box configuration
         this.fenTextBox.setText("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
         this.resetFenButton.setOnAction(e -> {
             fenTextBox.setText("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
         });

         this.whitePlayerChoiceBox.getItems().addAll(Config.getAiFiles());
         this.blackPlayerChoiceBox.getItems().addAll(Config.getAiFiles());
     }
}
