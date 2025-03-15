package http.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

public class InstantAdapter extends TypeAdapter<Instant> {
    @Override
    public void write(JsonWriter jsonWriter, Instant instant) throws IOException {
        if (instant != null) {
            jsonWriter.value(instant.toString());
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public Instant read(JsonReader jsonReader) throws IOException {
        JsonToken check = jsonReader.peek();
        if (check == JsonToken.NULL) {
            jsonReader.skipValue();
            return null;
        } else {
            return Instant.parse(jsonReader.nextString());
        }
    }
}
