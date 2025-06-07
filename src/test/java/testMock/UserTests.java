// Тесты с вынесенными вспомогательными методами ResponseMockHelper и UserTestHelper
package testMock;

import clientMock.UserClient;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import modelMock.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import testMock.helpers.UserTestHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@Epic("User API Tests")
public class UserTests {

    @Mock
    private UserClient userClient;

    private UserTestHelper helper;

    private final String testUsername = "testuser1109";

    @BeforeEach
    public void setup() {
        userClient = mock(UserClient.class);
        helper = new UserTestHelper(userClient);
    }

    @Test
    @Order(1)
    @Feature("POST /user")
    @DisplayName("Создание пользователя")
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testCreateUser() {
        User user = helper.buildTestUser(testUsername);
        Response response = helper.mockCreateUserResponse(user);
        Response actualResponse = userClient.createUser(user);
        String usernameFromResponse = actualResponse.jsonPath().getString("username");
        assertEquals(user.getUsername(), usernameFromResponse);
        assertEquals(200, actualResponse.getStatusCode());
    }

    @Test
    @Order(2)
    @Feature("GET /user/{username}")
    @DisplayName("Получение пользователя по юзернейм")
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testCreateAndGetUser() {
        User user = helper.buildTestUser(testUsername);
        helper.mockCreateUserResponse(user);
        helper.mockGetUserResponse(testUsername);
        Response createResp = userClient.createUser(user);
        assertEquals(200, createResp.getStatusCode());
        assertEquals(testUsername, createResp.jsonPath().getString("username"));
        Response getResp = userClient.getUser(testUsername);
        assertEquals(200, getResp.getStatusCode());
        assertEquals(testUsername, getResp.jsonPath().getString("username"));
    }

    @Test
    @Order(3)
    @Feature("PUT /user/{username}")
    @DisplayName("Обновление пользователя")
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testUpdateUser() {
        User user = helper.buildTestUser(testUsername);
        helper.mockCreateUserResponse(user);
        helper.mockUpdateUserResponse(testUsername, "Updated");
        Response createResp = userClient.createUser(user);
        assertEquals(200, createResp.getStatusCode());
        assertEquals(testUsername, createResp.jsonPath().getString("username"));
        User updatedUser = helper.buildTestUser(testUsername);
        updatedUser.setFirstName("Updated");
        Response updateResp = userClient.updateUser(testUsername, updatedUser);
        assertEquals(200, updateResp.getStatusCode());
        // Здесь мокируем ответ GET, чтобы вернуть обновлённого пользователя
        helper.mockGetUserResponse1(testUsername, updatedUser);
        Response checkResp = userClient.getUser(testUsername);
        assertEquals("Updated", checkResp.jsonPath().getString("firstName"));
    }

    @Test
    @Order(4)
    @Feature("DELETE /user/{username}")
    @DisplayName("Удаление пользователя")
    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testDeleteUser() {
        User user = helper.buildTestUser(testUsername);
        helper.mockCreateUserResponse(user);
        helper.mockGetUserResponse(testUsername);
        helper.mockDeleteUserResponse(testUsername);
        Response createResp = userClient.createUser(user);
        assertEquals(200, createResp.getStatusCode());
        Response getResp = userClient.getUser(testUsername);
        getResp.jsonPath().getString("username"); // Вывод тела
        Response deleteResp = userClient.deleteUser(testUsername);
        assertEquals(200, deleteResp.getStatusCode());
    }

    @AfterEach
    public void cleanUp() {
        helper.resetMocks();
    }
}


/*
// Тесты без вынесения вспомогательных классов

package testMock;

import clientMock.UserClient;
import io.qameta.allure.*;
import io.restassured.response.Response;
import modelMock.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@Epic("User API Tests")
public class UserTests {

    @Mock
    private UserClient userClient;

    private final String testUsername = "testuser1109";

    private User buildTestUser() {
        return User.builder()
                .id(1109)
                .username(testUsername)
                .firstName("Mark")
                .lastName("Sem")
                .email("test@mail.com")
                .password("password")
                .phone("1234567890")
                .userStatus(0)
                .build();
    }


    // Метод для мокирования Response с возможностью вывести статус и тело

    private Response mockResponse(int statusCode, String key, String value) {
        Response response = mock(Response.class);
        io.restassured.path.json.JsonPath jsonPath = mock(io.restassured.path.json.JsonPath.class);
        when(response.getStatusCode()).thenReturn(statusCode);
        when(response.jsonPath()).thenReturn(jsonPath);
        // Мокируем getString только если ключ не пустой
        if (key != null && !key.isEmpty()) {
            when(jsonPath.getString(key)).thenReturn(value);
            // Вывод тела при вызове jsonPath().getString(key)
            doAnswer(invocation -> {
                System.out.println("Response body: {" + key + ": \"" + value + "\"}");
                return value;
            }).when(jsonPath).getString(key);
        }
        // Вывод кода при вызове getStatusCode()
        doAnswer(invocation -> {
            System.out.println("Response status code: " + statusCode);
            return statusCode;
        }).when(response).getStatusCode();
        return response;
    }

    @Test
    @Order(1)
    @Feature("POST /user")
    @DisplayName("Создание пользователя")
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testCreateUser() {
        User user = buildTestUser();
        // Передаем username в тело для демонстрации вывода
        Response mockedResponse = mockResponse(200, "username", user.getUsername());
        when(userClient.createUser(user)).thenReturn(mockedResponse);
        Response response = userClient.createUser(user);
        // Вызываем getString для вывода тела из мок-ответа
        String usernameFromResponse = response.jsonPath().getString("username");
        assertEquals(user.getUsername(), usernameFromResponse);
        assertEquals(200, response.getStatusCode());
        verify(userClient).createUser(user);
    }

    @Test
    @Order(2)
    @Feature("GET /user/{username}")
    @DisplayName("Получение пользователя по юзернейм")
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testCreateAndGetUser() {
        User user = buildTestUser();
        // Мокируем создание пользователя с телом (username)
        Response createResponse = mockResponse(200, "username", testUsername);
        when(userClient.createUser(user)).thenReturn(createResponse);
        // Мокируем получение пользователя с телом
        Response getResponse = mockResponse(200, "username", testUsername);
        when(userClient.getUser(testUsername)).thenReturn(getResponse);
        // Вызов создания пользователя
        Response responseCreate = userClient.createUser(user);
        assertEquals(200, responseCreate.getStatusCode());
        // ВЫЗОВ, который выводит тело ответа созданного пользователя в консоль,
        // как в тесте testCreateUser()
        String createdUsername = responseCreate.jsonPath().getString("username");
        assertEquals(testUsername, createdUsername);
        // Вызов получения пользователя
        Response responseGet = userClient.getUser(testUsername);
        assertEquals(200, responseGet.getStatusCode());
        assertEquals(testUsername, responseGet.jsonPath().getString("username"));
        verify(userClient).createUser(user);
        verify(userClient).getUser(testUsername);
    }

    @Test
    @Order(3)
    @Feature("PUT /user/{username}")
    @DisplayName("Обновление пользователя")
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testUpdateUser() {
        User user = buildTestUser();
        Response createResponse = mockResponse(200, "", "");
        when(userClient.createUser(user)).thenReturn(createResponse);
        Response createResp = userClient.createUser(user);
        assertEquals(200, createResp.getStatusCode());
        // Сначала возвращаем "созданного" пользователя, потом обновленного
        Response createdUserResponse = mockResponse(200, "username", testUsername);
        Response updatedUserResponse = mockResponse(200, "firstName", "Updated");
        when(userClient.getUser(testUsername))
                .thenReturn(createdUserResponse)
                .thenReturn(updatedUserResponse);
        Response respGetCreatedUser = userClient.getUser(testUsername);
        assertEquals(testUsername, respGetCreatedUser.jsonPath().getString("username"));
        User updatedUser = buildTestUser();
        updatedUser.setFirstName("Updated");
        Response updateResponse = mockResponse(200, "", "");
        when(userClient.updateUser(testUsername, updatedUser)).thenReturn(updateResponse);
        Response response = userClient.updateUser(testUsername, updatedUser);
        assertEquals(200, response.getStatusCode());
        Response check = userClient.getUser(testUsername);
        assertEquals("Updated", check.jsonPath().getString("firstName"));
        verify(userClient).createUser(user);
        verify(userClient, times(2)).getUser(testUsername);
        verify(userClient).updateUser(testUsername, updatedUser);
    }

    @Test
    @Order(4)
    @Feature("DELETE /user/{username}")
    @DisplayName("Удаление пользователя")
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testDeleteUser() {
        User user = buildTestUser();
        Response createResponse = mockResponse(200, "", "");
        when(userClient.createUser(user)).thenReturn(createResponse);
        Response createResp = userClient.createUser(user);
        assertEquals(200, createResp.getStatusCode());
        Response getUserResponse = mockResponse(200, "username", testUsername);
        when(userClient.getUser(testUsername)).thenReturn(getUserResponse);
        Response getResp = userClient.getUser(testUsername);
        getResp.jsonPath().getString("username"); // триггерим вывод тела
        Response deleteResponse = mockResponse(200, "", "");
        when(userClient.deleteUser(testUsername)).thenReturn(deleteResponse);
        Response deleteResp = userClient.deleteUser(testUsername);
        assertEquals(200, deleteResp.getStatusCode());
        verify(userClient).createUser(user);
        verify(userClient).getUser(testUsername);
        verify(userClient).deleteUser(testUsername);
    }

    @AfterEach
    public void cleanUp() {
        reset(userClient); // сбрасываем мок-объект, чтобы не было конфликтов между тестами
    }
}*/



/*
// Более простые тесты, без вывода тела и кода в консоль
package testMock;

import clientMock.UserClient;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import modelMock.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Epic("User API Mocked Tests")
@Feature("UserClient mock tests")
public class UserTests {

    private UserClient userClient;
    private final String testUsername = "testuser123";
    private User testUser;

    @BeforeEach
    public void setUp() {
        userClient = Mockito.mock(UserClient.class);

        testUser = User.builder()
                .id(1L)
                .username(testUsername)
                .firstName("Mark")
                .lastName("Sem")
                .email("test@mail.com")
                .password("password")
                .phone("1234567890")
                .userStatus(0)
                .build();
    }

    @Test
    @DisplayName("Создание пользователя (мок)")
    public void testCreateUser() {
        Response mockedResponse = mock(Response.class);
        when(mockedResponse.getStatusCode()).thenReturn(200);
        when(userClient.createUser(testUser)).thenReturn(mockedResponse);

        Response response = userClient.createUser(testUser);

        assertEquals(200, response.getStatusCode(), "Статус код должен быть 200");
        verify(userClient).createUser(testUser);
    }

    @Test
    @DisplayName("Получение пользователя (мок)")
    public void testGetUser() {
        Response mockedResponse = mock(Response.class);
        JsonPath jsonPath = new JsonPath("{ \"username\": \"" + testUsername + "\" }");

        when(mockedResponse.getStatusCode()).thenReturn(200);
        when(mockedResponse.jsonPath()).thenReturn(jsonPath);
        when(userClient.getUser(testUsername)).thenReturn(mockedResponse);

        Response response = userClient.getUser(testUsername);

        assertEquals(200, response.getStatusCode());
        assertEquals(testUsername, response.jsonPath().getString("username"));
        verify(userClient).getUser(testUsername);
    }

    @Test
    @DisplayName("Обновление пользователя (мок)")
    public void testUpdateUser() {
        User updatedUser = User.builder()
                .id(1L)
                .username(testUsername)
                .firstName("Updated")
                .lastName("Sem")
                .email("test@mail.com")
                .password("password")
                .phone("1234567890")
                .userStatus(0)
                .build();

        // Мок ответа на обновление
        Response updateResponse = mock(Response.class);
        when(updateResponse.getStatusCode()).thenReturn(200);
        when(userClient.updateUser(testUsername, updatedUser)).thenReturn(updateResponse);

        // Мок ответа на получение после обновления
        JsonPath jsonPath = new JsonPath("{ \"firstName\": \"Updated\" }");
        Response getResponse = mock(Response.class);
        when(getResponse.jsonPath()).thenReturn(jsonPath);
        when(userClient.getUser(testUsername)).thenReturn(getResponse);

        // Выполнение
        Response responseUpdate = userClient.updateUser(testUsername, updatedUser);
        assertEquals(200, responseUpdate.getStatusCode());

        Response responseGet = userClient.getUser(testUsername);
        assertEquals("Updated", responseGet.jsonPath().getString("firstName"));

        verify(userClient).updateUser(testUsername, updatedUser);
        verify(userClient).getUser(testUsername);
    }

    @Test
    @DisplayName("Удаление пользователя (мок)")
    public void testDeleteUser() {
        Response mockedResponse = mock(Response.class);
        when(mockedResponse.getStatusCode()).thenReturn(200);
        when(userClient.deleteUser(testUsername)).thenReturn(mockedResponse);

        Response response = userClient.deleteUser(testUsername);

        assertEquals(200, response.getStatusCode());
        verify(userClient).deleteUser(testUsername);
    }
}*/



