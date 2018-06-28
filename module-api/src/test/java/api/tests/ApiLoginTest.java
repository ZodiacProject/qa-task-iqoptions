package api.tests;
import com.iqoptions.data.entity.role.UserData;
import io.qameta.allure.Allure;
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
    private HttpClient client;
    private HttpPost post;
    private List<NameValuePair> urlParameters;

    @BeforeEach
    void before() {
        client = HttpClientBuilder.create().build();
        post = new HttpPost(new UserData().getUrl());
        urlParameters = new ArrayList<>();
    }

    private static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of("email", "password", 400),
                Arguments.of("zavaxucid@99pubboolicita.com", "302bis", 403),
                Arguments.of("zavaxucid@99pu~!%**licita.com", "302bis", 400),
                Arguments.of("zavaxucid@99pubblicita.com", "302%%$$~bisQW", 403),
                Arguments.of("zavaxucid@99pubblicita.com", "", 400),
                Arguments.of("", "", 400),
                Arguments.of(
                        "zavaxucid@99pubblicita.com",
                        "select username,pass from users where username='$uname' and password='$passwrd' limit 0,1",
                        403
                )
        );
    }

    @Test
    @DisplayName("Checking positive at login API")
    void checkLoginAPIPositive() throws Throwable {
        urlParameters.add(new BasicNameValuePair("email", new UserData().getEmail()));
        urlParameters.add(new BasicNameValuePair("password", new UserData().getPass()));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = getResponse(post);

        addResponseMessageToAllure(response.getEntity());

        checkResponseStatus(200, response.getStatusLine().getStatusCode());
    }

    @ParameterizedTest(name = "Checking negative at login API: Email: {0}, pass: {1}, status code: {2}")
    @MethodSource("testData")
    void checkLoginAPINegative(String email, String pass, int statusCode) throws Throwable {
        urlParameters.add(new BasicNameValuePair("email", email));
        urlParameters.add(new BasicNameValuePair("password", pass));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = getResponse(post);

        addResponseMessageToAllure(response.getEntity());

        checkResponseStatus(statusCode, response.getStatusLine().getStatusCode());
    }

    private void addResponseMessageToAllure(HttpEntity respEntity) throws Throwable {
        if (respEntity != null) {
            Allure.addDescription(EntityUtils.toString(respEntity));
        }
    }

    private HttpResponse getResponse(HttpPost post) throws Throwable {
        return client.execute(post);
    }

    private void checkResponseStatus(int expectedStatus, int actualStatus) {
        System.out.println("Response actual code: " + actualStatus);
        assertEquals(expectedStatus, actualStatus, "Response code: " + expectedStatus);
    }

}
