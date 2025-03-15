package http.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import manager.tasks.Status;

import java.io.IOException;

public class StatusAdapter extends TypeAdapter<Status> {
    @Override
    public void write(JsonWriter jsonWriter, Status status) throws IOException {
        jsonWriter.value(status.toString());
    }

    @Override
    public Status read(JsonReader jsonReader) throws IOException {
        JsonToken check = jsonReader.peek();
        if (check == JsonToken.NULL) {
            jsonReader.skipValue();
            return Status.NEW;
        } else {
            return Enum.valueOf(Status.class, jsonReader.nextString());
        }
    }
}
