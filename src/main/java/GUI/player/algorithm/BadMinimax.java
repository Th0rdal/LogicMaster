package GUI.player.algorithm;

import GUI.controller.AlertHandler;
import GUI.exceptions.AlgorithmExecutionException;
import GUI.game.move.Move;
import GUI.game.move.SPECIAL_MOVE;

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
public class BadMinimax extends AlgorithmHandlerBase{

    private Move chosenMove = null;
    private HashMap<String, ArrayList<Move>> possibleMoves = new HashMap<>();
    private final HashMap<String, String> parameter = new HashMap<>();
    private String path = "";
    private String inputString = "";
    private boolean possibleMovesDone = false;
    private boolean moveDone = false;

    /**
     * constructor call
     * @param path: the path to the algorithm file
     */
    public BadMinimax(String path) {
        this.path = path;
    }

    /**
     * clears all variables for a new calculation
     */
    private void clearCalculation() {
        this.possibleMoves.clear();
        this.chosenMove = null;
        this.inputString = "";
        this.possibleMovesDone = false;
        this.moveDone = false;
    }

    /**
     * calculates the best move using the algorithm given
     * @param fen: the fen notation of the gamestate to get the move for
     * @return Move with the move the AI made
     */
    public Move calculateMove(String fen) {
        if (!Objects.equals(fen, this.inputString)) {
            this.clearCalculation();
        } else if (this.moveDone) {
            return this.chosenMove;
        }
        this.executeAlgorithm(this.prepareCommand(fen, null));
        this.moveDone = true;
        return this.chosenMove;
    }

    /**
     * calculates all possible moves from a given gamestate
     * @param fen: the fen notation of the gamestate to the get the possible moves vor
     * @return: Hashmap with all moves in an arraylist, accessible with the string notation of the start position
     */
    public HashMap<String, ArrayList<Move>> calculatePossibleMoves(String fen) {
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

    /**
     * prepares the command that will be sent to the algorithm
     * @param fen: the fen notation to send
     * @param passedParameter: the parameter to send (like maxDepth, maxThreads, etc...)
     * @return: String array of all commands
     */
    private String[] prepareCommand(String fen, HashMap<String, String> passedParameter) {
        if (!Objects.equals(fen, "")) {
            this.inputString = fen;
        }
        HashMap<String, String> givenParameter = new HashMap<>(Objects.requireNonNullElse(passedParameter, this.parameter));

        String temp = "\"" + this.inputString + "\"";

        ArrayList<String> params = new ArrayList<>();
        for (Map.Entry<String, String> entry : givenParameter.entrySet()) {
            params.add(entry.getKey());
            if (!Objects.equals(entry.getValue(), "")) {
                params.add(entry.getValue());
            }
        }
        String[] command = new String[params.size()+2];
        int index = 0;
        command[index++] = this.path;
        System.arraycopy(params.toArray(new String[0]), 0, command, index, params.size());
        command[params.size()+1] = temp;

        return command;

    }

    /**
     * executes the actual algorithm in a Process
     * @param command: String array of all commands to send the algorithm
     */
    private void executeAlgorithm(String[] command) {
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

                ArrayList<Move> temp = new ArrayList<>();
                this.possibleMoves.put("DRAW", temp);
                while ((line = stdInput.readLine()) != null) {
                    Move tempMove = new Move(line);
                    if (tempMove.isDraw()) {
                       ArrayList<Move> tempMoves = new ArrayList<>();
                       tempMoves.add(tempMove);
                       this.possibleMoves.put("DRAW", tempMoves); // should be empty unless it is a draw
                    }
                    if (tempMove.getSpecialMove() == SPECIAL_MOVE.KING_CASTLE || tempMove.getSpecialMove() == SPECIAL_MOVE.QUEEN_CASTLE) {
                        if (this.possibleMoves.containsKey("CASTLE")) {
                            this.possibleMoves.get("CASTLE").add(tempMove);
                        } else {
                            ArrayList<Move> tempMoves = new ArrayList<>();
                            tempMoves.add(tempMove);
                            this.possibleMoves.put("CASTLE", tempMoves);
                        }
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
                AlertHandler.throwError();
                throw new AlgorithmExecutionException("Something went wrong while trying to read a line", e);
            }

            stderrThread.join();

        } catch (IOException | InterruptedException e) {
            AlertHandler.throwError();
            throw new AlgorithmExecutionException("Something went wrong executing the algorithm", e);
        }
    }

    /**
     * sets the parameter to send. This is used to set the default commands
     */
    public void setParameter() {
        this.parameter.put("-ifen", "");
        this.parameter.put("-md", "4");
        this.parameter.put("-om", "");
        this.parameter.put("-mt", "16");
    }

    /**
     * checks if there was any stderr output from the algorithm.
     * @param process: the process containing the algorithm call
     * @return the Thread started to check if there are errors
     */
    private static Thread getErrorOutput(Process process) {
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        Thread stderrThread = new Thread(() -> {
            boolean errorFound = false;
            try {
                String line;
                while ((line = stdError.readLine()) != null) {
                    System.err.println("Error:\n" + line);
                    errorFound = true;
                }
                stdError.close();
            } catch (IOException e) {
                AlertHandler.throwError();
                throw new AlgorithmExecutionException("Something went wrong executing the algorithm and it returned an error", e);
            }
            if (errorFound) {
                AlertHandler.throwError();
                throw new AlgorithmExecutionException("Something went wrong executing the algorithm and it returned an error");
            }
        });

        stderrThread.start();
        return stderrThread;
    }
}
