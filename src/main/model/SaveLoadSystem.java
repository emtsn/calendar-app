package model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import model.deserializers.ColorDeserializer;
import model.serializers.ColorSerializer;
import model.serializers.LocalDateSerializer;
import model.serializers.LocalTimeSerializer;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;

public interface SaveLoadSystem {
    boolean PRINT_PATH = true;
    boolean PRINT_DATA_ON_SAVE = false;

    // EFFECTS: saves saveable onto file
    static <T> void saveWithJackson(T saveable, String fileName) throws IOException {
        if (PRINT_PATH) {
            System.out.println("Attempting to save to: " + fileName);
        }
        if (fileName.isEmpty()) {
            throw new IOException("File Name is empty");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        simpleModule.addSerializer(LocalTime.class, new LocalTimeSerializer());
        simpleModule.addSerializer(Color.class, new ColorSerializer());
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.registerModule(simpleModule);
        String jsonText = objectMapper.writeValueAsString(saveable);
        if (PRINT_DATA_ON_SAVE) {
            System.out.println(jsonText);
        }
        objectMapper.writeValue(new File(fileName), saveable);
    }

    // EFFECTS: loads object from file in path fileName
    static <T> T loadWithJackson(String fileName, Class<T> valueType) throws IOException {
        if (PRINT_PATH) {
            System.out.println("Attempting to load from: " + fileName);
        }
        if (fileName.isEmpty()) {
            throw new IOException("File Name is empty");
        }
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Color.class, new ColorDeserializer());
            objectMapper.registerModule(module);
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(fileInputStream, valueType);
        }
    }

    // MODIFIES: this
    // EFFECTS: saves data to file, returns true if successful
    boolean save();

    // MODIFIES: this
    // EFFECTS: loads data from file, returns true if successful
    boolean load();
}
