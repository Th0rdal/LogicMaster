package GUI.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class AlgorithmHandler {

    private Move chosenMove = null;
    private ArrayList<Move> possibleMoves = new ArrayList<>();
    

    public void executeAlgorithmFen(String path, String fen) {
        this.chosenMove = null;
        this.possibleMoves = new ArrayList<>();

        String temp = "\"" + fen + "\"";
        System.out.println(temp);
        String[] command = {path, "-ifen", "-md", "4", "-om", "-mt", "16", temp};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        System.out.println(String.join(" ", processBuilder.command().toArray(new String[0])));
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("exited not with code 0, but: " + exitCode);
            }

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Thread stderrThread = getErrorOutput(process);

            try {
               String line;
               line = stdInput.readLine();
               this.chosenMove = new Move(line);

               while ((line = stdInput.readLine()) != null) {
                   possibleMoves.add(new Move(line));
               }
                stdInput.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            stderrThread.join();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Thread getErrorOutput(Process process) {
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        Thread stderrThread = new Thread(() -> {
            try {
                String line;
                while ((line = stdError.readLine()) != null) {
                    System.err.println("Error:\n" + line);
                }
                stdError.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        stderrThread.start();
        return stderrThread;
    }

    public Move getChosenMove() {
        return this.chosenMove;
    }
    public ArrayList<Move> getPossibleMoves() {
        return this.possibleMoves;
    }
}
