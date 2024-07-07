package GUI.game.timecontrol;

import com.google.gson.*;

import java.lang.reflect.Type;

public class TimecontrolSerializer implements JsonSerializer<Timecontrol> {

    @Override
    public JsonElement serialize(Timecontrol src, Type typeOfSrc, JsonSerializationContext context) {
        // Serialize Timecontrol to a string representation
        String serialized = src.toString(); // Assuming toString() provides the desired string representation

        return new JsonPrimitive(serialized); // Convert string to JsonElement
    }

}