package ch.fhnw.swc.mrs.controller;

import static ch.fhnw.swc.mrs.util.JsonUtil.dataToJson;
import static ch.fhnw.swc.mrs.util.JsonUtil.jsonToData;
import static ch.fhnw.swc.mrs.util.RequestUtil.getParamId;
import static spark.Spark.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import ch.fhnw.swc.mrs.util.JsonUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ch.fhnw.swc.mrs.api.MRSServices;
import ch.fhnw.swc.mrs.model.Rental;
import ch.fhnw.swc.mrs.util.StatusCodes;
import spark.Request;
import spark.Response;
import spark.Route;

public final class RentalController {
    private static MRSServices backend;
    private static final Route FETCH_ALL_RENTALS = (Request request, Response response) -> {
        Collection<Rental> rentals = backend.getAllRentals();
        return dataToJson(rentals);
    };

    private static final Route CREATE_RENTAL = (Request request, Response response) -> {
        Rental rental = (Rental) jsonToData(request.body(), Rental.class);
        Rental createdRental = backend.createRental(rental.getUser().getUserid(), rental.getMovie().getMovieid(),
                rental.getRentalDate());
        if (createdRental == null) {
            halt(StatusCodes.BAD_REQUEST);
        }
        response.status(StatusCodes.CREATED);
        return dataToJson(createdRental);
    };

    private static final Route DELETE_RENTAL = (Request request, Response response) -> {
        long id = getParamId(request);
        if (backend.deleteRental(id)) {
            response.status(StatusCodes.NO_CONTENT);
        } else {
            response.status(StatusCodes.NOT_FOUND);
        }
        return "";
    };

    /**
     * Initialize RentalController by registering back-end and routes.
     *
     * @param services the back-end component.
     */
    public static void init(MRSServices services) {
        if (services == null) {
            throw new IllegalArgumentException("Backend component missing");
        }
        backend = services;
        JsonUtil.registerSerializer(new RentalSerializer());
        JsonUtil.registerDeserializer(Rental.class, new RentalDeserializer());

        post("/rentals", RentalController.CREATE_RENTAL);
        delete("/rentals/:id", RentalController.DELETE_RENTAL);
        get("/rentals", RentalController.FETCH_ALL_RENTALS);
    }

    // Prevent instantiation
    private RentalController() {
    }

    /**
     * Helper class to serialize the Rental object to JSON
     */
    private static class RentalSerializer extends StdSerializer<Rental> {
        protected RentalSerializer() {
            super(Rental.class);
        }

        @Override
        public void serialize(Rental rental, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartObject();
            jgen.writeNumberField("id", rental.getRentalId());
            jgen.writeNumberField("userId", rental.getUser().getUserid());
            jgen.writeNumberField("movieId", rental.getMovie().getMovieid());
            jgen.writeStringField("rentalDate", rental.getRentalDate().format(DateTimeFormatter.ISO_DATE));
            jgen.writeEndObject();
        }
    }

    /**
     * Helper class to deserialize the Rental object from JSON
     */
    private static class RentalDeserializer extends StdDeserializer<Rental> {
        protected RentalDeserializer() {
            super(Rental.class);
        }

        @Override
        public Rental deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            JsonNode node = jp.getCodec().readTree(jp);
            long userId = node.get("userId").asLong();
            long movieId = node.get("movieId").asLong();
            LocalDate rentalDate = LocalDate.parse(node.get("rentalDate").asText(), DateTimeFormatter.ISO_DATE);
            return new Rental(backend.getUserById(userId), backend.getMovieById(movieId), rentalDate);
        }
    }
}
