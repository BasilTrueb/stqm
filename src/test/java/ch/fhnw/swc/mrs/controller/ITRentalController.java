package ch.fhnw.swc.mrs.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import ch.fhnw.swc.mrs.Application;
import ch.fhnw.swc.mrs.util.StatusCodes;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

@Tag("integration")
class ITRentalController {

    private String baseUrl = "http://localhost:";

    @BeforeAll
    static void startApplication() throws Exception {
        Application.main(null);
    }

    @BeforeEach
    void setPort() throws Exception {
        baseUrl = baseUrl + Application.getPort();
    }

    @DisplayName("Get a rental by its id.")
    @Test
    void testGetRentalById() {
        given().contentType("application/json").when()
                .get(baseUrl + "/rentals/{id}", "1").then().statusCode(200)
                .body("userId", equalTo(1))
                .body("movieId", equalTo(1));
    }

    @DisplayName("Delete rental")
    @Test
    void testDeleteRental() {
        String json = get(baseUrl + "/rentals").asString();
        int elementsBefore = new JsonPath(json).getInt("size()");

        given().when().delete(baseUrl + "/rentals/1").then()
                .statusCode(StatusCodes.NO_CONTENT);

        json = get(baseUrl + "/rentals").asString();
        int elementsAfter = new JsonPath(json).getInt("size()");
        assertEquals(elementsBefore, elementsAfter + 1);
    }

    @DisplayName("Create rental")
    @Test
    void testCreateRental() {
        String bodyContent = """
                {\r
                        "userId": 1,\r
                        "movieId": 1,\r
                        "rentalDate" : "2023-01-01"\r
                    }""";

        String json1 = get(baseUrl + "/rentals").asString();
        int elementsBefore = new JsonPath(json1).getInt("size()");

        given().
                contentType("application/json").
                body(bodyContent)
                .when()
                .post(baseUrl + "/rentals")
                .then()
                .statusCode(StatusCodes.CREATED)
                .body("userId", equalTo(1))
                .body("movieId", equalTo(1))
                .body("$", hasKey("id")); // verify that the id of the new rental is returned

        String json2 = get(baseUrl + "/rentals").asString();
        int elementsAfter = new JsonPath(json2).getInt("size()");
        assertEquals(elementsBefore, elementsAfter - 1);
    }

    @DisplayName("Update rental")
    @Test
    void testUpdateRental() {
        String body = """
                {\r
                        "id": 1,\r
                        "userId": 1,\r
                        "movieId": 2,\r
                        "rentalDate": "2023-01-01"\r
                    }""";
        String json = get(baseUrl + "/rentals").asString();
        int elementsBefore = new JsonPath(json).getInt("size()");

        given().
                contentType("application/json").
                body(body).
                when().
                put(baseUrl + "/rentals/1").
                then().
                statusCode(StatusCodes.OK).
                body("movieId", equalTo(2));

        json = get(baseUrl + "/rentals").asString();
        int elementsAfter = new JsonPath(json).getInt("size()");
        assertEquals(elementsBefore, elementsAfter);
    }

    @AfterAll
    static void stopSpark() throws Exception {
        Application.stop();
        Thread.sleep(1000);
    }
}
