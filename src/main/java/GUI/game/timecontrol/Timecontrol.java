package GUI.game.timecontrol;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Timecontrol {
    private final int startTime;
    private final int increment;
    private final HashMap<Integer, String> timecontrolChangeMap;
    public static final Timecontrol zeroTimecontrol = new Timecontrol(0, 0, false); // used to display custom in index page
    private final boolean custom;

    public Timecontrol(int startTime, int increment, boolean custom) {
        this.startTime = startTime;
        this.increment = increment;
        this.timecontrolChangeMap = new HashMap<>();
        this.custom = custom;
    }

    public Timecontrol(String timecontrolString) {
        try {
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
            throw new RuntimeException();
        }
    }

    private int interpretStartTimeString(String startTimeString) {
        if (startTimeString.contains(".")) {
            return Integer.parseInt(startTimeString.substring(0, startTimeString.indexOf("."))) * 60
            + Integer.parseInt(startTimeString.substring(startTimeString.indexOf(".")+1));
        }
        return Integer.parseInt(startTimeString) * 60;
    }

    public boolean hasTimecontrolSpecialChanges(int fullmoveCounter) {
        return this.timecontrolChangeMap.containsKey(fullmoveCounter);
    }

    public int[] getTimecontrolChanges(int fullmoveCounter) {
        if (!hasTimecontrolSpecialChanges(fullmoveCounter)) {
            return new int[]{0, 0};
        }
        String temp = this.timecontrolChangeMap.get(fullmoveCounter);
        return new int[]{this.interpretStartTimeString(temp.substring(0, temp.indexOf("+"))),
                this.interpretIncrement(temp.substring(temp.indexOf("+")+1))};
    }

    public int interpretIncrement(String incrementString) {
        if (incrementString.contains(".")) {
            return Integer.parseInt(incrementString.substring(0, incrementString.indexOf("."))) * 60
            + Integer.parseInt(incrementString.substring(incrementString.indexOf(".")+1));
        }
        return Integer.parseInt(incrementString);
    }

    @Override
    public String toString() {
        if (this.startTime == 0) {
            return "custom";
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
        } else if (this.custom != ((Timecontrol) obj).custom) {
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

    public static ArrayList<Integer> convertByteTimeToArrayList(byte[] timeBytes) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < timeBytes.length; i=i+4) {
            byte[] temp = {timeBytes[i], timeBytes[i+1], timeBytes[i+2], timeBytes[i+3]};
            list.add(ByteBuffer.wrap(temp).getInt());
        }
        return list;
    }

    public boolean hasNoStartValue() {
        return this.startTime != 0;
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
