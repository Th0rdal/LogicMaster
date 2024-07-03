package GUI.utilities;

import org.h2.jdbc.JdbcDatabaseMetaData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AlgorithmHandler {

    private Move chosenMove = null;
    private HashMap<String, ArrayList<Move>> possibleMoves = new HashMap<>();
    private HashMap<String, String> parameter = new HashMap<>();
    private String path = "";
    private String inputString = "";

    public AlgorithmHandler(String path) {
        this.path = path;
    }

    public void executeAlgorithm(String fen) {
        this.chosenMove = null;
        this.possibleMoves = new HashMap<>();

        if (!Objects.equals(fen, "")) {
            this.inputString = fen;
        }

        String temp = "\"" + this.inputString + "\"";
        System.out.println(temp);

        ArrayList<String> params = new ArrayList<>();
        for (Map.Entry<String, String> entry : this.parameter.entrySet()) {
            params.add(entry.getKey());
            if (!Objects.equals(entry.getValue(), "")) {
                params.add(entry.getValue());
            }
        }
        String command[] = new String[params.size()+2];
        int index = 0;
        command[index++] = this.path;
        System.arraycopy(params.toArray(new String[0]), 0, command, index, params.size());
        command[params.size()+1] = temp;

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
               this.chosenMove = line.equals("") ? null : new Move(line);
               System.out.println(this.chosenMove);

               while ((line = stdInput.readLine()) != null) {
                   Move tempMove = new Move(line);
                   //TODO handle draw and other non standard moves
                   if (tempMove.getSpecialMove() == SPECIAL_MOVE.KING_CASTLE || tempMove.getSpecialMove() == SPECIAL_MOVE.QUEEN_CASTLE) {
                       ArrayList<Move> tempMoves = new ArrayList<>();
                       tempMoves.add(tempMove);
                       this.possibleMoves.put("CASTLE", tempMoves);
                   } else if (this.possibleMoves.containsKey(tempMove.getOldPosition().toString())) {
                       this.possibleMoves.get(tempMove.getOldPosition().toString()).add(tempMove);
                   } else {
                       ArrayList<Move> tempMoves = new ArrayList<>();
                       tempMoves.add(tempMove);
                       this.possibleMoves.put(tempMove.getOldPosition().toString(), tempMoves);
                   }

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

    public HashMap<String, ArrayList<Move>> getPossibleMoves() {
        return possibleMoves;
    }

    public void setParameter(HashMap<String, String> parameter) {
        this.parameter = parameter;
    }

    public void addParameter(String key, String value) {
        this.parameter.put(key, value);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setInputString(String inputString) {
        this.inputString = inputString;
    }
}
