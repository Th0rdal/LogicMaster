package GUI;

import GUI.player.Algorithm.AIFile;
import GUI.game.timecontrol.Timecontrol;
import GUI.game.timecontrol.TimecontrolSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Config {
    public static final String aiFilesPath = "algorithms";
    private static final ArrayList<AIFile> aiFiles = new ArrayList<>();
    private ArrayList<Timecontrol> timecontrol;
    private static Config instance;

    private Config() {
        this.loadConfig();
    }

    public static Config getInstance() {
        if(instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void saveConfig() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Timecontrol.class, new TimecontrolSerializer()).setPrettyPrinting().create();
        String json = gson.toJson(Config.getInstance());
        try (FileWriter writer = new FileWriter("config.json")) {
             writer.write(json);
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
    }

    public void loadConfig() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Timecontrol.class, new TimecontrolSerializer()).setPrettyPrinting().create();
        try (FileReader reader = new FileReader("config.json")) {
             Map<String, List<String>> data = gson.fromJson(reader, new TypeToken<Map<String, List<String>>>(){}.getType());
             this.timecontrol = new ArrayList<>();
             for (String temp : data.get("timecontrol")) {
                 try {
                     this.timecontrol.add(new Timecontrol(temp));
                 } catch (RuntimeException e) {}
             }
             this.timecontrol.sort(Comparator.comparingInt(Timecontrol::getStartTime));
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
    }

    public void removeTimecontrol(Timecontrol timecontrol) {
        this.timecontrol.remove(timecontrol);
    }

    public List<Timecontrol> getTimecontrol() {
        return timecontrol;
    }

    public void setTimecontrol(ArrayList<Timecontrol> timecontrol) {
        this.timecontrol = timecontrol;
    }

    public void addTimecontrol(Timecontrol timecontrol) {
        for (Timecontrol temp : this.timecontrol) {
            if (temp.equals(timecontrol)) {
                return;
            }
        }
        this.timecontrol.add(timecontrol);
    }

    public static ArrayList<AIFile> getAiFiles() {
        if (Config.aiFiles.isEmpty()) {
            Path directoryPath = Paths.get(Config.aiFilesPath);

            try (Stream<Path> filePathStream = Files.walk(directoryPath)) {
             filePathStream.filter(Files::isRegularFile).forEach(element -> {
                 String tempPath = element.toString();

                 Config.aiFiles.add(
                     new AIFile(
                         element.toString(),
                         element.getFileName().toString().substring(0, element.getFileName().toString().lastIndexOf("."))
                     )
                 );
                //paths.add(tempPath.substring(tempPath.lastIndexOf("\\")+1, tempPath.indexOf(".", tempPath.lastIndexOf("/"))));
             });
         } catch (IOException e) {}
        }

        return Config.aiFiles;
    }
}
