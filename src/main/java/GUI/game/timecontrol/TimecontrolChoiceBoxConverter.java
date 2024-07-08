package GUI.game.timecontrol;

import javafx.util.StringConverter;

public class TimecontrolChoiceBoxConverter extends StringConverter<Timecontrol> {

    @Override
    public String toString(Timecontrol object) {
        if (object.isActive()) {
            return object.toString();
        } else {
            return "custom";
        }
    }

    @Override
    public Timecontrol fromString(String string) {
        return null;
    }

}
