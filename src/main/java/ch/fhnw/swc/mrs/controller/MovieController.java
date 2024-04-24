package ch.fhnw.swc.mrs.controller;

import static ch.fhnw.swc.mrs.util.JsonUtil.dataToJson;
import static ch.fhnw.swc.mrs.util.JsonUtil.jsonToData;
import static ch.fhnw.swc.mrs.util.RequestUtil.getParamRented;
import static ch.fhnw.swc.mrs.util.RequestUtil.getParamId;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.delete;
import static spark.Spark.halt;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import ch.fhnw.swc.mrs.model.Movie;
import ch.fhnw.swc.mrs.util.JsonUtil;
import ch.fhnw.swc.mrs.util.StatusCodes;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Java Spark controller class to control movie related routes.
 */
public final class MovieController {

    private static MRSServices backend;

    private static Route fetchAllMovies = (Request request, Response response) -> {
        Collection<Movie> movies;
        String rented = getParamRented(request);
        if (rented != null && !rented.isEmpty()) {
            movies = backend.getAllMovies("true".equals(rented));
        } else {
            movies = backend.getAllMovies();
        }
        return dataToJson(movies);
    };

    private static Route fetchOneMovie = (Request request, Response response) -> {
        long id = getParamId(request);
        Movie m = backend.getMovieById(id);
        String body = "";
        if (m == null) {
            response.status(StatusCodes.NOT_FOUND);
        } else {
            response.status(StatusCodes.OK);
            body = dataToJson(m);
        }
        return body;
    };

    private static Route deleteMovie = (Request request, Response response) -> {
        long id = getParamId(request);
        if (backend.deleteMovie(id)) {
            response.status(StatusCodes.NO_CONTENT);
        } else {
            response.status(StatusCodes.NOT_FOUND);
        }
        return "";
    };

    private static Route createMovie = (Request request, Response response) -> {
        String json = request.body();

        Movie newMovie = (Movie) jsonToData(json, Movie.class);
        String body = "";
        try {
            Movie m = backend.createMovie(newMovie.getTitle(), 
                    newMovie.getReleaseDate(), 
                    newMovie.getAgeRating());
            body = dataToJson(m);
            response.status(StatusCodes.CREATED);
        } catch (Exception e) {
            halt(StatusCodes.NOT_FOUND, e.getMessage());
        }
        return body;
    };

    private static Route updateMovie = (Request request, Response response) -> {
        long id = getParamId(request);
        String json = request.body();
        Movie m = (Movie) jsonToData(json, Movie.class);
        if (id != m.getMovieid()) {
            halt(StatusCodes.BAD_REQUEST, "request id does not correspond with movie id");
        }
        if (!backend.updateMovie(m)) {
            halt(StatusCodes.BAD_REQUEST, "update could not be processed.");
        }
        return dataToJson(m);
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
        JsonUtil.registerSerializer(new MovieSerializer());
        JsonUtil.registerDeserializer(Movie.class, new MovieDeserializer());
        

        get("/movies", MovieController.fetchAllMovies);
        get("/movies/:id", MovieController.fetchOneMovie);
        delete("/movies/:id", MovieController.deleteMovie);
        post("/movies", MovieController.createMovie);
        put("/movies/:id", MovieController.updateMovie);
    }

    // prevent instantiation
    private MovieController() {
    }

    /**
     * Helper class to serialize the Movie Object to json
     */
    @SuppressWarnings("serial")
    private static class MovieSerializer extends StdSerializer<Movie> {

        MovieSerializer() {
            super(Movie.class);
        }

        @Override
        public void serialize(Movie m, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartObject();
            jgen.writeNumberField("id", m.getMovieid());
            jgen.writeBooleanField("rented", m.isRented());
            jgen.writeStringField("title", m.getTitle());
            jgen.writeStringField("releaseDate", m.getReleaseDate().format(DateTimeFormatter.ISO_DATE));
            jgen.writeNumberField("ageRating", m.getAgeRating());
            jgen.writeEndObject();
        }
    }
    
    /**
     * Helper class to deserialize the Movie Object from json
     */
    private static class MovieDeserializer extends StdDeserializer<Movie> {
        
        protected MovieDeserializer(Class<?> vc) {
            super(vc);
        }

        private static final long serialVersionUID = 1L;

        MovieDeserializer() { 
            this(null); 
        }

        @Override
        public Movie deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
            JsonNode node = jp.getCodec().readTree(jp);
            String title = node.get("title").asText();
            String released = node.get("releaseDate").asText();
            LocalDate releaseDate = LocalDate.parse(released, DateTimeFormatter.ISO_DATE);
            int ageRating = node.get("ageRating").asInt();
            Movie m = new Movie(title, releaseDate, ageRating);
            m.setMovieId(node.get("id").asLong());
            return m;
        }
    }
    
}
