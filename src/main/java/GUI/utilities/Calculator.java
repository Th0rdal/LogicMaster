package GUI.utilities;

/**
 * defines some calculations needed.
 */
public class Calculator {

    /**
     * Rounds value based on position. ONLY ROUNDS UP AT 85 OR LARGER (e.g., 230 -> 200 / 290 -> 300)
     * @param value: value of the integer to round.
     * @param position: position round up to. 1 = ones position, 2 = tens position, ...
     * @return
     */
    public static int round(int value, int position) {

        int divider = (int) Math.pow(10, position);
        float tempValue = (float) value / divider;
        int tempChooser = value % divider;
        if (tempChooser > 85) { // value is bigger than 75 (e.g., 278 -> 300)
            return (int) (Math.ceil(tempValue) * divider);
        } else {
            return (int) (Math.floor(tempValue) * divider);
        }
    }

    /**
     * Formats the time given into a format usable for the clock
     * @param timeInTensOfSecond: time left given in tens of seconds
     * @return String formatted for the clock
     */
    public static String getClockTimeInFormat(int timeInTensOfSecond) {
        java.time.Duration duration = java.time.Duration.ofMillis(timeInTensOfSecond* 100L);
        String time;
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        long seconds = duration.minusHours(hours).minusMinutes(minutes).toSeconds();
        if (hours > 0) {
            time = String.format("%01d:%02d:%02d", hours, minutes, seconds);
        } else if (minutes == 0) { // if last minute, show first digit of ms as well
            time = String.format("%02d:%02d:%01d", minutes, seconds, duration.minusSeconds(seconds).toMillis() / 100);
        } else {
            time = String.format("%02d:%02d", minutes, duration.minusMinutes(minutes).toSeconds());
        }
        return time;
    }
}
