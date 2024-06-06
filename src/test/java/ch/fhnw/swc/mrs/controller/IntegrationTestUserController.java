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
class ITUserController {

    private String baseUrl = "http://localhost:";

    @BeforeAll
    static void startApplication() throws Exception {
        Application.main(null);
    }

    @BeforeEach
    void setPort() throws Exception {
        baseUrl = baseUrl + Application.getPort();
    }

    @DisplayName("Get a user by its id.")
    @Test
    void testGetUserById() {
        given().contentType("application/json").when()
                .get(baseUrl + "/users/{id}", "4").then().statusCode(200)
                .body("name", equalTo("Müller"));
    }

    @DisplayName("Delete user")
    @Test
    void testDeleteUser() {
        String json = get(baseUrl + "/users").asString();
        int elementsBefore = new JsonPath(json).getInt("size()");

        given().when().delete(baseUrl + "/users/5").then()
                .statusCode(StatusCodes.NO_CONTENT);

        json = get(baseUrl + "/users").asString();
        int elementsAfter = new JsonPath(json).getInt("size()");
        assertEquals(elementsBefore, elementsAfter + 1);
    }

    @DisplayName("Create user")
    @Test
    void testCreateUser() {
        String bodyContent = """
                {\r
                        "name": "Denzler",\r
                        "birthDate" : "1968-07-20",\r
                        "firstname" : "Christoph"\r
                    }""";

        String json1 = get(baseUrl + "/users").asString();
        int elementsBefore = new JsonPath(json1).getInt("size()");

        given().
                body(bodyContent)
                .when()
                .post(baseUrl + "/users")
                .then()
                .statusCode(StatusCodes.CREATED)
                .body("name", equalTo("Denzler"))
                .body("$", hasKey("id")); // verify that the id of the new user is returned

        String json2 = get(baseUrl + "/users").asString();
        int elementsAfter = new JsonPath(json2).getInt("size()");
        assertEquals(elementsBefore, elementsAfter - 1);
    }

    @DisplayName("Update user")
    @Test
    void testUpdateUser() {
        String body = """
                {\r
                        "id": "6",\r
                        "name": "Meier",\r
                        "firstname": "Katrin",\r
                        "birthDate": "2017-06-27"\r
                    }""";
        String json = get(baseUrl + "/users").asString();
        int elementsBefore = new JsonPath(json).getInt("size()");

        given().body(body).when().put(baseUrl + "/users/6").then()
                .statusCode(StatusCodes.OK).body("name", equalTo("Meier"));

        json = get(baseUrl + "/users").asString();
        int elementsAfter = new JsonPath(json).getInt("size()");
        assertEquals(elementsBefore, elementsAfter);
    }
    @AfterAll
    static void stopSpark() throws Exception {
        Application.stop();
        Thread.sleep(1000);
    }

}
