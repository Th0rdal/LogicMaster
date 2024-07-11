package GUI.game.timecontrol;

import GUI.exceptions.InvalidTimecontrolException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * represents a time control of a chess game
 */
public class Timecontrol {
    private final int startTime;
    private final int increment;
    private final HashMap<Integer, String> timecontrolChangeMap;
    public static final Timecontrol zeroTimecontrol = new Timecontrol("no timecontrol"); // used to display custom in index page
    private final boolean custom;

    /**
     * Timecontrol constructor
     * @param timecontrolString: the timecontrol in string representation
     */
    public Timecontrol(String timecontrolString) {
        try {
            if (Objects.equals(timecontrolString, "no timecontrol")) {
                startTime = 0;
                increment = 0;
                timecontrolChangeMap = new HashMap<>();
                custom = false;
                return;
            }
            this.timecontrolChangeMap = new HashMap<>();
            int index;
            if (timecontrolString.startsWith("c: ")) {
                this.custom = true;
                index = 3;
            } else {
                this.custom = false;
                index = 0;
            }
            int endIndex = timecontrolString.indexOf("+");
            this.startTime = this.interpretStartTimeString(timecontrolString.substring(index, endIndex));
            index = endIndex + 1;
            endIndex = timecontrolString.indexOf("/", index);
            endIndex = endIndex == -1 ? timecontrolString.length() : endIndex;
            this.increment = this.interpretIncrement(timecontrolString.substring(index, endIndex));

            if (!(timecontrolString.indexOf("/", endIndex+1) == -1)) {
                do {
                    index = endIndex + 1;
                    endIndex = timecontrolString.indexOf("/", index) == -1 ? timecontrolString.length() : timecontrolString.indexOf("/", index);
                    timecontrolChangeMap.put(
                            Integer.parseInt(timecontrolString.substring(index, timecontrolString.indexOf(":", index))),
                            timecontrolString.substring(timecontrolString.indexOf(":", index) + 1, endIndex));
                } while (endIndex != timecontrolString.length());
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new InvalidTimecontrolException();
        }
    }

    /**
     * used to interpret a start time (so if it has a . in it, it is correctly interpreted)
     * @param startTimeString: the start time in string format
     * @return int representing the time of the startTimeString in minutes
     */
    private int interpretStartTimeString(String startTimeString) {
        if (startTimeString.contains(".")) {
            return Integer.parseInt(startTimeString.substring(0, startTimeString.indexOf("."))) * 60
            + Integer.parseInt(startTimeString.substring(startTimeString.indexOf(".")+1));
        }
        return Integer.parseInt(startTimeString) * 60;
    }

    /**
     * returns true if there are changes for the current move counter
     * @param fullmoveCounter: the full move counter to check
     * @return true if there are, else false
     */
    public boolean hasTimecontrolSpecialChanges(int fullmoveCounter) {
        return this.timecontrolChangeMap.containsKey(fullmoveCounter);
    }

    /**
     * returns the changes to start time and increment in an array of size 2, based on the current full move. If there are no changes, it returns 0 for the values
     * @param fullmoveCounter: the full move counter to use
     * @return int array with start time and increment (start time index 0)
     */
    public int[] getTimecontrolChanges(int fullmoveCounter) {
        if (!hasTimecontrolSpecialChanges(fullmoveCounter)) {
            return new int[]{0, 0};
        }
        String temp = this.timecontrolChangeMap.get(fullmoveCounter);
        return new int[]{this.interpretStartTimeString(temp.substring(0, temp.indexOf("+"))),
                this.interpretIncrement(temp.substring(temp.indexOf("+")+1))};
    }

   /**
     * used to interpret an increment (so if it has a . in it, it is correctly interpreted)
     * @param incrementString: the increment in string format
     * @return int representing the time of the incrementString in seconds
     */
    public int interpretIncrement(String incrementString) {
        if (incrementString.contains(".")) {
            return Integer.parseInt(incrementString.substring(0, incrementString.indexOf("."))) * 60
            + Integer.parseInt(incrementString.substring(incrementString.indexOf(".")+1));
        }
        return Integer.parseInt(incrementString);
    }

    @Override
    public String toString() {
        if (!this.isActive()) {
            return "no timecontrol";
        }
        int minutes = this.startTime / 60;
        int seconds = this.startTime - minutes * 60;
        String startTime = seconds == 0 ? String.valueOf(minutes) : minutes + "." + seconds;

        int incrementMinutes = this.increment / 60;
        int incrementSeconds = this.increment - incrementMinutes * 60;
        String incrementString = incrementMinutes == 0 ? String.valueOf(incrementSeconds) : incrementMinutes + "." + incrementSeconds;

        StringBuilder builder = new StringBuilder();
        if (this.custom) {
            builder.append("c: ");
        }
        builder.append(startTime).append("+").append(incrementString);
        if (!(this.timecontrolChangeMap == null) && !this.timecontrolChangeMap.isEmpty()) {
            for (Map.Entry<Integer, String> entry : this.timecontrolChangeMap.entrySet()) {
                builder.append("/").append(entry.getKey()).append(":").append(entry.getValue());
            }
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Timecontrol)) {
            return false;
        }
        if (this.startTime != ((Timecontrol) obj).startTime) {
            return false;
        } else if (this.increment != ((Timecontrol) obj).increment) {
            return false;
        } else if (this.timecontrolChangeMap.size() != ((Timecontrol) obj).timecontrolChangeMap.size()) {
            return false;
        } else {
            for (Map.Entry<Integer, String> entry : this.timecontrolChangeMap.entrySet()) {
                if (!Objects.equals(entry.getValue(), ((Timecontrol) obj).timecontrolChangeMap.get(entry.getKey()))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * converts the byte array of times into an ArrayList of integers
     * @param timeBytes: the byte representation of the time after the move was made
     * @return: ArrayList of integers representing the counter of time after the move was taken
     */
    public static ArrayList<Integer> convertByteTimeToArrayList(byte[] timeBytes) {
        ArrayList<Integer> list = new ArrayList<>();
        if (timeBytes == null) {
            for(int i = 0; i < timeBytes.length/4; i=i+4) {
                list.add(0);
            }
        }
        for (int i = 0; i < timeBytes.length; i=i+4) {
            byte[] temp = {timeBytes[i], timeBytes[i+1], timeBytes[i+2], timeBytes[i+3]};
            list.add(ByteBuffer.wrap(temp).getInt());
        }
        return list;
    }

    public boolean isActive() {
        return !(this.startTime == 0 && this.increment == 0 && this.timecontrolChangeMap.isEmpty());
    }

    public boolean isCustom() {
        return this.custom;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getIncrement() {
        return increment;
    }
}
