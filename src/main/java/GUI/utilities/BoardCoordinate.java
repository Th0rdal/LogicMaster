package GUI.utilities;

public class BoardCoordinate {

    // the locations are between 1 and 8!!!
    private int xLocation, yLocation;

    public BoardCoordinate(int x, int y) {
        xLocation = x;
        yLocation = y;
    }
    public BoardCoordinate(String coordinate) {
        if (coordinate.length() != 2) {
            //TODO change to useful Error
            throw new RuntimeException("coordinate is not 2 character!");
        }

        char row = coordinate.charAt(0);
        if (Character.isDigit(row)) {
            xLocation = Integer.parseInt(coordinate);
        } else {
            if (Character.isLowerCase(row)) {
                xLocation = Character.getNumericValue(row - 'a');
            } else {
                xLocation = Character.getNumericValue(row - 'A');
            }
        }
        yLocation = Character.getNumericValue(coordinate.charAt(1));
    }

    public int getLocationInt() {
        return xLocation * 10 + yLocation;
    }

    @Override
    public String toString() {
        char xChar = (char) ('@' + xLocation);
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
