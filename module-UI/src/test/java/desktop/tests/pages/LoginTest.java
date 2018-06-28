package desktop.tests.pages;

import com.iqoptions.data.entity.role.UserData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

import java.util.stream.Stream;

@DisplayName("Tests for login page")
class LoginTest {

    @BeforeEach
    void before() {
        open("https://iqoption.com/ru/login");
        refresh();
    }

    private static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of("zavaxucid@99pubboolicita.com", "302bis", "Неправильный логин или пароль"),
                Arguments.of("zavaxucid@99pu~!%**licita.com", "302bis", "Неверный e-mail"),
                Arguments.of("zavaxucid@99pubblicita.com", "302%%$$~bisQW", "Неправильный логин или пароль"),
                Arguments.of("zavaxucid@99pubblicita.com", "", "Поле не заполнено"),
                Arguments.of("", "", "Поле не заполнено"),
                Arguments.of("zavaxucid@99pubblicita.com", "select username,pass from users where username='$uname' and password='$passwrd' limit 0,1", "Неправильный логин или пароль")
        );
    }

    @Test
    @DisplayName("Checking positive at login")
    void checkLoginPositive() {
        runLogin(
                new UserData().getEmail(),
                new UserData().getPass()
        );
        $(".SidebarProfile__UserEmail").waitUntil(exist.because("Failed authorization user in the account"), 5000);
    }

    @ParameterizedTest(name = "Checking negative scenario: Email: {0}, pass: {1}, errorMsg: {2}")
    @MethodSource("testData")
    void checkLoginNegative(String email, String pass, String expectedErrorMsg) {
        runLogin(email, pass);
        $(".SidebarLogin").$$("span").findBy(text(expectedErrorMsg))
                .shouldBe(visible.because("Autorization error: not found selector by name " + expectedErrorMsg));
    }

    private void runLogin(String email, String pass) {
        $("input[name='email']").setValue(email);
        $("input[name='password']").setValue(pass);
        $(".SidebarLogin__submit").click();
    }

    @AfterEach
    void tearDown() {
        close();
    }

}

