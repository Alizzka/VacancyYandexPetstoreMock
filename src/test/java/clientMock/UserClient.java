package clientMock;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;

import modelMock.User;

public class UserClient {
    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    @Step("Создание пользователя")
    public Response createUser(User user) {
        return given()
                .baseUri(BASE_URL)
                .contentType(JSON)
                .body(user)
                .when()
                .post("/user")
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Получение пользователя по username")
    public Response getUser(String username) {
        return given()
                .baseUri(BASE_URL)
                .when()
                .get("/user/" + username)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Обновление пользователя")
    public Response updateUser(String username, User user) {
        return given()
                .baseUri(BASE_URL)
                .contentType(JSON)
                .body(user)
                .when()
                .put("/user/" + username)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String username) {
        return given()
                .baseUri(BASE_URL)
                .when()
                .delete("/user/" + username)
                .then()
                .log().all()
                .extract().response();
    }
}

