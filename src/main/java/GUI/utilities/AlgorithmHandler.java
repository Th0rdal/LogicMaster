package GUI.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Handles the algorithm call.
 * The calculate functions could be called multiple times on the same fen!!!
 */
public class AlgorithmHandler {

    private Move chosenMove = null;
    private HashMap<String, ArrayList<Move>> possibleMoves = new HashMap<>();
    private HashMap<String, String> parameter = new HashMap<>();
    private String path = "";
    private String inputString = "";
    private boolean possibleMovesDone = false;
    private boolean moveDone = false;

    public AlgorithmHandler(String path) {
        this.path = path;
    }

    public void clearCalculation() {
        this.possibleMoves.clear();
        this.chosenMove = null;
        this.inputString = "";
        this.possibleMovesDone = false;
        this.moveDone = false;
    }

    public Move calculateMove(String fen) { // TODO should me mandatory function in base
        if (!Objects.equals(fen, this.inputString)) {
            this.clearCalculation();
        } else if (this.moveDone) {
            return this.chosenMove;
        }
        this.executeAlgorithm(this.prepareCommand(fen, null));
        this.moveDone = true;
        return this.chosenMove;
    }

    public HashMap<String, ArrayList<Move>> calculatePossibleMoves(String fen) { // TODO should me mandatory function in base
        if (!Objects.equals(fen, this.inputString)) {
            this.clearCalculation();
        } else if (this.possibleMovesDone) {
            return this.possibleMoves;
        }
        HashMap<String, String> tempPossibleMoves = new HashMap<>();
        tempPossibleMoves.put("-ifen", "");
        tempPossibleMoves.put("-opm", "");
        tempPossibleMoves.put("-mt", "1");

        this.executeAlgorithm(this.prepareCommand(fen, tempPossibleMoves));
        this.possibleMovesDone = true;
        return this.possibleMoves;
    }

    private String[] prepareCommand(String fen, HashMap<String, String> passedParameter) {
        HashMap<String, String> givenParameter = new HashMap<>();
        if (!Objects.equals(fen, "")) {
            this.inputString = fen;
        }
        if (passedParameter == null) {
            givenParameter.putAll(this.parameter);
        } else {
            givenParameter.putAll(passedParameter);
        }

        String temp = "\"" + this.inputString + "\"";

        ArrayList<String> params = new ArrayList<>();
        for (Map.Entry<String, String> entry : givenParameter.entrySet()) {
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

        return command;

    }

    private void executeAlgorithm(String[] command) { // TODO should me mandatory function in base
        this.chosenMove = null;
        this.possibleMoves = new HashMap<>();

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
               this.chosenMove = line.isEmpty() ? null : new Move(line);

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

    public void setParameter() { // TODO should me mandatory function in base
        this.parameter.put("-ifen", "");
        this.parameter.put("-md", "4");
        this.parameter.put("-om", "");
        this.parameter.put("-mt", "16");
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
