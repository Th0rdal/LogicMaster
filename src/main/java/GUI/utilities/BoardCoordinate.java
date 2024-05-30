package GUI.utilities;

public class BoardCoordinate {

    // the locations are between 1 and 8!!!
    private int xLocation, yLocation;

    public BoardCoordinate(int x, int y) {
        xLocation = x;
        yLocation = y;
    }

    public int getLocationInt() {
        return xLocation * 10 + yLocation;
    }

    @Override
    public String toString() {
        char xChar = (char) ('A' + xLocation);
        return "" + xChar + (char) (yLocation + '0');
    }

    public String toLowerCaseString() {
        char xChar = (char) ('a' + xLocation);
        return "" + xChar + (char) (yLocation + '0');
    }

    @Override
    public boolean equals(Object obj) {
        if (this.getXLocation() == ((BoardCoordinate) obj).getXLocation() &&
            this.getYLocation() == ((BoardCoordinate) obj).getYLocation()) {
            return true;
        }
        return false;
    }

    public int getXLocation() {
        return xLocation;
    }

    public int getYLocation() {
        return yLocation;
    }
}
