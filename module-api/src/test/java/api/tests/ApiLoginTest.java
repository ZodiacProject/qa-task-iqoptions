package api.tests;

import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests for login API")
public class ApiLoginTest {
    private static HttpClient client;

    @BeforeAll
    public static void before() {
        client = HttpClientBuilder.create().build();
    }

    private static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of("email", "password", HttpStatus.SC_BAD_REQUEST),
                Arguments.of("zavaxucid@99pubboolicita.com", "302bis", HttpStatus.SC_FORBIDDEN),
                Arguments.of("zavaxucid@99pu~!%**licita.com", "302bis", HttpStatus.SC_BAD_REQUEST),
                Arguments.of("zavaxucid@99pubblicita.com", "302%%$$~bisQW", HttpStatus.SC_FORBIDDEN),
                Arguments.of("zavaxucid@99pubblicita.com", "", HttpStatus.SC_BAD_REQUEST),
                Arguments.of("", "", HttpStatus.SC_BAD_REQUEST),
                Arguments.of(
                        "zavaxucid@99pubblicita.com",
                        "select username,pass from users where username='$uname' and password='$passwrd' limit 0,1",
                        HttpStatus.SC_FORBIDDEN
                )
        );
    }

    @Test
    @DisplayName("Checking positive at login API")
    void checkLoginAPIPositive() throws Throwable {
        HttpResponse response = executeRequest(buildRequest("zavaxucid@99pubblicita.com", "302bis"));
        addResponseMessageToAllure(response.getEntity());
        checkResponseStatus(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @ParameterizedTest(name = "Checking negative at login API: Email: {0}, pass: {1}, status code: {2}")
    @MethodSource("testData")
    void checkLoginAPINegative(String email, String password, int statusCode) throws Throwable {
        HttpResponse response = executeRequest(buildRequest(email, password));
        addResponseMessageToAllure(response.getEntity());
        checkResponseStatus(statusCode, response.getStatusLine().getStatusCode());
    }

    private HttpPost buildRequest(String email, String password) throws Throwable {
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("email", email));
        urlParameters.add(new BasicNameValuePair("password", password));
        HttpPost post = new HttpPost("https://auth.iqoption.com/api/v1.0/login");
        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        return post;
    }

    @Step("Add response message to description")
    private void addResponseMessageToAllure(HttpEntity respEntity) throws Throwable {
        if (respEntity != null) {
            saveSimpleTextLog("Response message", String.valueOf(EntityUtils.toString(respEntity)));
        }
    }

    @Step("Get response from server")
    private HttpResponse executeRequest(HttpPost post) throws Throwable {
        saveSimpleTextLog("Request URI", buildRequestMsg(post));
        return client.execute(post);
    }

    private String buildRequestMsg(HttpPost post) {
        return String.valueOf(post.getURI());
    }

    @Step("Check response status code")
    private void checkResponseStatus(int expectedStatus, int actualStatus) {
        assertEquals(expectedStatus, actualStatus, "Response code:");
    }

    @Attachment(
            value = "{attachName}",
            type = "text/plain"
    )
    public static String saveSimpleTextLog(String attachName, String message) {
        if (message == null) {
            message = "null";
        }
        return message;
    }

}
