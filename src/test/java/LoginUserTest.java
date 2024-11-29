import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class LoginUserTest {
    Faker faker = new Faker();
    private String email = faker.internet().emailAddress();
    private String name = faker.name().username();
    private String password = faker.internet().password(3, 10);
    private String passwordFake = faker.internet().password(2, 5);
    private String emailFake = faker.internet().emailAddress();
    private UserClient userClient = new UserClient();
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Логин юзера:логин под существующим пользователем")

    public void createUserAndLoginTest() {
        User user = new User(email, name, password);
        Response responseCreate = userClient.createUniqueUser(user);
        Assert.assertEquals("true", responseCreate.jsonPath().getString("success"));
        Response loginResponse = userClient.loginUser(new User(user.getEmail(), user.getName(), user.getPassword()));
        Assert.assertEquals("true", loginResponse.jsonPath().getString("success"));
        Assert.assertEquals(200, loginResponse.statusCode());
        accessToken = responseCreate.jsonPath().getString("accessToken");

    }

    @Test
    @DisplayName("Логин юзера:логин с неверным паролем")

    public void createUserAndLoginWithIncorrectPasswordTest() {
        User user = new User(email, name, password);
        Response responseCreate = userClient.createUniqueUser(user);
        Assert.assertEquals("true", responseCreate.jsonPath().getString("success"));
        Response loginResponseIncorrectPassword = userClient.loginUser(new User(user.getEmail(), user.getName(), passwordFake));
        Assert.assertEquals("false", loginResponseIncorrectPassword.jsonPath().getString("success"));
        Assert.assertEquals("email or password are incorrect", loginResponseIncorrectPassword.jsonPath().getString("message"));
        Assert.assertEquals(401, loginResponseIncorrectPassword.statusCode());
        accessToken = responseCreate.jsonPath().getString("accessToken");

    }

    @Test
    @DisplayName("Логин юзера:логин с неверным email")

    public void createUserAndLoginWithIncorrectEmailTest() {
        User user = new User(email, name, password);
        Response responseCreate = userClient.createUniqueUser(user);
        Assert.assertEquals("true", responseCreate.jsonPath().getString("success"));
        Response loginResponseIncorrectEmail = userClient.loginUser(new User(emailFake, user.getName(), user.getPassword()));
        Assert.assertEquals("false", loginResponseIncorrectEmail.jsonPath().getString("success"));
        Assert.assertEquals("email or password are incorrect", loginResponseIncorrectEmail.jsonPath().getString("message"));
        Assert.assertEquals(401, loginResponseIncorrectEmail.statusCode());
        accessToken = responseCreate.jsonPath().getString("accessToken");
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken);

    }
}
