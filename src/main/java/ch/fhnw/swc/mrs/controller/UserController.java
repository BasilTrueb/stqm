package ch.fhnw.swc.mrs.controller;

import static ch.fhnw.swc.mrs.util.JsonUtil.dataToJson;
import static ch.fhnw.swc.mrs.util.JsonUtil.jsonToData;
import static ch.fhnw.swc.mrs.util.RequestUtil.getParamName;
import static ch.fhnw.swc.mrs.util.RequestUtil.getParamId;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.halt;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ch.fhnw.swc.mrs.api.MRSServices;
import ch.fhnw.swc.mrs.model.User;
import ch.fhnw.swc.mrs.util.JsonUtil;
import ch.fhnw.swc.mrs.util.StatusCodes;
import spark.Request;
import spark.Response;
import spark.Route;


/**
 * Java Spark controller class to control user related routes.
 */
public final class UserController {

    private static MRSServices backend;

    private static Route fetchAllUsers = (Request request, Response response) -> {
        Collection<User> users;
        String name = getParamName(request);
        if (name != null && !name.isEmpty()) {
            users = new ArrayList<User>(1);
            users.add(backend.getUserByName(name));
        } else {
            users = backend.getAllUsers();
        }
        return dataToJson(users);
    };

    private static Route fetchOneUser = (Request request, Response response) -> {
        long id = getParamId(request);
        User u = backend.getUserById(id);
        String body = "";
        if (u == null) {
            response.status(StatusCodes.NOT_FOUND);
        } else {
            response.status(StatusCodes.OK);
            body = dataToJson(u);
        }
        return body;
    };

    private static Route deleteUser = (Request request, Response response) -> {
        long id = getParamId(request);
        if (backend.deleteUser(id)) {
            response.status(StatusCodes.NO_CONTENT);
        } else {
            response.status(StatusCodes.NOT_FOUND);
        }
        return "";
    };

    private static Route createUser = (Request request, Response response) -> {
        
        String json = request.body();

        User newUser = (User) jsonToData(json, User.class);
        String body = "";
        try {
            User u = backend.createUser(newUser.getName(), newUser.getFirstName(), newUser.getBirthdate());
            body = dataToJson(u);
            response.status(StatusCodes.CREATED);
        } catch (Exception e) {
            halt(StatusCodes.NOT_FOUND, e.getMessage());
        }
        return body;
    };

    private static Route updateUser = (Request request, Response response) -> {
        // get the id from the route param
        long id = getParamId(request);
        
        // get the new data from the body
        String json = request.body();
        User u = (User) jsonToData(json, User.class);
        
        if (id != u.getUserid()) {
            halt(StatusCodes.BAD_REQUEST, "request id does not correspond with user id");
        }
        if (!backend.updateUser(u)) {
            halt(StatusCodes.BAD_REQUEST, "update could not be processed.");
        }
        return dataToJson(u);
    };

    /**
     * Initialize MovieController by registering back-end and routes.
     * 
     * @param services the back-end component.
     */
    public static void init(MRSServices services) {
        if (services == null) {
            throw new IllegalArgumentException("Backend component missing");
        }
        backend = services;
        JsonUtil.registerSerializer(new UserSerializer());
        JsonUtil.registerDeserializer(User.class, new UserDeserializer());

        get("/users", UserController.fetchAllUsers);
        get("/users/:id", UserController.fetchOneUser);
        delete("/users/:id", UserController.deleteUser);
        post("/users", UserController.createUser);
        put("/users/:id", UserController.updateUser);
    }

    // prevent instantiation
    private UserController() {
    }

    /**
     * Helper class to serialize the User Object to json
     */
    @SuppressWarnings("serial")
    private static class UserSerializer extends StdSerializer<User> {

        UserSerializer() {
            super(User.class);
        }

        @Override
        public void serialize(User u, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartObject();
            jgen.writeNumberField("id", u.getUserid());
            jgen.writeStringField("name", u.getName());
            jgen.writeStringField("firstname", u.getFirstName());
            jgen.writeStringField("birthDate", u.getBirthdate().format(DateTimeFormatter.ISO_DATE));
            jgen.writeEndObject();
        }

    }
    
    /**
     * Helper class to deserialize the User Object from json
     */
    private static class UserDeserializer extends StdDeserializer<User> {
        
        protected UserDeserializer(Class<?> vc) {
            super(vc);
        }

        private static final long serialVersionUID = 1L;

        UserDeserializer() { 
            this(null); 
        }

        @Override
        public User deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
            JsonNode node = jp.getCodec().readTree(jp);
            String name = node.get("name").asText();
            String firstname = node.get("firstname").asText();
            String birthdate = node.get("birthDate").asText();
            LocalDate dateOfBirth = LocalDate.parse(birthdate, DateTimeFormatter.ISO_DATE);
            User u = new User(name, firstname, dateOfBirth);
            if (node.get("id") != null) {
                long id = node.get("id").asLong();
                u.setUserId(id);
            }
            return u;
        }
    }
    
}
