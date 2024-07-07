package GUI.game;

public class BoardCoordinate {

    // the locations are between 1 and 8!!!
    private final int xLocation;
    private final int yLocation;

    public BoardCoordinate(int x, int y) {
        xLocation = x;
        yLocation = y;
    }
    public BoardCoordinate(BoardCoordinate copy) {
        this.xLocation = copy.getXLocation();
        this.yLocation = copy.getYLocation();
    }
    public BoardCoordinate(String coordinate) {
        if (coordinate.equals("-")) {
            xLocation = -1;
            yLocation = -1;
        } else if (coordinate.length() != 2) {
            //TODO change to useful Error
            throw new RuntimeException("coordinate is not 2 character!");
        } else {
            int row = coordinate.charAt(0);
            if (Character.isDigit(row)) {
                xLocation = Integer.parseInt(coordinate);
            } else {
                if (Character.isLowerCase(row)) {
                    xLocation = Character.getNumericValue(row) - Character.getNumericValue('a') + 1;
                } else {
                    xLocation = Character.getNumericValue(row) - Character.getNumericValue('A') + 1;
                }
            }
            yLocation = Character.getNumericValue(coordinate.charAt(1));
        }
    }

    public int getLocationInt() {
        return xLocation + (yLocation - 1) * 8 - 1;
    }

    @Override
    public String toString() {
        if (xLocation == -1 && yLocation == -1) {
            return "-";
        }
        char xChar = (char) ('@' + xLocation);
        return "" + xChar + (char) (yLocation + '0');
    }

    public String toLowerCaseString() {
        if (xLocation == -1 && yLocation == -1) {
            return "-";
        }
        char xChar = (char) ('`' + xLocation);
        return "" + xChar + (char) (yLocation + '0');
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BoardCoordinate)) {
            throw new IllegalArgumentException("the parameter must be of type BoardCoordinate");
        }
        return this.getXLocation() == ((BoardCoordinate) obj).getXLocation() &&
                this.getYLocation() == ((BoardCoordinate) obj).getYLocation();
    }

    public int getXLocation() {
        return xLocation;
    }

    public int getYLocation() {
        return yLocation;
    }
}
