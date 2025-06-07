package testMock.helpers;

import clientMock.UserClient;
import io.restassured.response.Response;
import modelMock.User;

import static org.mockito.Mockito.*;

public class ResponseMockHelper {

    public static Response mockResponse(int statusCode, String key, String value) {
        Response response = mock(Response.class);
        io.restassured.path.json.JsonPath jsonPath = mock(io.restassured.path.json.JsonPath.class);

        when(response.getStatusCode()).thenReturn(statusCode);
        when(response.jsonPath()).thenReturn(jsonPath);

        if (key != null && !key.isEmpty()) {
            when(jsonPath.getString(key)).thenReturn(value);
            doAnswer(invocation -> {
                System.out.println("Response body: {" + key + ": \"" + value + "\"}");
                return value;
            }).when(jsonPath).getString(key);
        }

        doAnswer(invocation -> {
            System.out.println("Response status code: " + statusCode);
            return statusCode;
        }).when(response).getStatusCode();

        return response;
    }

    public static void resetClient(Object client) {
        reset(client);
    }
    private final UserClient userClient;

    public ResponseMockHelper(UserClient userClient) {
        this.userClient = userClient;
    }
}


