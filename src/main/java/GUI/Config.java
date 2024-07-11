package GUI;

import GUI.controller.AlertHandler;
import GUI.exceptions.InvalidTimecontrolException;
import GUI.player.algorithm.AIFile;
import GUI.game.timecontrol.Timecontrol;
import GUI.game.timecontrol.TimecontrolSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.scene.paint.Color;

import GUI.exceptions.ConfigurationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Stream;

/**
 * handles loading and saving the configuration from config.json. Designed as singleton
 */
public class Config {
    public static final String START_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static final int MAX_CHARACTER_NAME = 40;

    // colors used for the board
    public static final Color squareColorWhite = Color.LIGHTGRAY;
    public static final Color squareColorBlack = Color.SIENNA;
    public static final Color selectedMoveHistory = Color.LIGHTBLUE;
    public static final Color selectedColor = Color.web("#C6EDC3");
    public static final Color selectedTextColor = Color.BLUE;
    public static final Color defaultTextColor = Color.BLACK;

    public static final String aiFilesPath = "algorithms";
    private static final ArrayList<AIFile> aiFiles = new ArrayList<>();
    private ArrayList<Timecontrol> timecontrol;
    private static Config instance;

    private Config() {
        this.loadConfig();
    }

    /**
     * returns the instance of config if it exists, else it creates it beforehand
     * @return Config instance
     */
    public static Config getInstance() {
        if(instance == null) {
            instance = new Config();
        }
        return instance;
    }

    /**
     * saves the current Configuration to the config.json file
     */
    public void saveConfig() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Timecontrol.class, new TimecontrolSerializer()).setPrettyPrinting().create();
        String json = gson.toJson(Config.getInstance());
        try (FileWriter writer = new FileWriter("config.json")) {
             writer.write(json);
         } catch (IOException e) {
            AlertHandler.throwError();
            throw new ConfigurationException("Failed to save configuration", e);
         }
    }

    /**
     * loads the current Configuration from the config.json file
     */
    public void loadConfig() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Timecontrol.class, new TimecontrolSerializer()).setPrettyPrinting().create();
        try (FileReader reader = new FileReader("config.json")) {
             Map<String, List<String>> data = gson.fromJson(reader, new TypeToken<Map<String, List<String>>>(){}.getType());
             this.timecontrol = new ArrayList<>();
             for (String temp : data.get("timecontrol")) {
                 try { // ignored exception just so the error does not stop execution
                     this.timecontrol.add(new Timecontrol(temp));
                 } catch (InvalidTimecontrolException ignored) {
                     String message = MessageFormat.format("The timecontrol string '%s' is not valid.", temp);
                     AlertHandler.throwWarningAndWait("invalid timecontrol", message);
                 }
             }
             this.timecontrol.sort(Comparator.comparingInt(Timecontrol::getStartTime));
         } catch (IOException e) {
            AlertHandler.throwError();
            throw new ConfigurationException("Failed to load configuration", e);
         }
    }

    /**
     * remove time control from the configuration
     * @param timecontrol: the time control object to remove
     */
    public void removeTimecontrol(Timecontrol timecontrol) {
        this.timecontrol.remove(timecontrol);
    }

    public List<Timecontrol> getTimecontrol() {
        return timecontrol;
    }

    public void setTimecontrol(ArrayList<Timecontrol> timecontrol) {
        this.timecontrol = timecontrol;
    }

    /**
     * add a time control to the array
     * @param timecontrol: the time control to add
     */
    public void addTimecontrol(Timecontrol timecontrol) {
        for (Timecontrol temp : this.timecontrol) {
            if (temp.equals(timecontrol)) {
                return;
            }
        }
        this.timecontrol.add(timecontrol);
    }

    /**
     * gets all files that are in the algorithms folder (.gitkeep is an exception, because it is not seen as folder)
     * @return: ArrayList of AIFiles with all files that are in teh algorithms folder
     */
    public static ArrayList<AIFile> getAiFiles() {
        if (Config.aiFiles.isEmpty()) {
            Path directoryPath = Paths.get(Config.aiFilesPath);

            try (Stream<Path> filePathStream = Files.walk(directoryPath)) {
             filePathStream.filter(Files::isRegularFile).forEach(element -> {

                 Config.aiFiles.add(
                     new AIFile(
                         element.toString(),
                         element.getFileName().toString().substring(0, element.getFileName().toString().lastIndexOf("."))
                     )
                 );
             });
         } catch (IOException e) {
                AlertHandler.throwError();
                throw new ConfigurationException("Failed to load AI files", e);
            }
        }

        return Config.aiFiles;
    }
}
