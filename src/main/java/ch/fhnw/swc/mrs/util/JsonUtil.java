package ch.fhnw.swc.mrs.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Utility class for transforming Java objects to json strings and vice versa.
 */
public final class JsonUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * Register a serializer with the mapper.
     * 
     * @param serializer the custom serializer.
     */
    public static void registerSerializer(StdSerializer<?> serializer) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(serializer);
        mapper.registerModule(module);
    }

    /**
     * Register a deserializer with the mapper.
     * 
     * @param <T> the type to which to map the json object.
     * @param valueType the class type to which to map the json object.
     * @param deserializer the custom deserializer.
     */
    public static <T> void registerDeserializer(Class<T> valueType, StdDeserializer<? extends T> deserializer) {
        SimpleModule module = new SimpleModule();
        
        module.addDeserializer(valueType, deserializer);
        mapper.registerModule(module);
    }

    
    /**
     * Convert DTO to JSON.
     * 
     * @param data the dto.
     * @return a JSON String.
     */
    public static String dataToJson(Object data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("IOEXception while mapping object (" + data + ") to JSON", e);
        }
    }

    /**
     * Convert JSON to DTO.
     * @param data data from which the data to be deserialized.
     * @param valueType the data type into which convert the data.
     * @return the data in an generic object.
     */
    public static Object jsonToData(String data, Class<?> valueType) {
        try {
            return mapper.readValue(data, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("IOEXception while mapping json (" + data + ") to value type", e);
        }
    }    
    
    // prevent instantiation
    private JsonUtil() {
    }
}
