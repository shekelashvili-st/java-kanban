package http.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        jsonWriter.value(duration.toString());
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        JsonToken check = jsonReader.peek();
        if (check == JsonToken.NULL) {
            jsonReader.skipValue();
            return Duration.ZERO;
        } else {
            return Duration.parse(jsonReader.nextString());
        }
    }
}
