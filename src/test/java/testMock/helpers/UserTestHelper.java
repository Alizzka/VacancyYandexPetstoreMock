package testMock.helpers;

import clientMock.UserClient;
import io.restassured.response.Response;
import modelMock.User;

import static org.mockito.Mockito.*;

public class UserTestHelper {

    private final UserClient userClient;

    public UserTestHelper(UserClient userClient) {
        this.userClient = userClient;
    }

    public User buildTestUser(String username) {
        return User.builder()
                .id(1109)
                .username(username)
                .firstName("Mark")
                .lastName("Sem")
                .email("test@mail.com")
                .password("password")
                .phone("1234567890")
                .userStatus(0)
                .build();
    }

    public Response mockCreateUserResponse(User user) {
        Response mockedResponse = ResponseMockHelper.mockResponse(200, "username", user.getUsername());
        when(userClient.createUser(user)).thenReturn(mockedResponse);
        return mockedResponse;
    }

    public Response mockGetUserResponse(String username) {
        Response mockedResponse = ResponseMockHelper.mockResponse(200, "username", username);
        when(userClient.getUser(username)).thenReturn(mockedResponse);
        return mockedResponse;
    }

    public Response mockGetUserResponse1(String username, User user) {
        Response mockedResponse = ResponseMockHelper.mockResponse(200, "firstName", user.getFirstName());
        when(userClient.getUser(username)).thenReturn(mockedResponse);
        return mockedResponse;
    }

    public Response mockUpdateUserResponse(String username, String updatedFirstName) {
        Response createdUserResponse = ResponseMockHelper.mockResponse(200, "username", username);
        Response updatedUserResponse = ResponseMockHelper.mockResponse(200, "firstName", updatedFirstName);
        when(userClient.getUser(username))
                .thenReturn(createdUserResponse)
                .thenReturn(updatedUserResponse);

        Response updateResponse = ResponseMockHelper.mockResponse(200, "", "");
        when(userClient.updateUser(eq(username), any(User.class))).thenReturn(updateResponse);
        return updateResponse;
    }

    public Response mockDeleteUserResponse(String username) {
        Response deleteResponse = ResponseMockHelper.mockResponse(200, "", "");
        when(userClient.deleteUser(username)).thenReturn(deleteResponse);
        return deleteResponse;
    }

    public void resetMocks() {
        ResponseMockHelper.resetClient(userClient);
    }
}

